package com.danish.academicsync.enrollment;

import com.danish.academicsync.report.dto.EnrollmentsByCourseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByExternalEnrollmentId(String externalEnrollmentId);

    @Query("SELECT new com.danish.academicsync.report.dto.EnrollmentsByCourseResponse(c.courseCode, c.title, COUNT(e)) " +
           "FROM Enrollment e JOIN e.course c GROUP BY c.courseCode, c.title ORDER BY COUNT(e) DESC")
    List<EnrollmentsByCourseResponse> countEnrollmentsByCourse();

    List<Enrollment> findByLastSyncedAtBeforeOrLastSyncedAtIsNull(Instant threshold);
}
