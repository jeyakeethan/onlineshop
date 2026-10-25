package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class MockPaymentCompletionDTO {
    private final Long orderId;
    private final String paymentIntentId;
    private final String orderStatus;
    private final String paymentStatus;

    public MockPaymentCompletionDTO(Long orderId, String paymentIntentId, String orderStatus, String paymentStatus) {
        this.orderId = orderId;
        this.paymentIntentId = paymentIntentId;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
    }
}
