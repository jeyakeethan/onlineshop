package com.example.onlineshop.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "store_settings")
@Getter
@Setter
public class StoreSettings {
    @Id
    private Long settingsId;

    @Column(nullable = false)
    private String storeName;

    private String logoUrl;
    private String contactEmail;
    private String contactPhone;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(columnDefinition = "text")
    private String shippingPolicy;

    @Column(columnDefinition = "text")
    private String returnPolicy;

    @Column(columnDefinition = "text")
    private String privacyPolicy;

    @Column(columnDefinition = "text")
    private String termsPolicy;
}
