package com.danish.academicsync.sync;

import com.danish.academicsync.IntegrationTestBase;
import com.danish.academicsync.course.CourseRepository;
import com.danish.academicsync.enrollment.EnrollmentRepository;
import com.danish.academicsync.student.StudentRepository;
import com.danish.academicsync.sync.dto.SyncResultResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class SyncWorkflowIntegrationTest extends IntegrationTestBase {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SyncRunRepository syncRunRepository;

    private final RestClient restClient = RestClient.create();

    @Test
    void shouldSyncStudentsCoursesAndEnrollments() {
        SyncResultResponse studentResult = post("/api/sync/students");
        SyncResultResponse courseResult = post("/api/sync/courses");
        SyncResultResponse enrollmentResult = post("/api/sync/enrollments");

        assertThat(studentResult.recordsCreated()).isEqualTo(3);
        assertThat(courseResult.recordsCreated()).isEqualTo(3);
        assertThat(enrollmentResult.recordsCreated()).isEqualTo(3);

        assertThat(studentRepository.count()).isEqualTo(3);
        assertThat(courseRepository.count()).isEqualTo(3);
        assertThat(enrollmentRepository.count()).isEqualTo(3);
        assertThat(syncRunRepository.count()).isEqualTo(3);
    }

    @Test
    void shouldUpdateExistingStudentsOnSecondSync() {
        post("/api/sync/students");

        SyncResultResponse secondResult = post("/api/sync/students");

        assertThat(secondResult.recordsCreated()).isEqualTo(0);
        assertThat(secondResult.recordsUpdated()).isEqualTo(3);
        assertThat(studentRepository.count()).isEqualTo(3);
    }

    private SyncResultResponse post(String path) {
        return restClient.post()
                .uri("http://localhost:" + port + path)
                .retrieve()
                .body(SyncResultResponse.class);
    }
}