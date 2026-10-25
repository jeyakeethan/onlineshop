package com.example.onlineshop.dto;

import lombok.Data;

@Data
public class CouponValidationDTO {
    private String couponCode;
    private boolean valid;
    private double discountAmount;
    private String message;
}
