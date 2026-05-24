package com.danish.academicsync.report.dto;

public record EnrollmentsByCourseResponse(
        String courseCode,
        String courseTitle,
        long enrollmentCount
) {
}