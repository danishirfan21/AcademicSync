package com.danish.academicsync.sync;

import com.danish.academicsync.course.Course;
import com.danish.academicsync.course.CourseRepository;
import com.danish.academicsync.integration.SisClient;
import com.danish.academicsync.mock.dto.MockCourseDto;
import com.danish.academicsync.mock.dto.MockStudentDto;
import com.danish.academicsync.student.Student;
import com.danish.academicsync.student.StudentRepository;
import com.danish.academicsync.sync.dto.SyncResultResponse;

import academicsync.src.main.java.com.danish.academicsync.messaging.SyncEventPublisher;
import academicsync.src.main.java.com.danish.academicsync.observability.SyncMetricsService;
import academicsync.src.main.java.com.danish.academicsync.report.dto.SyncRunRepository;
import lombok.RequiredArgsConstructor;
import main.java.com.danish.academicsync.enrollment.EnrollmentRepository;
import main.java.com.danish.academicsync.sync.dto.RetryResultResponse;
import main.java.com.danish.academicsync.sync.dto.SyncErrorResponse;
import main.java.com.danish.academicsync.sync.dto.SyncRunResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import com.danish.academicsync.enrollment.Enrollment;
import com.danish.academicsync.enrollment.EnrollmentRepository;
import com.danish.academicsync.mock.dto.MockEnrollmentDto;

