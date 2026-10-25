package com.example.onlineshop.service;

import com.example.onlineshop.config.KafkaConfig;
import com.example.onlineshop.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducerService {
    private final ObjectProvider<KafkaTemplate<String, OrderEvent>> kafkaTemplateProvider;

    @Value("${app.kafka.enabled:false}")
    private boolean kafkaEnabled;

    public void publish(OrderEvent event) {
        if (!kafkaEnabled) {
            log.info("Kafka disabled; skipping queue publish for order {}.", event.getOrderId());
            return;
        }

        KafkaTemplate<String, OrderEvent> kafkaTemplate = kafkaTemplateProvider.getIfAvailable();
        if (kafkaTemplate == null) {
            log.warn("Kafka is not available and app.kafka.enabled=true but producer bean is missing for order {}.", event.getOrderId());
            return;
        }

        try {
            kafkaTemplate.send(KafkaConfig.ORDER_TOPIC, event.getOrderId().toString(), event);
        } catch (Exception ex) {
            log.warn("Kafka unavailable, order event not published for order {}. Reason: {}", event.getOrderId(), ex.getMessage());
        }
    }
}