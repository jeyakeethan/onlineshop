package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.ProviderReconciliationState;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.ReturnRequest;

public interface PaymentReconciliationProvider {
    ProviderReconciliationState getPaymentState(Order order);

    ProviderReconciliationState getRefundState(ReturnRequest refund);
}
