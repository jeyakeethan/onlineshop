package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MockPaymentCompletionRequest {
    private String paymentIntentId;
    private String outcome;
    private Double amount;
}
