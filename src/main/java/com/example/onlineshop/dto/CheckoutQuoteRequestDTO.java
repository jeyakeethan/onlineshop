package com.example.onlineshop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutQuoteRequestDTO {
    private List<CartItemRequestDTO> items;
    private String shippingMethod;
    private String couponCode;
    private String addressId;
    private String country;
    private String postalCode;
}
