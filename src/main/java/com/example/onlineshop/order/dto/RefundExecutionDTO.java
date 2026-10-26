package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class RefundExecutionDTO {
    private final Long refundId;
    private final Long orderId;
    private final String provider;
    private final String providerRefundId;
    private final double amount;
    private final String status;
    private final boolean replay;

    public RefundExecutionDTO(Long refundId, Long orderId, String provider, String providerRefundId,
                              double amount, String status, boolean replay) {
        this.refundId = refundId;
        this.orderId = orderId;
        this.provider = provider;
        this.providerRefundId = providerRefundId;
        this.amount = amount;
        this.status = status;
        this.replay = replay;
    }
}
