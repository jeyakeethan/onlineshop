package com.example.onlineshop.shipping.dto;

import com.example.onlineshop.shipping.entity.ShippingMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminShippingMethodDTO {
    private Long methodId;
    private String code;
    private String label;
    private BigDecimal fee;
    private BigDecimal freeShippingThreshold;
    private String estimate;
    private Boolean active;

    public AdminShippingMethodDTO() {
    }

    public AdminShippingMethodDTO(ShippingMethod method) {
        this.methodId = method.getMethodId();
        this.code = method.getCode();
        this.label = method.getLabel();
        this.fee = method.getFee();
        this.freeShippingThreshold = method.getFreeShippingThreshold();
        this.estimate = method.getEstimate();
        this.active = method.isActive();
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
