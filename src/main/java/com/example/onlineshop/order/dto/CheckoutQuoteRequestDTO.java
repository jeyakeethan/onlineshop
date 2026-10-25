package com.example.onlineshop.order.dto;

import com.example.onlineshop.cart.dto.CartItemRequestDTO;
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
