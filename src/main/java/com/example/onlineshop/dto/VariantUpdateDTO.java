package com.example.onlineshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class VariantUpdateDTO {
    private String skuCode;
    private String size;
    private String color;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
}