package com.example.onlineshop.promotion.dto;

import com.example.onlineshop.promotion.entity.Coupon;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponDTO {
    private Long couponId;
    private String code;
    private String description;
    private String type = "PERCENTAGE";
    private double value;
    private double minSpend;
    private double maxDiscount;
    private int usageLimit;
    private int usedCount;
    private Boolean active;
    private Boolean stackable;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public CouponDTO() {
    }

    public CouponDTO(Coupon coupon) {
        if (coupon == null) {
            return;
        }
        this.couponId = coupon.getCouponId();
        this.code = coupon.getCode();
        this.description = coupon.getDescription();
        this.type = coupon.getType();
        this.value = coupon.getValue();
        this.minSpend = coupon.getMinSpend();
        this.maxDiscount = coupon.getMaxDiscount();
        this.usageLimit = coupon.getUsageLimit();
        this.usedCount = coupon.getUsedCount();
        this.active = coupon.isActive();
        this.stackable = coupon.isStackable();
        this.startAt = coupon.getStartAt();
        this.endAt = coupon.getEndAt();
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }

    public boolean isStackable() {
        return Boolean.TRUE.equals(this.stackable);
    }
}
