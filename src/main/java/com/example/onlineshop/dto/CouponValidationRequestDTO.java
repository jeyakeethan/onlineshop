package com.example.onlineshop.dto;

import lombok.Data;

@Data
public class CouponValidationRequestDTO {
    private String couponCode;
    private Double subtotal;
    private String currency;
}
