package com.example.onlineshop;

import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.config.entity.StoreSettings;
import com.example.onlineshop.config.repository.StoreSettingsRepository;
import com.example.onlineshop.order.dto.PaymentIntentCreateRequest;
import com.example.onlineshop.order.dto.PaymentIntentDTO;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.service.PaymentIntentService;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentIntentServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @Mock
    private StoreSettingsRepository storeSettingsRepository;

    @InjectMocks
    private PaymentIntentService paymentIntentService;

    @Test
    void create_usesPersistedOrderAmountAndReplaysStableIntent() {
        Order order = order(164L, "buyer@example.com", 126.75);
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(164L)).thenReturn(Optional.of(order));
        when(storeSettingsRepository.findById(1L)).thenReturn(Optional.of(settings("USD")));

        PaymentIntentCreateRequest request = request(164L, "mock", "usd");
        PaymentIntentDTO first = paymentIntentService.create(request, "payment-164");
        PaymentIntentDTO replay = paymentIntentService.create(request, "payment-164");

        assertEquals(126.75, first.getAmount());
        assertEquals(first.getPaymentIntentId(), replay.getPaymentIntentId());
        assertEquals("USD", first.getCurrency());
        assertEquals("mock", first.getProvider());
        assertEquals("REQUIRES_PAYMENT", first.getStatus());
        verify(orderRepository, times(2)).save(order);
    }

    @Test
    void create_rejectsMissingIdempotencyAndUnsupportedProviderBeforeSaving() {
        PaymentIntentCreateRequest request = request(164L, "stripe", "USD");

        assertThrows(IllegalArgumentException.class,
                () -> paymentIntentService.create(request, " "));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");
        when(orderRepository.findById(164L)).thenReturn(Optional.of(order(164L, "buyer@example.com", 20.00)));
        assertThrows(IllegalStateException.class,
                () -> paymentIntentService.create(request, "payment-164"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void create_rejectsForeignOrderAndCurrencyMismatch() {
        Order order = order(165L, "owner@example.com", 50.00);
        when(authenticationHelper.getCurrentUserId()).thenReturn("other@example.com");
        when(orderRepository.findById(165L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedException.class,
                () -> paymentIntentService.create(request(165L, "mock", "USD"), "payment-165"));

        when(authenticationHelper.getCurrentUserId()).thenReturn("owner@example.com");
        when(storeSettingsRepository.findById(1L)).thenReturn(Optional.of(settings("USD")));
        assertThrows(IllegalArgumentException.class,
                () -> paymentIntentService.create(request(165L, "mock", "EUR"), "payment-165"));
        verify(orderRepository, never()).save(order);
    }

    private Order order(long id, String email, double total) {
        User user = new User();
        user.setEmail(email);
        Order order = new Order();
        order.setOrderId(id);
        order.setUser(user);
        order.setTotalPrice(total);
        order.setPaymentStatus("REQUIRES_PAYMENT");
        order.setOrderStatus("PENDING");
        return order;
    }

    private PaymentIntentCreateRequest request(long orderId, String provider, String currency) {
        PaymentIntentCreateRequest request = new PaymentIntentCreateRequest();
        request.setOrderId(orderId);
        request.setProvider(provider);
        request.setCurrency(currency);
        return request;
    }

    private StoreSettings settings(String currency) {
        StoreSettings settings = new StoreSettings();
        settings.setSettingsId(1L);
        settings.setStoreName("Online Shop");
        settings.setCurrency(currency);
        return settings;
    }
}
