package com.danish.academicsync.report.dto;

public record SyncHealthResponse(
        long totalRuns,
        long completedRuns,
        long failedRuns,
        long partialSuccessRuns,
        long openErrors
) {
}