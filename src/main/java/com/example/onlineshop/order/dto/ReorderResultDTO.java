package com.example.onlineshop.order.dto;

import com.example.onlineshop.cart.dto.CartDTO;
import lombok.Getter;
import java.util.List;

@Getter
public class ReorderResultDTO {
    private final CartDTO cart;
    private final List<String> addedSkuCodes;
    private final List<String> unavailableSkuCodes;

    public ReorderResultDTO(CartDTO cart, List<String> addedSkuCodes, List<String> unavailableSkuCodes) {
        this.cart = cart;
        this.addedSkuCodes = List.copyOf(addedSkuCodes);
        this.unavailableSkuCodes = List.copyOf(unavailableSkuCodes);
    }
}
