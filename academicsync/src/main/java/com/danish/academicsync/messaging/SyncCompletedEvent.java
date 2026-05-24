package com.danish.academicsync.messaging;

import java.time.Instant;

public record SyncCompletedEvent(
        Long syncRunId,
        String syncType,
        String status,
        int recordsProcessed,
        int recordsCreated,
        int recordsUpdated,
        int recordsFailed,
        Instant occurredAt
) {
}