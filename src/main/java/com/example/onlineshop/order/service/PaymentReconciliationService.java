package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.ProviderReconciliationState;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.PaymentReconciliationMismatch;
import com.example.onlineshop.order.entity.ReturnRequest;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.order.repository.PaymentReconciliationMismatchRepository;
import com.example.onlineshop.order.repository.ReturnRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReconciliationService {
    private final OrderRepository orderRepository;
    private final ReturnRequestRepository returnRequestRepository;
    private final PaymentReconciliationMismatchRepository mismatchRepository;
    private final PaymentReconciliationProvider provider;

    @Scheduled(fixedDelayString = "${app.payments.reconciliation.fixed-delay-ms:300000}")
    @Transactional
    public void reconcile() {
        reconcileNow();
    }

    @Transactional
    public int reconcileNow() {
        int mismatches = 0;
        List<Order> orders = orderRepository.findByPaymentStatusIn(
                List.of("REQUIRES_PAYMENT", "PAID", "FAILED", "REFUNDED"));
        for (Order order : orders) {
            ProviderReconciliationState state = provider.getPaymentState(order);
            if (!same(order.getPaymentStatus(), state.getPaymentStatus())) {
                mismatches++;
                recordMismatch("payment:" + order.getPaymentIntentId(), "PAYMENT", order.getPaymentIntentId(),
                        order.getPaymentStatus(), state.getPaymentStatus());
            } else {
                resolveMismatch("payment:" + order.getPaymentIntentId());
            }
        }

        for (ReturnRequest refund : returnRequestRepository.findByRefundStatus("EXECUTED")) {
            ProviderReconciliationState state = provider.getRefundState(refund);
            boolean matches = same(refund.getRefundStatus(), state.getRefundStatus())
                    && same(refund.getProviderRefundId(), state.getProviderRefundId());
            String key = "refund:" + refund.getReturnRequestId();
            if (!matches) {
                mismatches++;
                recordMismatch(key, "REFUND", String.valueOf(refund.getReturnRequestId()),
                        refund.getRefundStatus(), state.getRefundStatus());
            } else {
                resolveMismatch(key);
            }
        }
        return mismatches;
    }

    private void recordMismatch(String key, String resourceType, String resourceId,
                                String internalStatus, String providerStatus) {
        PaymentReconciliationMismatch mismatch = mismatchRepository.findByReconciliationKey(key)
                .orElseGet(PaymentReconciliationMismatch::new);
        mismatch.setReconciliationKey(key);
        mismatch.setProvider("mock");
        mismatch.setResourceType(resourceType);
        mismatch.setResourceId(resourceId);
        mismatch.setInternalStatus(internalStatus == null ? "UNKNOWN" : internalStatus);
        mismatch.setProviderStatus(providerStatus == null ? "UNKNOWN" : providerStatus);
        mismatch.setDetectedAt(LocalDateTime.now());
        mismatch.setResolvedAt(null);
        mismatchRepository.save(mismatch);
        log.warn("Payment reconciliation mismatch: {} internal={} provider={}",
                key, mismatch.getInternalStatus(), mismatch.getProviderStatus());
    }

    private void resolveMismatch(String key) {
        mismatchRepository.findByReconciliationKey(key).ifPresent(existing -> {
            if (existing.getResolvedAt() == null) {
                existing.setResolvedAt(LocalDateTime.now());
                mismatchRepository.save(existing);
            }
        });
    }

    private boolean same(String left, String right) {
        return left == null ? right == null : left.equalsIgnoreCase(right);
    }
}