import com.danish.academicsync.mock.dto.MockEnrollmentDto;
import com.danish.academicsync.observability.SyncMetricsService;
import com.danish.academicsync.messaging.SyncEventPublisher;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SisClient sisClient;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SyncRunRepository syncRunRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SyncErrorRepository syncErrorRepository;
    private final SyncMetricsService syncMetricsService;
    private final SyncEventPublisher syncEventPublisher;

    @Transactional
    public SyncResultResponse syncStudents() {
        SyncRun run = startRun(SyncType.STUDENTS);

        int processed = 0;
        int created = 0;
        int updated = 0;
        int failed = 0;

        try {
            List<MockStudentDto> students = sisClient.fetchStudents();

            for (MockStudentDto dto : students) {
                processed++;

                if (isBlank(dto.externalStudentId()) || isBlank(dto.fullName()) || isBlank(dto.email())) {
                    failed++;
                    logSyncError(
                            run,
                            dto.externalStudentId(),
                            "STUDENT",
                            "Missing required student fields",
                            dto.toString()
                    );
                    continue;
                }

                Student student = studentRepository
                        .findByExternalStudentId(dto.externalStudentId())
                        .orElseGet(() -> {
                            Student s = new Student();
                            s.setExternalStudentId(dto.externalStudentId());
                            return s;
                        });

                boolean isNew = student.getId() == null;

                student.setFullName(dto.fullName());
                student.setEmail(dto.email());
                student.setProgram(dto.program());
                student.setAcademicStatus(dto.academicStatus());
                student.setAdmissionTerm(dto.admissionTerm());
                student.setLastSyncedAt(Instant.now());

                studentRepository.save(student);

                if (isNew) {
                    created++;
                } else {
                    updated++;
                }
            }

            completeRun(run, processed, created, updated, failed);

        } catch (Exception ex) {
            failRun(run, processed, created, updated, failed, ex.getMessage());
        }

        SyncResultResponse response = toResponse(run);
        syncMetricsService.recordSyncResult(response);
        syncEventPublisher.publishSyncCompleted(response);
        return response;
    }

    @Transactional
    public SyncResultResponse syncCourses() {
        SyncRun run = startRun(SyncType.COURSES);

        int processed = 0;
        int created = 0;
        int updated = 0;
        int failed = 0;

        try {
            List<MockCourseDto> courses = sisClient.fetchCourses();

            for (MockCourseDto dto : courses) {
                processed++;

                if (isBlank(dto.courseCode()) || isBlank(dto.title())) {
                    failed++;
                    logSyncError(
                            run,
                            dto.courseCode(),
                            "COURSE",
                            "Missing required course fields",
                            dto.toString()
                    );
                    continue;
                }

                Course course = courseRepository
                        .findByCourseCode(dto.courseCode())
                        .orElseGet(() -> {
                            Course c = new Course();
                            c.setCourseCode(dto.courseCode());
                            return c;
                        });

                boolean isNew = course.getId() == null;

                course.setTitle(dto.title());
                course.setDepartment(dto.department());
                course.setCreditHours(dto.creditHours());
                course.setActive(Boolean.TRUE.equals(dto.active()));
                course.setLastSyncedAt(Instant.now());

                courseRepository.save(course);

                if (isNew) {
                    created++;
                } else {
                    updated++;
                }
            }

            completeRun(run, processed, created, updated, failed);

        } catch (Exception ex) {
            failRun(run, processed, created, updated, failed, ex.getMessage());
        }

        SyncResultResponse response = toResponse(run);
        syncMetricsService.recordSyncResult(response);
        syncEventPublisher.publishSyncCompleted(response);
        return response;
    }

    private SyncRun startRun(SyncType type) {
        SyncRun run = new SyncRun();
        run.setSyncType(type.name());
        run.setStatus(SyncStatus.RUNNING.name());
        run.setStartedAt(Instant.now());
        return syncRunRepository.save(run);
    }

    private void completeRun(
            SyncRun run,
            int processed,
            int created,
            int updated,
            int failed
    ) {
        run.setStatus(failed > 0 ? SyncStatus.PARTIAL_SUCCESS.name() : SyncStatus.COMPLETED.name());
        run.setCompletedAt(Instant.now());
        run.setRecordsProcessed(processed);
        run.setRecordsCreated(created);
        run.setRecordsUpdated(updated);
        run.setRecordsFailed(failed);
        syncRunRepository.save(run);
    }

    private void failRun(
            SyncRun run,
            int processed,
            int created,
            int updated,
            int failed,
            String message
    ) {
        run.setStatus(SyncStatus.FAILED.name());
        run.setCompletedAt(Instant.now());
        run.setRecordsProcessed(processed);
        run.setRecordsCreated(created);
        run.setRecordsUpdated(updated);
        run.setRecordsFailed(failed);
        run.setErrorMessage(message);
        syncRunRepository.save(run);
    }

    private SyncResultResponse toResponse(SyncRun run) {
        return new SyncResultResponse(
                run.getId(),
                SyncType.valueOf(run.getSyncType()),
                SyncStatus.valueOf(run.getStatus()),
                run.getRecordsProcessed(),
                run.getRecordsCreated(),
                run.getRecordsUpdated(),
                run.getRecordsFailed(),
                run.getStartedAt(),
                run.getCompletedAt()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Transactional
    public SyncResultResponse syncEnrollments() {
        SyncRun run = startRun(SyncType.ENROLLMENTS);

        int processed = 0;
        int created = 0;
        int updated = 0;
        int failed = 0;

        try {
            List<MockEnrollmentDto> enrollments = sisClient.fetchEnrollments();

            for (MockEnrollmentDto dto : enrollments) {
                processed++;

                if (
                    isBlank(dto.externalEnrollmentId()) ||
                    isBlank(dto.externalStudentId()) ||
                    isBlank(dto.courseCode())
                ) {
                    failed++;
                    logSyncError(
                            run,
                            dto.externalEnrollmentId(),
                            "ENROLLMENT",
                            "Missing required enrollment fields",
                            dto.toString()
                    );
                    continue;
                }

                var studentOptional = studentRepository.findByExternalStudentId(dto.externalStudentId());
                var courseOptional = courseRepository.findByCourseCode(dto.courseCode());

                if (studentOptional.isEmpty() || courseOptional.isEmpty()) {
                    failed++;
                    logSyncError(
                            run,
                            dto.externalEnrollmentId(),
                            "ENROLLMENT",
                            "Missing referenced student or course",
                            dto.toString()
                    );
                    continue;
                }

                Enrollment enrollment = enrollmentRepository
                        .findByExternalEnrollmentId(dto.externalEnrollmentId())
                        .orElseGet(() -> {
                            Enrollment e = new Enrollment();
                            e.setExternalEnrollmentId(dto.externalEnrollmentId());
                            return e;
                        });

                boolean isNew = enrollment.getId() == null;

                enrollment.setStudent(studentOptional.get());
                enrollment.setCourse(courseOptional.get());
                enrollment.setSemester(dto.semester());
                enrollment.setStatus(dto.status());
                enrollment.setLastSyncedAt(Instant.now());

                enrollmentRepository.save(enrollment);

                if (isNew) {
                    created++;
                } else {
                    updated++;
                }
            }

            completeRun(run, processed, created, updated, failed);

        } catch (Exception ex) {
            failRun(run, processed, created, updated, failed, ex.getMessage());
        }

        SyncResultResponse response = toResponse(run);
        syncMetricsService.recordSyncResult(response);
        syncEventPublisher.publishSyncCompleted(response);
        return response;
    }

    private void logSyncError(
        SyncRun run,
        String sourceRecordId,
        String entityType,
        String reason,
        String rawPayload
    ) {
        SyncError error = new SyncError();
        error.setSyncRun(run);
        error.setSourceRecordId(sourceRecordId);
        error.setEntityType(entityType);
        error.setErrorReason(reason);
        error.setRawPayload(rawPayload);
        error.setRetryCount(0);
        error.setResolved(false);

        syncErrorRepository.save(error);
    }

    @Transactional(readOnly = true)
    public List<SyncRunResponse> getSyncRuns() {
        return syncRunRepository.findAll()
                .stream()
                .map(this::toRunResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SyncRunResponse getSyncRun(Long id) {
        SyncRun run = syncRunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sync run not found: " + id));

        return toRunResponse(run);
    }

    @Transactional(readOnly = true)
    public List<SyncErrorResponse> getOpenSyncErrors() {
        return syncErrorRepository.findByResolvedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::toErrorResponse)
                .toList();
    }

    private SyncRunResponse toRunResponse(SyncRun run) {
        return new SyncRunResponse(
                run.getId(),
                run.getSyncType(),
                run.getStatus(),
                run.getStartedAt(),
                run.getCompletedAt(),
                run.getRecordsProcessed(),
                run.getRecordsCreated(),
                run.getRecordsUpdated(),
                run.getRecordsFailed(),
                run.getErrorMessage()
        );
    }

    private SyncErrorResponse toErrorResponse(SyncError error) {
        return new SyncErrorResponse(
                error.getId(),
                error.getSyncRun().getId(),
                error.getSourceRecordId(),
                error.getEntityType(),
                error.getErrorReason(),
                error.getRawPayload(),
                error.getRetryCount(),
                error.isResolved(),
                error.getCreatedAt()
        );
    }

    @Transactional
    public RetryResultResponse retryOpenErrors() {
        var errors = syncErrorRepository.findByResolvedFalseOrderByCreatedAtDesc();

        int attempted = 0;
        int resolved = 0;
        int stillFailed = 0;

        for (SyncError error : errors) {
            attempted++;

            boolean success = retrySingleError(error);

            if (success) {
                error.setResolved(true);
                resolved++;
            } else {
                error.setRetryCount(error.getRetryCount() + 1);
                stillFailed++;
            }

            syncErrorRepository.save(error);
        }

        return new RetryResultResponse(attempted, resolved, stillFailed);
    }

    @Transactional
    public RetryResultResponse retryError(Long id) {
        SyncError error = syncErrorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sync error not found: " + id));

        if (error.isResolved()) {
            return new RetryResultResponse(0, 0, 0);
        }

        boolean success = retrySingleError(error);

        if (success) {
            error.setResolved(true);
            syncErrorRepository.save(error);
            return new RetryResultResponse(1, 1, 0);
        }

        error.setRetryCount(error.getRetryCount() + 1);
        syncErrorRepository.save(error);

        return new RetryResultResponse(1, 0, 1);
    }

    private boolean retrySingleError(SyncError error) {
        try {
            if ("ENROLLMENT".equals(error.getEntityType())) {
                return retryEnrollmentError(error);
            }

            // Student/course validation errors cannot be safely reconstructed yet
            // because rawPayload is only stored as dto.toString().
            return false;

        } catch (Exception ex) {
            return false;
        }
    }

    private boolean retryEnrollmentError(SyncError error) {
        List<MockEnrollmentDto> enrollments = sisClient.fetchEnrollments();

        MockEnrollmentDto dto = enrollments.stream()
                .filter(item -> item.externalEnrollmentId().equals(error.getSourceRecordId()))
                .findFirst()
                .orElse(null);

        if (dto == null) {
            return false;
        }

        var studentOptional = studentRepository.findByExternalStudentId(dto.externalStudentId());
        var courseOptional = courseRepository.findByCourseCode(dto.courseCode());

        if (studentOptional.isEmpty() || courseOptional.isEmpty()) {
            return false;
        }

        Enrollment enrollment = enrollmentRepository
                .findByExternalEnrollmentId(dto.externalEnrollmentId())
                .orElseGet(() -> {
                    Enrollment e = new Enrollment();
                    e.setExternalEnrollmentId(dto.externalEnrollmentId());
                    return e;
                });

        enrollment.setStudent(studentOptional.get());
        enrollment.setCourse(courseOptional.get());
        enrollment.setSemester(dto.semester());
        enrollment.setStatus(dto.status());
        enrollment.setLastSyncedAt(Instant.now());

        enrollmentRepository.save(enrollment);

        return true;
    }
}