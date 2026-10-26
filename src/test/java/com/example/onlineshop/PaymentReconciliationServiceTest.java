package com.example.onlineshop;

import com.example.onlineshop.order.dto.ProviderReconciliationState;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.PaymentReconciliationMismatch;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.order.repository.PaymentReconciliationMismatchRepository;
import com.example.onlineshop.order.repository.ReturnRequestRepository;
import com.example.onlineshop.order.service.PaymentReconciliationProvider;
import com.example.onlineshop.order.service.PaymentReconciliationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentReconciliationServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @Mock
    private PaymentReconciliationMismatchRepository mismatchRepository;

    @Mock
    private PaymentReconciliationProvider provider;

    @InjectMocks
    private PaymentReconciliationService reconciliationService;

    @Test
    void reconcile_recordsPaymentMismatchForOperationalAlerting() {
        Order order = order("pi-168", "PAID");
        when(orderRepository.findByPaymentStatusIn(any())).thenReturn(List.of(order));
        when(returnRequestRepository.findByRefundStatus("EXECUTED")).thenReturn(List.of());
        when(provider.getPaymentState(order)).thenReturn(new ProviderReconciliationState("FAILED", null, null));
        when(mismatchRepository.findByReconciliationKey("payment:pi-168")).thenReturn(Optional.empty());

        int mismatches = reconciliationService.reconcileNow();

        assertEquals(1, mismatches);
        verify(mismatchRepository).save(any(PaymentReconciliationMismatch.class));
    }

    @Test
    void reconcile_resolvesPreviouslyRecordedMismatchWhenProviderMatches() {
        Order order = order("pi-168-resolved", "PAID");
        PaymentReconciliationMismatch existing = new PaymentReconciliationMismatch();
        existing.setReconciliationKey("payment:pi-168-resolved");
        existing.setResolvedAt(null);
        when(orderRepository.findByPaymentStatusIn(any())).thenReturn(List.of(order));
        when(returnRequestRepository.findByRefundStatus("EXECUTED")).thenReturn(List.of());
        when(provider.getPaymentState(order)).thenReturn(new ProviderReconciliationState("PAID", null, null));
        when(mismatchRepository.findByReconciliationKey("payment:pi-168-resolved"))
                .thenReturn(Optional.of(existing));

        int mismatches = reconciliationService.reconcileNow();

        assertEquals(0, mismatches);
        assertNotNull(existing.getResolvedAt());
        verify(mismatchRepository).save(existing);
    }

    @Test
    void reconcile_doesNotCreateMismatchForMatchingProviderState() {
        Order order = order("pi-168-match", "PAID");
        when(orderRepository.findByPaymentStatusIn(any())).thenReturn(List.of(order));
        when(returnRequestRepository.findByRefundStatus("EXECUTED")).thenReturn(List.of());
        when(provider.getPaymentState(order)).thenReturn(new ProviderReconciliationState("PAID", null, null));
        when(mismatchRepository.findByReconciliationKey("payment:pi-168-match")).thenReturn(Optional.empty());

        reconciliationService.reconcileNow();

        verify(mismatchRepository, never()).save(any(PaymentReconciliationMismatch.class));
    }

    private Order order(String intentId, String status) {
        Order order = new Order();
        order.setOrderId(168L);
        order.setPaymentIntentId(intentId);
        order.setPaymentStatus(status);
        return order;
    }
}
