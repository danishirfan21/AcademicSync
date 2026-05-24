package com.danish.academicsync.report;

import com.danish.academicsync.report.dto.EnrollmentsByCourseResponse;
import com.danish.academicsync.report.dto.StaleRecordResponse;
import com.danish.academicsync.report.dto.StudentsByProgramResponse;
import com.danish.academicsync.report.dto.SyncHealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/students-by-program")
    public List<StudentsByProgramResponse> studentsByProgram() {
        return reportService.studentsByProgram();
    }

    @GetMapping("/enrollments-by-course")
    public List<EnrollmentsByCourseResponse> enrollmentsByCourse() {
        return reportService.enrollmentsByCourse();
    }

    @GetMapping("/sync-health")
    public SyncHealthResponse syncHealth() {
        return reportService.syncHealth();
    }

    @GetMapping("/stale-records")
    public List<StaleRecordResponse> staleRecords(
            @RequestParam(defaultValue = "24") int olderThanHours
    ) {
        return reportService.staleRecords(olderThanHours);
    }
}