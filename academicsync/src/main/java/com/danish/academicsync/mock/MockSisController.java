package com.danish.academicsync.mock;

import com.danish.academicsync.mock.dto.MockCourseDto;
import com.danish.academicsync.mock.dto.MockEnrollmentDto;
import com.danish.academicsync.mock.dto.MockStudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MockSisController {

    private final MockSisDataService mockSisDataService;

    @GetMapping("/mock-sis/students")
    public List<MockStudentDto> students() {
        return mockSisDataService.students();
    }

    @GetMapping("/mock-sis/courses")
    public List<MockCourseDto> courses() {
        return mockSisDataService.courses();
    }

    @GetMapping("/mock-sis/enrollments")
    public List<MockEnrollmentDto> enrollments() {
        return mockSisDataService.enrollments();
    }
}
