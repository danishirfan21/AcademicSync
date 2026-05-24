package com.danish.academicsync.messaging;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "academicsync.messaging.enabled", havingValue = "true")
public class RabbitMqConfig {

    @Value("${academicsync.messaging.exchange}")
    private String exchangeName;

    @Value("${academicsync.messaging.sync-routing-key}")
    private String syncRoutingKey;

    @Value("${academicsync.messaging.failure-routing-key}")
    private String failureRoutingKey;

    @Bean
    public DirectExchange academicSyncExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue syncAuditQueue() {
        return QueueBuilder.durable("academicsync.sync.audit").build();
    }

    @Bean
    public Binding syncCompletedBinding() {
        return BindingBuilder
                .bind(syncAuditQueue())
                .to(academicSyncExchange())
                .with(syncRoutingKey);
    }

    @Bean
    public Binding syncFailureBinding() {
        return BindingBuilder
                .bind(syncAuditQueue())
                .to(academicSyncExchange())
                .with(failureRoutingKey);
    }
}
