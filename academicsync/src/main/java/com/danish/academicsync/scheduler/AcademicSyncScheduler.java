package com.danish.academicsync.scheduler;

import com.danish.academicsync.sync.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicSyncScheduler {

    private final SyncService syncService;

    @Value("${academicsync.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    @Scheduled(cron = "${academicsync.scheduler.cron}")
    public void runScheduledAcademicSync() {
        if (!schedulerEnabled) {
            log.info("AcademicSync scheduler is disabled. Skipping scheduled sync.");
            return;
        }

        log.info("Starting scheduled academic sync.");

        var studentResult = syncService.syncStudents();
        log.info("Student sync completed: {}", studentResult);

        var courseResult = syncService.syncCourses();
        log.info("Course sync completed: {}", courseResult);

        var enrollmentResult = syncService.syncEnrollments();
        log.info("Enrollment sync completed: {}", enrollmentResult);

        log.info("Scheduled academic sync completed.");
    }
}