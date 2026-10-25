package com.example.onlineshop.dto;

import com.example.onlineshop.model.TaxRule;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TaxRuleDTO {
    private Long ruleId;
    private String region;
    private BigDecimal rate;
    private Boolean taxInclusive;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
    private Boolean active;

    public TaxRuleDTO() {
    }

    public TaxRuleDTO(TaxRule rule) {
        this.ruleId = rule.getRuleId();
        this.region = rule.getRegion();
        this.rate = rule.getRate();
        this.taxInclusive = rule.isTaxInclusive();
        this.effectiveFrom = rule.getEffectiveFrom();
        this.effectiveTo = rule.getEffectiveTo();
        this.active = rule.isActive();
    }

    public boolean isTaxInclusive() {
        return Boolean.TRUE.equals(taxInclusive);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}