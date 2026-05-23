package com.danish.academicsync.sync;

import com.danish.academicsync.course.Course;
import com.danish.academicsync.course.CourseRepository;
import com.danish.academicsync.integration.SisClient;
import com.danish.academicsync.mock.dto.MockCourseDto;
import com.danish.academicsync.mock.dto.MockStudentDto;
import com.danish.academicsync.student.Student;
import com.danish.academicsync.student.StudentRepository;
import com.danish.academicsync.sync.dto.SyncResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final SisClient sisClient;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SyncRunRepository syncRunRepository;

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

        return toResponse(run);
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

        return toResponse(run);
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
}