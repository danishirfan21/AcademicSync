package com.danish.academicsync.integration;

import com.danish.academicsync.mock.dto.MockCourseDto;
import com.danish.academicsync.mock.dto.MockStudentDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class SisClient {

    private final RestClient restClient;

    public SisClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public List<MockStudentDto> fetchStudents() {
        return restClient.get()
                .uri("/mock-sis/students")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<MockCourseDto> fetchCourses() {
        return restClient.get()
                .uri("/mock-sis/courses")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}