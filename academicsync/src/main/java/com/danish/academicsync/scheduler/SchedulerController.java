package com.danish.academicsync.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SchedulerController {

    @Value("${academicsync.scheduler.enabled:true}")
    private boolean schedulerEnabled;

    @Value("${academicsync.scheduler.cron}")
    private String cronExpression;

    @GetMapping("/api/scheduler/status")
    public Map<String, Object> schedulerStatus() {
        return Map.of(
                "enabled", schedulerEnabled,
                "cron", cronExpression,
                "description", "Runs student, course, and enrollment sync in dependency order"
        );
    }
}