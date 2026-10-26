package com.example.onlineshop.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentAttemptDTO {
    private final String paymentIntentId;
    private final String status;
    private final LocalDateTime changedAt;
    private final String note;

    public PaymentAttemptDTO(String paymentIntentId, String status, LocalDateTime changedAt, String note) {
        this.paymentIntentId = paymentIntentId;
        this.status = status;
        this.changedAt = changedAt;
        this.note = note;
    }
}
