package com.danish.academicsync.mock;

import com.danish.academicsync.mock.dto.MockCourseDto;
import com.danish.academicsync.mock.dto.MockEnrollmentDto;
import com.danish.academicsync.mock.dto.MockStudentDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MockSisController {

    @GetMapping("/mock-sis/students")
    public List<MockStudentDto> students() {
        return List.of(
                new MockStudentDto(
                        "STU-1001",
                        "Ayesha Khan",
                        "ayesha.khan@university.edu",
                        "BS Computer Science",
                        "ACTIVE",
                        "Fall 2022"
                ),
                new MockStudentDto(
                        "STU-1002",
                        "Hamza Ali",
                        "hamza.ali@university.edu",
                        "BS Economics",
                        "ACTIVE",
                        "Fall 2021"
                ),
                new MockStudentDto(
                        "STU-1003",
                        "Sara Ahmed",
                        "sara.ahmed@university.edu",
                        "BBA",
                        "ON_LEAVE",
                        "Spring 2023"
                )
        );
    }

    @GetMapping("/mock-sis/courses")
    public List<MockCourseDto> courses() {
        return List.of(
                new MockCourseDto(
                        "CS-201",
                        "Data Structures",
                        "Computer Science",
                        4,
                        true
                ),
                new MockCourseDto(
                        "CS-305",
                        "Database Systems",
                        "Computer Science",
                        3,
                        true
                ),
                new MockCourseDto(
                        "ECO-210",
                        "Microeconomics",
                        "Economics",
                        3,
                        true
                )
        );
    }

    @GetMapping("/mock-sis/enrollments")
    public List<MockEnrollmentDto> enrollments() {
        return List.of(
                new MockEnrollmentDto(
                        "ENR-5001",
                        "STU-1001",
                        "CS-201",
                        "Fall 2026",
                        "ENROLLED"
                ),
                new MockEnrollmentDto(
                        "ENR-5002",
                        "STU-1001",
                        "CS-305",
                        "Fall 2026",
                        "ENROLLED"
                ),
                new MockEnrollmentDto(
                        "ENR-5003",
                        "STU-1002",
                        "ECO-210",
                        "Fall 2026",
                        "ENROLLED"
                )
        );
    }
}