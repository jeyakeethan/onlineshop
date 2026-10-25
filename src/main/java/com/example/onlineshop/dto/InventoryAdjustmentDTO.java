package com.example.onlineshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryAdjustmentDTO {
    @NotNull
    private Integer quantityDelta;

    @NotBlank
    private String reason;

    private String reference;
}