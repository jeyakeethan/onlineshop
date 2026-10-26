package com.example.onlineshop;

import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.order.service.PaymentStatusService;
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
class PaymentStatusServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private PaymentStatusService paymentStatusService;

    @Test
    void getStatus_returnsOwnPaymentStateWithoutChangingIt() {
        Order order = order(166L, "buyer@example.com");
        when(orderRepository.findById(166L)).thenReturn(Optional.of(order));
        when(authenticationHelper.getCurrentUserId()).thenReturn("buyer@example.com");

        var result = paymentStatusService.getStatus(166L);

        assertEquals(166L, result.getOrderId());
        assertEquals("pi-166", result.getPaymentIntentId());
        assertEquals("REQUIRES_PAYMENT", result.getPaymentStatus());
        assertEquals("PENDING", result.getOrderStatus());
        verify(orderRepository).findById(166L);
        verify(authenticationHelper).getCurrentUserId();
    }

    @Test
    void getStatus_rejectsAnotherUsersOrder() {
        when(orderRepository.findById(166L)).thenReturn(Optional.of(order(166L, "owner@example.com")));
        when(authenticationHelper.getCurrentUserId()).thenReturn("other@example.com");

        assertThrows(AccessDeniedException.class, () -> paymentStatusService.getStatus(166L));
    }

    @Test
    void getStatus_rejectsMissingOrder() {
        when(orderRepository.findById(166L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> paymentStatusService.getStatus(166L));
        verifyNoInteractions(authenticationHelper);
    }

    private Order order(long id, String email) {
        User user = new User();
        user.setEmail(email);
        Order order = new Order();
        order.setOrderId(id);
        order.setUser(user);
        order.setPaymentIntentId("pi-" + id);
        order.setPaymentStatus("REQUIRES_PAYMENT");
        order.setOrderStatus("PENDING");
        return order;
    }
}
