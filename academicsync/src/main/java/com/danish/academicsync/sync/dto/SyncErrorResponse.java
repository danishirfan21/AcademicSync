package com.danish.academicsync.sync.dto;

import java.time.Instant;

public record SyncErrorResponse(
        Long id,
        Long syncRunId,
        String sourceRecordId,
        String entityType,
        String errorReason,
        String rawPayload,
        int retryCount,
        boolean resolved,
        Instant createdAt
) {
}