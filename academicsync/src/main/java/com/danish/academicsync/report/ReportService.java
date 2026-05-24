package com.danish.academicsync.report;

import com.danish.academicsync.course.CourseRepository;
import com.danish.academicsync.enrollment.EnrollmentRepository;
import com.danish.academicsync.report.dto.EnrollmentsByCourseResponse;
import com.danish.academicsync.report.dto.StaleRecordResponse;
import com.danish.academicsync.report.dto.StudentsByProgramResponse;
import com.danish.academicsync.report.dto.SyncHealthResponse;
import com.danish.academicsync.student.StudentRepository;
import com.danish.academicsync.sync.SyncErrorRepository;
import com.danish.academicsync.sync.SyncRunRepository;
import com.danish.academicsync.sync.SyncStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SyncRunRepository syncRunRepository;
    private final SyncErrorRepository syncErrorRepository;

    @Transactional(readOnly = true)
    public List<StudentsByProgramResponse> studentsByProgram() {
        return studentRepository.countStudentsByProgram();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentsByCourseResponse> enrollmentsByCourse() {
        return enrollmentRepository.countEnrollmentsByCourse();
    }

    @Transactional(readOnly = true)
    public SyncHealthResponse syncHealth() {
        return new SyncHealthResponse(
                syncRunRepository.count(),
                syncRunRepository.countByStatus(SyncStatus.COMPLETED.name()),
                syncRunRepository.countByStatus(SyncStatus.FAILED.name()),
                syncRunRepository.countByStatus(SyncStatus.PARTIAL_SUCCESS.name()),
                syncErrorRepository.countByResolvedFalse()
        );
    }

    @Transactional(readOnly = true)
    public List<StaleRecordResponse> staleRecords(int olderThanHours) {
        Instant threshold = Instant.now().minus(olderThanHours, ChronoUnit.HOURS);

        List<StaleRecordResponse> records = new ArrayList<>();

        studentRepository.findByLastSyncedAtBeforeOrLastSyncedAtIsNull(threshold)
                .forEach(student -> records.add(new StaleRecordResponse(
                        "STUDENT",
                        student.getExternalStudentId(),
                        student.getFullName(),
                        student.getLastSyncedAt()
                )));

        courseRepository.findByLastSyncedAtBeforeOrLastSyncedAtIsNull(threshold)
                .forEach(course -> records.add(new StaleRecordResponse(
                        "COURSE",
                        course.getCourseCode(),
                        course.getTitle(),
                        course.getLastSyncedAt()
                )));

        enrollmentRepository.findByLastSyncedAtBeforeOrLastSyncedAtIsNull(threshold)
                .forEach(enrollment -> records.add(new StaleRecordResponse(
                        "ENROLLMENT",
                        enrollment.getExternalEnrollmentId(),
                        enrollment.getStudent().getExternalStudentId() + " → " + enrollment.getCourse().getCourseCode(),
                        enrollment.getLastSyncedAt()
                )));

        return records;
    }
}