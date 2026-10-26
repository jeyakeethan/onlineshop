package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentIntentCreateRequest {
    private Long orderId;
    private String provider;
    private String currency;
}
