package com.example.onlineshop.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CheckoutQuoteDTO {
    private double subtotal;
    private double discount;
    private double shippingFee;
    private double tax;
    private double total;
    private String shippingMethod;
    private String currency = "USD";
    private LocalDateTime expiresAt;
    private boolean freeShipping;
    private List<String> warnings = new ArrayList<>();
}
