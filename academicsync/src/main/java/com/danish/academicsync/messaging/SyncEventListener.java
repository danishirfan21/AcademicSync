package com.danish.academicsync.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyncEventListener {

    @RabbitListener(queues = "academicsync.sync.audit")
    public void handleSyncEvent(SyncCompletedEvent event) {
        log.info("Received sync event: {}", event);
    }
}