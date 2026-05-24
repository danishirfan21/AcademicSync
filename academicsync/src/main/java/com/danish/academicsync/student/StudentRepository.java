package com.danish.academicsync.student;

import com.danish.academicsync.report.dto.StudentsByProgramResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByExternalStudentId(String externalStudentId);

    @Query("SELECT new com.danish.academicsync.report.dto.StudentsByProgramResponse(COALESCE(s.program, 'Unknown'), COUNT(s)) " +
           "FROM Student s GROUP BY s.program ORDER BY COUNT(s) DESC")
    List<StudentsByProgramResponse> countStudentsByProgram();

    List<Student> findByLastSyncedAtBeforeOrLastSyncedAtIsNull(Instant threshold);
}
