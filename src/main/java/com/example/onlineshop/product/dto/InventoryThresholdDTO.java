package com.example.onlineshop.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryThresholdDTO {
    @NotNull
    @Min(0)
    private Integer lowStockThreshold;
}
