package com.danish.academicsync.importer;

import com.danish.academicsync.importer.dto.CsvImportResponse;
import com.danish.academicsync.student.Student;
import com.danish.academicsync.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentCsvImportService {

    private final StudentRepository studentRepository;

    @Transactional
    public CsvImportResponse importStudents(MultipartFile file) {
        int processed = 0;
        int created = 0;
        int updated = 0;
        int failed = 0;

        List<String> errors = new ArrayList<>();

        try (
                InputStreamReader reader = new InputStreamReader(
                        file.getInputStream(),
                        StandardCharsets.UTF_8
                )
        ) {
            var records = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build()
                    .parse(reader);

            for (var record : records) {
                processed++;

                String externalStudentId = get(record, "externalStudentId");
                String fullName = get(record, "fullName");
                String email = get(record, "email");
                String program = get(record, "program");
                String academicStatus = get(record, "academicStatus");
                String admissionTerm = get(record, "admissionTerm");

                if (isBlank(externalStudentId) || isBlank(fullName) || isBlank(email)) {
                    failed++;
                    errors.add("Row " + record.getRecordNumber() + ": missing required fields");
                    continue;
                }

                Student student = studentRepository
                        .findByExternalStudentId(externalStudentId)
                        .orElseGet(() -> {
                            Student s = new Student();
                            s.setExternalStudentId(externalStudentId);
                            return s;
                        });

                boolean isNew = student.getId() == null;

                student.setFullName(fullName);
                student.setEmail(email);
                student.setProgram(program);
                student.setAcademicStatus(academicStatus);
                student.setAdmissionTerm(admissionTerm);
                student.setLastSyncedAt(Instant.now());

                studentRepository.save(student);

                if (isNew) {
                    created++;
                } else {
                    updated++;
                }
            }

        } catch (Exception ex) {
            errors.add("CSV import failed: " + ex.getMessage());
            failed++;
        }

        return new CsvImportResponse(
                processed,
                created,
                updated,
                failed,
                errors
        );
    }

    private String get(org.apache.commons.csv.CSVRecord record, String column) {
        return record.isMapped(column) ? record.get(column) : null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}