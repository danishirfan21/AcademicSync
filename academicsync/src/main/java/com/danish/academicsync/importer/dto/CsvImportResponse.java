package com.danish.academicsync.importer.dto;

import java.util.List;

public record CsvImportResponse(
        int rowsProcessed,
        int rowsCreated,
        int rowsUpdated,
        int rowsFailed,
        List<String> errors
) {
}