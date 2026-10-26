package com.example.onlineshop.config.dto;

import com.example.onlineshop.config.entity.StoreSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminSettingsDTO {
    private Long settingsId;
    private String storeName;
    private String logoUrl;
    private String contactEmail;
    private String contactPhone;
    private String currency;
    private String shippingPolicy;
    private String returnPolicy;
    private String privacyPolicy;
    private String termsPolicy;

    public AdminSettingsDTO(StoreSettings settings) {
        this(settings.getSettingsId(), settings.getStoreName(), settings.getLogoUrl(),
                settings.getContactEmail(), settings.getContactPhone(), settings.getCurrency(),
                settings.getShippingPolicy(), settings.getReturnPolicy(), settings.getPrivacyPolicy(),
                settings.getTermsPolicy());
    }
}
