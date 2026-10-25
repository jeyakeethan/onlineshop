package com.example.onlineshop.repository;

import com.example.onlineshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_email(String userId);
    java.util.Optional<Order> findByUser_emailAndIdempotencyKey(String userId, String idempotencyKey);
}
