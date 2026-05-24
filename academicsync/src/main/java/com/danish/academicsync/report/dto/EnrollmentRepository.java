package com.danish.academicsync.enrollment;

import com.danish.academicsync.report.dto.EnrollmentsByCourseResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByExternalEnrollmentId(String externalEnrollmentId);

    @Query("""
            select new com.danish.academicsync.report.dto.EnrollmentsByCourseResponse(
                c.courseCode,
                c.title,
                count(e)
            )
            from Enrollment e
            join e.course c
            group by c.courseCode, c.title
            order by count(e) desc
            """)
    List<EnrollmentsByCourseResponse> countEnrollmentsByCourse();

    List<Enrollment> findByLastSyncedAtBeforeOrLastSyncedAtIsNull(Instant threshold);
}