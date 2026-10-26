package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class PaymentStatusDTO {
    private final Long orderId;
    private final String paymentIntentId;
    private final String paymentStatus;
    private final String orderStatus;

    public PaymentStatusDTO(Long orderId, String paymentIntentId, String paymentStatus, String orderStatus) {
        this.orderId = orderId;
        this.paymentIntentId = paymentIntentId;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
    }
}
