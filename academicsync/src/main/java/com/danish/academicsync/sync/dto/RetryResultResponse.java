package com.danish.academicsync.sync.dto;

public record RetryResultResponse(
        int attempted,
        int resolved,
        int stillFailed
) {
}