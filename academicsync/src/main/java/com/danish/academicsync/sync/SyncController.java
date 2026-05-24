package com.danish.academicsync.sync;

import com.danish.academicsync.sync.dto.SyncResultResponse;
import lombok.RequiredArgsConstructor;
import main.java.com.danish.academicsync.sync.dto.SyncErrorResponse;
import main.java.com.danish.academicsync.sync.dto.SyncRunResponse;

import com.danish.academicsync.sync.dto.SyncRunResponse;
import com.danish.academicsync.sync.dto.SyncErrorResponse;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.danish.academicsync.sync.dto.RetryResultResponse;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/students")
    public SyncResultResponse syncStudents() {
        return syncService.syncStudents();
    }

    @PostMapping("/courses")
    public SyncResultResponse syncCourses() {
        return syncService.syncCourses();
    }

    @PostMapping("/enrollments")
    public SyncResultResponse syncEnrollments() {
        return syncService.syncEnrollments();
    }

    @GetMapping("/runs")
    public List<SyncRunResponse> getSyncRuns() {
        return syncService.getSyncRuns();
    }

    @GetMapping("/runs/{id}")
    public SyncRunResponse getSyncRun(@PathVariable Long id) {
        return syncService.getSyncRun(id);
    }

    @GetMapping("/errors")
    public List<SyncErrorResponse> getOpenSyncErrors() {
        return syncService.getOpenSyncErrors();
    }

    @PostMapping("/retry-errors")
    public RetryResultResponse retryOpenErrors() {
        return syncService.retryOpenErrors();
    }

    @PostMapping("/errors/{id}/retry")
    public RetryResultResponse retryError(@PathVariable Long id) {
        return syncService.retryError(id);
    }
}