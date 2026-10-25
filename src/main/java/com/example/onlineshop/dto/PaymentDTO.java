package com.example.onlineshop.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String paymentMethod;
    private String transactionId;
    private double amount;
}
