package com.danish.academicsync.mock.dto;

public record MockCourseDto(
        String courseCode,
        String title,
        String department,
        Integer creditHours,
        Boolean active
) {
}