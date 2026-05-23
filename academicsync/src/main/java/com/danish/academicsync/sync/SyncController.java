package com.danish.academicsync.sync;

import com.danish.academicsync.sync.dto.SyncResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}