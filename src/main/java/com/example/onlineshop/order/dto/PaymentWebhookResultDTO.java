package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class PaymentWebhookResultDTO {
    private final String eventId;
    private final Long orderId;
    private final String paymentStatus;
    private final boolean replay;

    public PaymentWebhookResultDTO(String eventId, Long orderId, String paymentStatus, boolean replay) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.replay = replay;
    }
}
