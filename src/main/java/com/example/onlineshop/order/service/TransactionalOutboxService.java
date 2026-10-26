package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.OrderEvent;
import com.example.onlineshop.order.entity.OutboxEvent;
import com.example.onlineshop.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionalOutboxService {
    private final OutboxEventRepository outboxEventRepository;
    private final OrderProducerService orderProducerService;
    private final ObjectMapper objectMapper;

    @Transactional
    public OutboxEvent enqueue(OrderEvent event) {
        if (event == null || event.getOrderId() == null || event.getStatus() == null) {
            throw new IllegalArgumentException("An order event with ID and status is required");
        }
        String key = "order:" + event.getOrderId() + ":" + event.getStatus().name();
        return outboxEventRepository.findByDeduplicationKey(key).orElseGet(() -> {
            OutboxEvent outbox = new OutboxEvent();
            outbox.setDeduplicationKey(key);
            outbox.setEventType("ORDER_" + event.getStatus().name());
            outbox.setStatus("PENDING");
            outbox.setPayload(serialize(event));
            outbox.setAttempts(0);
            outbox.setCreatedAt(LocalDateTime.now());
            outbox.setNextAttemptAt(outbox.getCreatedAt());
            return outboxEventRepository.save(outbox);
        });
    }

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms:10000}")
    public void dispatchPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc("PENDING", LocalDateTime.now());
        events.forEach(this::dispatchOne);
    }

    @Transactional
    public void dispatchOne(OutboxEvent outbox) {
        if (!"PENDING".equals(outbox.getStatus())) return;
        try {
            OrderEvent event = objectMapper.readValue(outbox.getPayload(), OrderEvent.class);
            if (orderProducerService.publish(event)) {
                outbox.setStatus("PUBLISHED");
                outbox.setPublishedAt(LocalDateTime.now());
                outbox.setLastError(null);
            } else {
                markRetry(outbox, "Event adapter unavailable");
            }
        } catch (Exception exception) {
            markRetry(outbox, exception.getMessage());
        }
        outboxEventRepository.save(outbox);
    }

    private void markRetry(OutboxEvent outbox, String error) {
        outbox.setAttempts(outbox.getAttempts() + 1);
        outbox.setStatus("PENDING");
        outbox.setLastError(error == null ? "Unknown dispatch failure" : error.substring(0, Math.min(500, error.length())));
        long delaySeconds = Math.min(3600, 1L << Math.min(outbox.getAttempts(), 12));
        outbox.setNextAttemptAt(LocalDateTime.now().plusSeconds(delaySeconds));
    }

    private String serialize(OrderEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Unable to serialize order event", exception);
        }
    }
}
