package com.example.onlineshop.order.dto;

import lombok.Data;

@Data
public class CouponValidationRequestDTO {
    private String couponCode;
    private Double subtotal;
    private String currency;
}
