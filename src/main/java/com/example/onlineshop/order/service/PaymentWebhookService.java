package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.PaymentWebhookPayload;
import com.example.onlineshop.order.dto.PaymentWebhookResultDTO;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.PaymentWebhookEvent;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.order.repository.PaymentWebhookEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final PaymentWebhookEventRepository webhookEventRepository;

    @Value("${app.payments.mock.webhook-secret:local-mock-webhook-secret}")
    private String webhookSecret = "local-mock-webhook-secret";

    @Transactional
    public PaymentWebhookResultDTO process(String provider, String signature, String rawBody) {
        String normalizedProvider = provider == null ? "" : provider.trim().toLowerCase(Locale.ROOT);
        if (!"mock".equals(normalizedProvider)) {
            throw new IllegalArgumentException("Payment provider is not configured");
        }
        if (rawBody == null || rawBody.isBlank() || !isValidSignature(signature, rawBody)) {
            throw new org.springframework.security.access.AccessDeniedException("Invalid payment webhook signature");
        }

        PaymentWebhookPayload payload;
        try {
            payload = objectMapper.readValue(rawBody, PaymentWebhookPayload.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid payment webhook payload", exception);
        }
        validatePayload(payload);

        var prior = webhookEventRepository.findByEventId(payload.getEventId());
        if (prior.isPresent()) {
            Order order = orderRepository.findByPaymentIntentId(payload.getPaymentIntentId())
                    .orElseThrow(() -> new IllegalArgumentException("Payment intent not found"));
            return new PaymentWebhookResultDTO(payload.getEventId(), order.getOrderId(),
                    order.getPaymentStatus(), true);
        }

        Order order = orderRepository.findByPaymentIntentId(payload.getPaymentIntentId())
                .orElseThrow(() -> new IllegalArgumentException("Payment intent not found"));
        String expectedCurrency = order.getPaymentCurrency() == null ? "USD" : order.getPaymentCurrency();
        if (Math.abs(payload.getAmount() - order.getTotalPrice()) > 0.01
                || !expectedCurrency.equalsIgnoreCase(payload.getCurrency())) {
            throw new IllegalArgumentException("Webhook amount or currency does not match the order");
        }

        boolean succeeded = "SUCCEEDED".equalsIgnoreCase(payload.getStatus())
                || "PAID".equalsIgnoreCase(payload.getStatus());
        boolean failed = "FAILED".equalsIgnoreCase(payload.getStatus())
                || "DECLINED".equalsIgnoreCase(payload.getStatus());
        if (!succeeded && !failed) {
            throw new IllegalArgumentException("Webhook status must be SUCCEEDED or FAILED");
        }
        if (succeeded && "FAILED".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new IllegalStateException("A failed payment cannot transition to paid from this event");
        }
        if (failed && "PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new IllegalStateException("A paid order cannot transition to failed");
        }

        String status = succeeded ? "PAID" : "FAILED";
        order.setPaymentStatus(status);
        order.setOrderStatus(status);
        orderRepository.save(order);

        PaymentWebhookEvent processed = new PaymentWebhookEvent();
        processed.setEventId(payload.getEventId());
        processed.setProvider(normalizedProvider);
        processed.setPaymentIntentId(payload.getPaymentIntentId());
        processed.setOutcome(status);
        processed.setProcessedAt(LocalDateTime.now());
        webhookEventRepository.save(processed);
        return new PaymentWebhookResultDTO(payload.getEventId(), order.getOrderId(), status, false);
    }

    private void validatePayload(PaymentWebhookPayload payload) {
        if (payload == null || isBlank(payload.getEventId()) || isBlank(payload.getPaymentIntentId())
                || payload.getAmount() == null || payload.getAmount() <= 0
                || isBlank(payload.getCurrency()) || isBlank(payload.getStatus())) {
            throw new IllegalArgumentException("Webhook event, intent, amount, currency and status are required");
        }
    }

    private boolean isValidSignature(String signature, String rawBody) {
        if (signature == null || signature.isBlank() || webhookSecret == null || webhookSecret.isBlank()) return false;
        String supplied = signature.startsWith("sha256=") ? signature.substring("sha256=".length()) : signature;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = HexFormat.of().formatHex(mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8)));
            return MessageDigest.isEqual(expected.getBytes(StandardCharsets.US_ASCII),
                    supplied.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII));
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
