package com.example.onlineshop.cart.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartItemRequestDTO {
    private Long inventoryId;
    private Long variantId;
    private Integer quantity;
}
