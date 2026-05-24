package com.danish.academicsync.importer;

import com.danish.academicsync.importer.dto.CsvImportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final StudentCsvImportService studentCsvImportService;

    @PostMapping(
            value = "/students/csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public CsvImportResponse importStudentsCsv(@RequestParam("file") MultipartFile file) {
        return studentCsvImportService.importStudents(file);
    }
}