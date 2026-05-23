package com.danish.academicsync.sync.dto;

import com.danish.academicsync.sync.SyncStatus;
import com.danish.academicsync.sync.SyncType;

import java.time.Instant;

public record SyncResultResponse(
        Long syncRunId,
        SyncType syncType,
        SyncStatus status,
        int recordsProcessed,
        int recordsCreated,
        int recordsUpdated,
        int recordsFailed,
        Instant startedAt,
        Instant completedAt
) {
}