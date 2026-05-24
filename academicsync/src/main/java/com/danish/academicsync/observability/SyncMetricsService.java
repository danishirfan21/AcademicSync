package com.danish.academicsync.observability;

import com.danish.academicsync.sync.dto.SyncResultResponse;
import com.danish.academicsync.sync.SyncStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class SyncMetricsService {

    private final MeterRegistry meterRegistry;

    public SyncMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordSyncResult(SyncResultResponse result) {
        meterRegistry.counter(
                "academicsync_sync_runs_total",
                "sync_type", result.syncType().name(),
                "status", result.status().name()
        ).increment();

        meterRegistry.counter(
                "academicsync_records_processed_total",
                "sync_type", result.syncType().name()
        ).increment(result.recordsProcessed());

        meterRegistry.counter(
                "academicsync_records_failed_total",
                "sync_type", result.syncType().name()
        ).increment(result.recordsFailed());

        recordDuration(result);
    }

    private void recordDuration(SyncResultResponse result) {
        Instant startedAt = result.startedAt();
        Instant completedAt = result.completedAt();

        if (startedAt == null || completedAt == null) {
            return;
        }

        Timer timer = Timer.builder("academicsync_sync_duration")
                .tag("sync_type", result.syncType().name())
                .tag("status", result.status().name())
                .description("Duration of AcademicSync sync runs")
                .register(meterRegistry);

        timer.record(Duration.between(startedAt, completedAt));
    }
}
