package com.example.onlineshop.order.dto;

import lombok.Getter;

@Getter
public class PaymentIntentDTO {
    private final Long orderId;
    private final String paymentIntentId;
    private final String provider;
    private final String currency;
    private final double amount;
    private final String status;
    private final String clientSecret;

    public PaymentIntentDTO(Long orderId, String paymentIntentId, String provider,
                            String currency, double amount, String status, String clientSecret) {
        this.orderId = orderId;
        this.paymentIntentId = paymentIntentId;
        this.provider = provider;
        this.currency = currency;
        this.amount = amount;
        this.status = status;
        this.clientSecret = clientSecret;
    }
}
