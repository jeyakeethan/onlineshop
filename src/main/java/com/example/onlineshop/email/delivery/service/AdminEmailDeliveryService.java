package com.example.onlineshop.email.delivery.service;

import com.example.onlineshop.email.delivery.dto.EmailDeliveryDTO;
import com.example.onlineshop.email.delivery.repository.EmailDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

@Service @RequiredArgsConstructor
public class AdminEmailDeliveryService {
    private static final int MAX_ATTEMPTS = 5;
    private final EmailDeliveryRepository emailDeliveryRepository;

    public Page<EmailDeliveryDTO> getFailedDeliveries(int page, int size, String status) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }
        String normalized = status == null || status.isBlank() ? "" : status.trim().toUpperCase(Locale.ROOT);
        Set<String> allowed = Set.of("FAILED", "RETRYING");
        if (!normalized.isBlank() && !allowed.contains(normalized)) {
            throw new IllegalArgumentException("Status must be FAILED or RETRYING");
        }
        var filtered = emailDeliveryRepository.findAll().stream()
                .filter(delivery -> allowed.contains(delivery.getStatus() == null ? "" : delivery.getStatus().toUpperCase(Locale.ROOT)))
                .filter(delivery -> normalized.isBlank() || normalized.equalsIgnoreCase(delivery.getStatus()))
                .sorted(java.util.Comparator.comparing(com.example.onlineshop.email.delivery.entity.EmailDelivery::getCreatedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .toList();
        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());
        return new PageImpl<>(filtered.subList(start, end), PageRequest.of(page, size), filtered.size())
                .map(EmailDeliveryDTO::new);
    }

    @Transactional
    public EmailDeliveryDTO retry(Long deliveryId) {
        var delivery = emailDeliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Email delivery not found"));
        String status = delivery.getStatus() == null ? "" : delivery.getStatus().toUpperCase(Locale.ROOT);
        if (!Set.of("FAILED", "RETRYING").contains(status)) {
            throw new IllegalStateException("Only failed or retrying deliveries can be retried");
        }
        if ("RETRYING".equals(status)) {
            throw new IllegalStateException("Delivery retry is already in progress");
        }
        if (delivery.getAttempts() >= MAX_ATTEMPTS) {
            throw new IllegalStateException("Delivery retry limit has been reached");
        }
        delivery.setAttempts(delivery.getAttempts() + 1);
        delivery.setStatus("RETRYING");
        delivery.setNextAttemptAt(LocalDateTime.now());
        return new EmailDeliveryDTO(emailDeliveryRepository.save(delivery));
    }
}
