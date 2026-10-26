package com.example.onlineshop.order.service;

import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.config.entity.StoreSettings;
import com.example.onlineshop.config.repository.StoreSettingsRepository;
import com.example.onlineshop.order.dto.PaymentIntentCreateRequest;
import com.example.onlineshop.order.dto.PaymentIntentDTO;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaymentIntentService {
    private final OrderRepository orderRepository;
    private final AuthenticationHelper authenticationHelper;
    private final StoreSettingsRepository storeSettingsRepository;

    @Value("${app.payments.mock.enabled:true}")
    private boolean mockPaymentsEnabled = true;

    @Transactional
    public PaymentIntentDTO create(PaymentIntentCreateRequest request, String idempotencyKey) {
        if (request == null || request.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key is required");
        }

        String userEmail = authenticationHelper.getCurrentUserId();
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (order.getUser() == null || !userEmail.equalsIgnoreCase(order.getUser().getEmail())) {
            throw new AccessDeniedException("Order does not belong to the authenticated user");
        }
        if ("EXPIRED".equalsIgnoreCase(order.getPaymentStatus())
                || "EXPIRED".equalsIgnoreCase(order.getOrderStatus())
                || "CANCELLED".equalsIgnoreCase(order.getOrderStatus())
                || "FAILED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new IllegalStateException("Payment cannot be created for this order");
        }
        if (order.getTotalPrice() <= 0) {
            throw new IllegalStateException("Order total must be positive");
        }

        String provider = request.getProvider() == null || request.getProvider().isBlank()
                ? "mock" : request.getProvider().trim().toLowerCase(Locale.ROOT);
        if (!"mock".equals(provider) || !mockPaymentsEnabled) {
            throw new IllegalStateException("The requested payment provider is not configured");
        }

        String configuredCurrency = storeSettingsRepository.findById(1L)
                .map(StoreSettings::getCurrency)
                .filter(value -> value != null && !value.isBlank())
                .orElse("USD").toUpperCase(Locale.ROOT);
        String currency = request.getCurrency() == null || request.getCurrency().isBlank()
                ? configuredCurrency : request.getCurrency().trim().toUpperCase(Locale.ROOT);
        if (!configuredCurrency.equals(currency)) {
            throw new IllegalArgumentException("Payment currency does not match store currency");
        }

        order.setPaymentProvider(provider);
        order.setPaymentCurrency(currency);
        orderRepository.save(order);
        return toDto(order);
    }

    private PaymentIntentDTO toDto(Order order) {
        return new PaymentIntentDTO(order.getOrderId(), order.getPaymentIntentId(),
                order.getPaymentProvider(), order.getPaymentCurrency(), order.getTotalPrice(),
                order.getPaymentStatus(), "mock_client_secret_" + order.getPaymentIntentId());
    }
}
