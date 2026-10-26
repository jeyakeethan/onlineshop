package com.example.onlineshop.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_reconciliation_mismatches")
@Getter
@Setter
public class PaymentReconciliationMismatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 220)
    private String reconciliationKey;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(nullable = false, length = 32)
    private String resourceType;

    @Column(nullable = false, length = 160)
    private String resourceId;

    @Column(nullable = false, length = 32)
    private String internalStatus;

    @Column(nullable = false, length = 32)
    private String providerStatus;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    private LocalDateTime resolvedAt;
}
