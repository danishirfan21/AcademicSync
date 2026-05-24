package com.danish.academicsync.student;

import com.danish.academicsync.report.dto.StudentsByProgramResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByExternalStudentId(String externalStudentId);

    @Query("""
            select new com.danish.academicsync.report.dto.StudentsByProgramResponse(
                coalesce(s.program, 'UNKNOWN'),
                count(s)
            )
            from Student s
            group by coalesce(s.program, 'UNKNOWN')
            order by count(s) desc
            """)
    List<StudentsByProgramResponse> countStudentsByProgram();

    List<Student> findByLastSyncedAtBeforeOrLastSyncedAtIsNull(Instant threshold);
}