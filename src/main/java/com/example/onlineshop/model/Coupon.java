package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String type = "PERCENTAGE";

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private double minSpend = 0.0;

    private double maxDiscount = 0.0;

    @Column(nullable = false)
    private int usageLimit = 0;

    @Column(nullable = false)
    private int usedCount = 0;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean stackable = false;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
