package com.example.onlineshop.order.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String paymentMethod;
    private String transactionId;
    private double amount;
}
