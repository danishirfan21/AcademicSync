package com.danish.academicsync.mock.dto;

public record MockStudentDto(
        String externalStudentId,
        String fullName,
        String email,
        String program,
        String academicStatus,
        String admissionTerm
) {
}