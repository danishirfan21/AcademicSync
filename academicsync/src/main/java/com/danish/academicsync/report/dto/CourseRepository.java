package com.danish.academicsync.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByLastSyncedAtBeforeOrLastSyncedAtIsNull(Instant threshold);
}