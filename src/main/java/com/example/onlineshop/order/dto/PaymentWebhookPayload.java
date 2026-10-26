package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentWebhookPayload {
    private String eventId;
    private String paymentIntentId;
    private Double amount;
    private String currency;
    private String status;
}
