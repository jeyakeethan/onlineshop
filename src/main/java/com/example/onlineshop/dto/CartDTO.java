package com.example.onlineshop.dto;

import com.example.onlineshop.model.Cart;
import com.example.onlineshop.model.CartItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDTO {

    private Long cartId;
    private String userId;
    private List<CartItemDTO> cartItems;

    public CartDTO(Cart cart) {
        this(cart, cart == null || cart.getUser() == null ? null : cart.getUser().getEmail());
    }

    public CartDTO(Cart cart, String userId) {
        this.cartId = cart == null ? null : cart.getCartId();
        this.userId = userId;
        this.cartItems = cart == null || cart.getCartItems() == null
                ? List.of()
                : cart.getCartItems().stream().map(CartItemDTO::new).toList();
    }
}
