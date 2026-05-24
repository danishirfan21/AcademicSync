package com.danish.academicsync.integration;

import com.danish.academicsync.mock.dto.MockCourseDto;
import com.danish.academicsync.mock.MockSisDataService;
import com.danish.academicsync.mock.dto.MockStudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.danish.academicsync.mock.dto.MockEnrollmentDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SisClient {

    private final MockSisDataService mockSisDataService;

    public List<MockStudentDto> fetchStudents() {
        return mockSisDataService.students();
    }

    public List<MockCourseDto> fetchCourses() {
        return mockSisDataService.courses();
    }

    public List<MockEnrollmentDto> fetchEnrollments() {
        return mockSisDataService.enrollments();
    }
}
