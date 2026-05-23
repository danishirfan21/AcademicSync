package com.danish.academicsync.mock.dto;

public record MockEnrollmentDto(
        String externalEnrollmentId,
        String externalStudentId,
        String courseCode,
        String semester,
        String status
) {
}