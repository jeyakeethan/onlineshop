package com.example.onlineshop.report.dto;

import java.time.LocalDate;
import java.util.Date;

public class SalesReportDTO {
    private Long orderId;
    private Long skuId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private LocalDate orderDate;

    // Constructor, getters, and setters
    public SalesReportDTO(Long orderId, Long skuId, Integer quantity, Double unitPrice, Double totalPrice, LocalDate orderDate) {
        this.orderId = orderId;
        this.skuId = skuId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    // Getters and setters
}
