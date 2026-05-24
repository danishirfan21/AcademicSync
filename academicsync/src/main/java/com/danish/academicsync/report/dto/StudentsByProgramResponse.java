package com.danish.academicsync.report.dto;

public record StudentsByProgramResponse(
        String program,
        long studentCount
) {
}