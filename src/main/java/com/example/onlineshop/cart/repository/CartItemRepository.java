package com.example.onlineshop.cart.repository;

import com.example.onlineshop.cart.entity.Cart;
import com.example.onlineshop.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
}
