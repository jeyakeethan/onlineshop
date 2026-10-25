package com.example.onlineshop.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipping_zones")
@Getter
@Setter
public class ShippingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zoneId;

    @Column(nullable = false, length = 100)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shipping_zone_regions", joinColumns = @JoinColumn(name = "zone_id"))
    @Column(name = "region_code", nullable = false, length = 16)
    @OrderColumn(name = "display_order")
    private List<String> regionCodes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shipping_zone_postal_prefixes", joinColumns = @JoinColumn(name = "zone_id"))
    @Column(name = "postal_prefix", nullable = false, length = 16)
    @OrderColumn(name = "display_order")
    private List<String> postalCodePrefixes = new ArrayList<>();

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    private boolean active = true;
}
