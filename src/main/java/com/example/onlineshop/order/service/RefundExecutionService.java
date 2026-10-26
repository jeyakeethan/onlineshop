package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.RefundExecutionDTO;
import com.example.onlineshop.order.dto.RefundExecutionRequest;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.ReturnRequest;
import com.example.onlineshop.order.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RefundExecutionService {
    private final ReturnRequestRepository returnRequestRepository;
    @Value("${app.payments.mock.enabled:true}")
    private boolean mockPaymentsEnabled = true;

    @Transactional
    public RefundExecutionDTO execute(Long refundId, RefundExecutionRequest request, String idempotencyKey) {
        if (refundId == null) throw new IllegalArgumentException("Refund ID is required");
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key is required");
        }
        ReturnRequest refund = returnRequestRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("Refund not found"));
        String provider = request == null || request.getProvider() == null || request.getProvider().isBlank()
                ? "mock" : request.getProvider().trim().toLowerCase(Locale.ROOT);
        if (!"mock".equals(provider) || !mockPaymentsEnabled) {
            throw new IllegalStateException("The requested refund provider is not configured");
        }

        if ("EXECUTED".equalsIgnoreCase(refund.getRefundStatus())) {
            if (!idempotencyKey.equals(refund.getRefundIdempotencyKey())) {
                throw new IllegalStateException("This refund has already been executed");
            }
            return toDto(refund, true);
        }
        if (!"RESOLVED".equalsIgnoreCase(refund.getStatus())
                || !"REFUND".equalsIgnoreCase(refund.getRefundDecision())) {
            throw new IllegalStateException("Only resolved refund decisions can be executed");
        }
        if (refund.getRefundAmount() <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }
        Order order = refund.getOrder();
        if (order == null || !"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new IllegalStateException("The order is not eligible for a refund");
        }

        refund.setRefundProvider(provider);
        refund.setRefundIdempotencyKey(idempotencyKey);
        refund.setProviderRefundId("mock_refund_" + refundId + "_" + shortHash(idempotencyKey));
        refund.setRefundStatus("EXECUTED");
        refund.setRefundExecutedAt(java.time.LocalDateTime.now());
        order.setPaymentStatus("REFUNDED");
        order.setOrderStatus("REFUNDED");
        return toDto(returnRequestRepository.save(refund), false);
    }

    private RefundExecutionDTO toDto(ReturnRequest refund, boolean replay) {
        return new RefundExecutionDTO(refund.getReturnRequestId(), refund.getOrder().getOrderId(),
                refund.getRefundProvider(), refund.getProviderRefundId(), refund.getRefundAmount(),
                refund.getRefundStatus(), replay);
    }

    private String shortHash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 8; i++) result.append(String.format("%02x", digest[i]));
            return result.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create provider idempotency reference", exception);
        }
    }
}
