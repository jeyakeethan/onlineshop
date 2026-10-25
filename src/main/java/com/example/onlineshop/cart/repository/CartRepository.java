package com.example.onlineshop.cart.repository;

import com.example.onlineshop.cart.dto.CartDTO;
import com.example.onlineshop.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserEmail(String email);

}
