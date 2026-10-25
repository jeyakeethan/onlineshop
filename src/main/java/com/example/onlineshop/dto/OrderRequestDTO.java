package com.example.onlineshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Setter
public class OrderRequestDTO {
    @Schema(description = "Authenticated user email; omit this field when using the logged-in user", example = "buyer@example.com")
    private String userId;

    @NotEmpty
    @Valid
    @Schema(description = "Inventory items to reserve")
    private List<OrderItemRequestDTO> items;

    @Valid
    private AddressDTO shippingAddress;

    private String shippingMethod;
    private Double quotedTotal;
    private LocalDateTime quoteExpiresAt;

    @Getter
    @Setter
    public static class OrderItemRequestDTO {
        @NotNull
        @Schema(description = "Inventory record ID, not the product ID", example = "1")
        private Long inventoryId;

        @Min(1)
        @Schema(description = "Number of units to reserve", example = "1", minimum = "1")
        private int quantity;
    }
}
