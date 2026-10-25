package com.example.onlineshop.shipping.dto;

import com.example.onlineshop.shipping.entity.ShippingZone;
import lombok.Data;

import java.util.List;

@Data
public class ShippingZoneDTO {
    private Long zoneId;
    private String name;
    private List<String> regionCodes;
    private List<String> postalCodePrefixes;
    private Integer priority;
    private Boolean active;

    public ShippingZoneDTO() {
    }

    public ShippingZoneDTO(ShippingZone zone) {
        this.zoneId = zone.getZoneId();
        this.name = zone.getName();
        this.regionCodes = List.copyOf(zone.getRegionCodes());
        this.postalCodePrefixes = List.copyOf(zone.getPostalCodePrefixes());
        this.priority = zone.getPriority();
        this.active = zone.isActive();
    }

    public int getPriority() {
        return priority == null ? 0 : priority;
    }

    public Integer getPriorityOrNull() {
        return priority;
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}
