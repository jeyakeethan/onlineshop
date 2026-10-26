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
@Table(name = "payment_webhook_events")
@Getter
@Setter
public class PaymentWebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 160)
    private String eventId;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(nullable = false, length = 100)
    private String paymentIntentId;

    @Column(nullable = false, length = 32)
    private String outcome;

    @Column(nullable = false)
    private LocalDateTime processedAt;
}
