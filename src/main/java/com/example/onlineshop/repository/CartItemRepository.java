package com.example.onlineshop.repository;

import com.example.onlineshop.model.Cart;
import com.example.onlineshop.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
}