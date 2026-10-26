package com.example.onlineshop;

import com.example.onlineshop.order.dto.PaymentWebhookPayload;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.PaymentWebhookEvent;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.order.repository.PaymentWebhookEventRepository;
import com.example.onlineshop.order.service.PaymentWebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookServiceTest {
    private static final String SECRET = "test-webhook-secret";

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentWebhookEventRepository webhookEventRepository;

    private PaymentWebhookService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = new PaymentWebhookService(objectMapper, orderRepository, webhookEventRepository);
        ReflectionTestUtils.setField(service, "webhookSecret", SECRET);
    }

    @Test
    void process_acceptsValidSignatureAndTransitionsOrder() throws Exception {
        Order order = order("pi-165", 42.50);
        PaymentWebhookPayload payload = payload("evt-165", "pi-165", 42.50, "SUCCEEDED");
        String body = objectMapper.writeValueAsString(payload);
        when(webhookEventRepository.findByEventId("evt-165")).thenReturn(Optional.empty());
        when(orderRepository.findByPaymentIntentId("pi-165")).thenReturn(Optional.of(order));

        var result = service.process("mock", sign(body), body);

        assertFalse(result.isReplay());
        assertEquals("PAID", result.getPaymentStatus());
        assertEquals("PAID", order.getPaymentStatus());
        assertEquals("PAID", order.getOrderStatus());
        verify(orderRepository).save(order);
        verify(webhookEventRepository).save(any(PaymentWebhookEvent.class));
    }

    @Test
    void process_replaysKnownEventWithoutMutatingOrderAgain() throws Exception {
        Order order = order("pi-165-replay", 20.00);
        PaymentWebhookPayload payload = payload("evt-165-replay", "pi-165-replay", 20.00, "SUCCEEDED");
        String body = objectMapper.writeValueAsString(payload);
        when(webhookEventRepository.findByEventId("evt-165-replay"))
                .thenReturn(Optional.of(new PaymentWebhookEvent()));
        when(orderRepository.findByPaymentIntentId("pi-165-replay")).thenReturn(Optional.of(order));

        var result = service.process("mock", sign(body), body);

        assertTrue(result.isReplay());
        assertEquals("REQUIRES_PAYMENT", result.getPaymentStatus());
        verify(orderRepository, never()).save(any(Order.class));
        verify(webhookEventRepository, never()).save(any(PaymentWebhookEvent.class));
    }

    @Test
    void process_rejectsInvalidSignatureAndAmountMismatch() throws Exception {
        PaymentWebhookPayload payload = payload("evt-165-invalid", "pi-165-invalid", 20.00, "SUCCEEDED");
        String body = objectMapper.writeValueAsString(payload);

        assertThrows(AccessDeniedException.class,
                () -> service.process("mock", "sha256=wrong", body));

        Order order = order("pi-165-invalid", 21.00);
        when(webhookEventRepository.findByEventId("evt-165-invalid")).thenReturn(Optional.empty());
        when(orderRepository.findByPaymentIntentId("pi-165-invalid")).thenReturn(Optional.of(order));
        assertThrows(IllegalArgumentException.class,
                () -> service.process("mock", sign(body), body));
        verify(orderRepository, never()).save(any(Order.class));
    }

    private Order order(String intentId, double total) {
        Order order = new Order();
        order.setOrderId(165L);
        order.setPaymentIntentId(intentId);
        order.setPaymentCurrency("USD");
        order.setPaymentStatus("REQUIRES_PAYMENT");
        order.setOrderStatus("PENDING");
        order.setTotalPrice(total);
        return order;
    }

    private PaymentWebhookPayload payload(String eventId, String intentId, double amount, String status) {
        PaymentWebhookPayload payload = new PaymentWebhookPayload();
        payload.setEventId(eventId);
        payload.setPaymentIntentId(intentId);
        payload.setAmount(amount);
        payload.setCurrency("USD");
        payload.setStatus(status);
        return payload;
    }

    private String sign(String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return "sha256=" + HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
    }
}
