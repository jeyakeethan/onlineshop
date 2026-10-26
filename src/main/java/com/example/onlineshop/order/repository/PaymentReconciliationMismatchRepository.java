package com.example.onlineshop.order.repository;

import com.example.onlineshop.order.entity.PaymentReconciliationMismatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentReconciliationMismatchRepository extends JpaRepository<PaymentReconciliationMismatch, Long> {
    Optional<PaymentReconciliationMismatch> findByReconciliationKey(String reconciliationKey);
}
