package com.example.onlineshop.order.repository;

import com.example.onlineshop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_email(String userId);
    Page<Order> findByUser_email(String userId, Pageable pageable);
    Page<Order> findByUser_emailAndOrderStatusIgnoreCase(String userId, String orderStatus, Pageable pageable);
    java.util.Optional<Order> findByUser_emailAndIdempotencyKey(String userId, String idempotencyKey);
    java.util.Optional<Order> findByPaymentIntentId(String paymentIntentId);
    java.util.Optional<Order> findByGuestOrderReferenceAndGuestEmailIgnoreCase(String orderReference, String email);
}
