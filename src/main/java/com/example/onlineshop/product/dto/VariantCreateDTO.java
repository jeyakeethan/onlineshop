package com.example.onlineshop.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class VariantCreateDTO {

    @NotBlank
    private String skuCode;

    @NotBlank
    private String size;

    @NotBlank
    private String color;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal costPrice = BigDecimal.ZERO;
}
