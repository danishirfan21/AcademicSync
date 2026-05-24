package com.danish.academicsync.report.dto;

import java.time.Instant;

public record StaleRecordResponse(
        String entityType,
        String externalId,
        String displayName,
        Instant lastSyncedAt
) {
}