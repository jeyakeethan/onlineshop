package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class ProviderReconciliationState {
    private final String paymentStatus;
    private final String refundStatus;
    private final String providerRefundId;

    public ProviderReconciliationState(String paymentStatus, String refundStatus, String providerRefundId) {
        this.paymentStatus = paymentStatus;
        this.refundStatus = refundStatus;
        this.providerRefundId = providerRefundId;
    }
}
