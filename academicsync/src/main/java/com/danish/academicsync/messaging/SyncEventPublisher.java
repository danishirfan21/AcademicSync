package com.danish.academicsync.messaging;

import com.danish.academicsync.sync.dto.SyncResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SyncEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${academicsync.messaging.enabled:false}")
    private boolean messagingEnabled;

    @Value("${academicsync.messaging.exchange}")
    private String exchange;

    @Value("${academicsync.messaging.sync-routing-key}")
    private String syncRoutingKey;

    @Value("${academicsync.messaging.failure-routing-key}")
    private String failureRoutingKey;

    public void publishSyncCompleted(SyncResultResponse result) {
        if (!messagingEnabled) {
            return;
        }

        SyncCompletedEvent event = new SyncCompletedEvent(
                result.syncRunId(),
                result.syncType().name(),
                result.status().name(),
                result.recordsProcessed(),
                result.recordsCreated(),
                result.recordsUpdated(),
                result.recordsFailed(),
                Instant.now()
        );

        String routingKey = result.recordsFailed() > 0
                ? failureRoutingKey
                : syncRoutingKey;

        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
