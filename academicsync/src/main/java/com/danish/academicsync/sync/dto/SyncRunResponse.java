package com.danish.academicsync.sync.dto;

import java.time.Instant;

public record SyncRunResponse(
        Long id,
        String syncType,
        String status,
        Instant startedAt,
        Instant completedAt,
        int recordsProcessed,
        int recordsCreated,
        int recordsUpdated,
        int recordsFailed,
        String errorMessage
) {
}