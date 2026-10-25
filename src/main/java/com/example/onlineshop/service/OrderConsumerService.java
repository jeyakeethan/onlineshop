package com.example.onlineshop.service;

import com.example.onlineshop.config.KafkaConfig;
import com.example.onlineshop.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app", name = "kafka.enabled", havingValue = "true")
public class OrderConsumerService {
    private final AsyncOrderService orders;

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000))
    @KafkaListener(topics = KafkaConfig.ORDER_TOPIC, groupId = "application-consumer-group",
            containerFactory = "orderKafkaListenerContainerFactory")
    public void process(OrderEvent event) {
        orders.fulfill(event);
        orders.pay(event.getOrderId());
    }

    @RetryableTopic(attempts = "3", backoff = @Backoff(delay = 2000))
    @KafkaListener(topics = KafkaConfig.FAILED_TOPIC, groupId = "order-failure-monitor-group",
            containerFactory = "orderKafkaListenerContainerFactory")
    public void deadLetter(OrderEvent event) {
        orders.fail(event.getOrderId());
        log.error("Order {} routed to {}", event.getOrderId(), KafkaConfig.FAILED_TOPIC);
    }

}