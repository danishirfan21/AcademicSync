package com.danish.academicsync.report;

import com.danish.academicsync.IntegrationTestBase;
import com.danish.academicsync.report.dto.SyncHealthResponse;
import com.danish.academicsync.sync.dto.SyncResultResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class ReportIntegrationTest extends IntegrationTestBase {

    private final RestClient restClient = RestClient.create();

    @Test
    void shouldReturnSyncHealthReport() {
        post("/api/sync/students");
        post("/api/sync/courses");
        post("/api/sync/enrollments");

        SyncHealthResponse response = restClient.get()
                .uri("http://localhost:" + port + "/api/reports/sync-health")
                .retrieve()
                .body(SyncHealthResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.totalRuns()).isEqualTo(3);
        assertThat(response.completedRuns()).isEqualTo(3);
        assertThat(response.openErrors()).isEqualTo(0);
    }

    private SyncResultResponse post(String path) {
        return restClient.post()
                .uri("http://localhost:" + port + path)
                .retrieve()
                .body(SyncResultResponse.class);
    }
}