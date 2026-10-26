package com.example.onlineshop.order.repository;

import com.example.onlineshop.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_email(String userId);
    Page<Order> findByUser_email(String userId, Pageable pageable);
    Page<Order> findByUser_emailAndOrderStatusIgnoreCase(String userId, String orderStatus, Pageable pageable);
    java.util.Optional<Order> findByUser_emailAndIdempotencyKey(String userId, String idempotencyKey);
    java.util.Optional<Order> findByPaymentIntentId(String paymentIntentId);
    List<Order> findByPaymentStatusIn(Collection<String> paymentStatuses);
    java.util.Optional<Order> findByGuestOrderReferenceAndGuestEmailIgnoreCase(String orderReference, String email);
    List<Order> findByOrderDateBetween(LocalDate from, LocalDate to);
    Page<Order> findByOrderDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Order> findByReservationExpiresAtLessThanEqualAndPaymentStatusNotIn(
            LocalDateTime expiresAt, Collection<String> excludedPaymentStatuses);
}
