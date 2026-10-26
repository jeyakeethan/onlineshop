package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.ProviderReconciliationState;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.ReturnRequest;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentReconciliationProvider implements PaymentReconciliationProvider {
    @Override
    public ProviderReconciliationState getPaymentState(Order order) {
        return new ProviderReconciliationState(order.getPaymentStatus(), null, null);
    }

    @Override
    public ProviderReconciliationState getRefundState(ReturnRequest refund) {
        return new ProviderReconciliationState(refund.getOrder().getPaymentStatus(),
                refund.getRefundStatus(), refund.getProviderRefundId());
    }
}
