package com.example.onlineshop.dto;

import lombok.Data;

@Data
public class ShippingMethodDTO {
    private String code;
    private String label;
    private double fee;
    private String estimate;
    private boolean freeShippingEligible;
}
