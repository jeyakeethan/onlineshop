package com.example.onlineshop.dto;

import com.example.onlineshop.model.CartItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {

    private Long cartItemId;
    private Long inventoryId;
    private int quantity;
    private double priceAtAdd;
    private double currentPrice;
    private int availableQuantity;
    private boolean stockWarning;
    private boolean unavailable;

    public CartItemDTO (CartItem cartItem) {
        this.cartItemId = cartItem.getCartItemId();
        this.inventoryId = cartItem.getInventory().getInventoryId();
        this.quantity = cartItem.getQuantity();
        this.priceAtAdd = cartItem.getInventory().getSellingPrice();
        this.currentPrice = cartItem.getInventory().getSellingPrice();
        boolean productAvailable = cartItem.getInventory().getSku().isActive()
                && cartItem.getInventory().getSku().getProduct().isApproved();
        this.availableQuantity = productAvailable ? cartItem.getInventory().getStockAvailable() : 0;
        this.stockWarning = quantity > availableQuantity;
        this.unavailable = !productAvailable || availableQuantity == 0;
    }
}
