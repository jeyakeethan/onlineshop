package com.example.onlineshop;

import com.example.onlineshop.order.controller.AdminRefundController;
import com.example.onlineshop.order.dto.RefundExecutionRequest;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.ReturnRequest;
import com.example.onlineshop.order.repository.ReturnRequestRepository;
import com.example.onlineshop.order.service.RefundExecutionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundExecutionServiceTest {
    @Mock
    private ReturnRequestRepository returnRequestRepository;

    @InjectMocks
    private RefundExecutionService refundExecutionService;

    @Test
    void execute_runsProviderRefundAndReplaysSameIdempotencyKey() {
        ReturnRequest refund = refund(167L, "RESOLVED", "REFUND", 25.50);
        when(returnRequestRepository.findById(167L)).thenReturn(Optional.of(refund));
        when(returnRequestRepository.save(refund)).thenReturn(refund);
        RefundExecutionRequest request = new RefundExecutionRequest();
        request.setProvider("mock");

        var result = refundExecutionService.execute(167L, request, "refund-167");
        var replay = refundExecutionService.execute(167L, request, "refund-167");

        assertFalse(result.isReplay());
        assertTrue(replay.isReplay());
        assertEquals("EXECUTED", refund.getRefundStatus());
        assertEquals("REFUNDED", refund.getOrder().getPaymentStatus());
        assertEquals(result.getProviderRefundId(), replay.getProviderRefundId());
        verify(returnRequestRepository, times(1)).save(refund);
    }

    @Test
    void execute_rejectsDifferentReplayKeyAndIneligibleDecision() {
        ReturnRequest executed = refund(168L, "RESOLVED", "REFUND", 10.00);
        executed.setRefundStatus("EXECUTED");
        executed.setRefundIdempotencyKey("original-key");
        when(returnRequestRepository.findById(168L)).thenReturn(Optional.of(executed));
        assertThrows(IllegalStateException.class,
                () -> refundExecutionService.execute(168L, null, "other-key"));

        ReturnRequest pending = refund(169L, "RECEIVED", "REFUND", 10.00);
        when(returnRequestRepository.findById(169L)).thenReturn(Optional.of(pending));
        assertThrows(IllegalStateException.class,
                () -> refundExecutionService.execute(169L, null, "refund-169"));
        verify(returnRequestRepository, never()).save(pending);
    }

    @Test
    void execute_requiresPositiveAmountAndIdempotencyKey() {
        ReturnRequest refund = refund(170L, "RESOLVED", "REFUND", 0.0);
        when(returnRequestRepository.findById(170L)).thenReturn(Optional.of(refund));

        assertThrows(IllegalArgumentException.class,
                () -> refundExecutionService.execute(170L, null, " "));
        assertThrows(IllegalArgumentException.class,
                () -> refundExecutionService.execute(170L, null, "refund-170"));
    }

    @Test
    void adminEndpoint_requiresRefundExecutePermission() throws Exception {
        PreAuthorize authorization = AdminRefundController.class
                .getDeclaredMethod("executeRefund", Long.class, String.class, RefundExecutionRequest.class)
                .getAnnotation(PreAuthorize.class);
        assertNotNull(authorization);
        assertTrue(authorization.value().contains("refund:execute"));
    }

    private ReturnRequest refund(long id, String status, String decision, double amount) {
        Order order = new Order();
        order.setOrderId(id + 1000);
        order.setPaymentStatus("PAID");
        order.setOrderStatus("DELIVERED");
        ReturnRequest refund = new ReturnRequest();
        refund.setReturnRequestId(id);
        refund.setStatus(status);
        refund.setRefundDecision(decision);
        refund.setRefundAmount(amount);
        refund.setOrder(order);
        return refund;
    }
}
