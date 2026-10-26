package com.example.onlineshop.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "return_requests")
@Getter
@Setter
@NoArgsConstructor
public class ReturnRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnRequestId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, length = 32)
    private String status = "REQUESTED";

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column(length = 500)
    private String decisionReason;

    private LocalDateTime reviewedAt;

    @Column(length = 255)
    private String reviewedBy;

    @Column(length = 1000)
    private String inspectionNotes;

    private LocalDateTime receivedAt;

    @Column(length = 255)
    private String receivedBy;

    @Column(length = 32)
    private String refundDecision;

    private double refundAmount;

    @Column(length = 32)
    private String refundStatus = "NOT_EXECUTED";

    @Column(length = 32)
    private String refundProvider;

    @Column(length = 160)
    private String refundIdempotencyKey;

    @Column(length = 160)
    private String providerRefundId;

    private LocalDateTime refundExecutedAt;

    private LocalDateTime resolvedAt;

    @Column(length = 255)
    private String resolvedBy;

    @OneToMany(mappedBy = "returnRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReturnRequestItem> items = new ArrayList<>();
}
