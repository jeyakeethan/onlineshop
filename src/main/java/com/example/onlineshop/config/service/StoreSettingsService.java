package com.example.onlineshop.config.service;

import com.example.onlineshop.config.dto.AdminSettingsDTO;
import com.example.onlineshop.config.entity.StoreSettings;
import com.example.onlineshop.config.entity.StoreSettingsAudit;
import com.example.onlineshop.config.dto.AdminSettingsUpdateDTO;
import com.example.onlineshop.config.repository.StoreSettingsRepository;
import com.example.onlineshop.config.repository.StoreSettingsAuditRepository;
import com.example.onlineshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StoreSettingsService {
    private static final long SETTINGS_ID = 1L;
    private final StoreSettingsRepository repository;
    private final StoreSettingsAuditRepository auditRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public AdminSettingsDTO getSettings() {
        StoreSettings settings = repository.findById(SETTINGS_ID).orElseGet(this::createDefaults);
        return new AdminSettingsDTO(settings);
    }

    @Transactional
    public AdminSettingsDTO updateSettings(AdminSettingsUpdateDTO request) {
        StoreSettings settings = repository.findById(SETTINGS_ID).orElseGet(this::createDefaults);
        String previous = snapshot(settings);
        if (request.getStoreName() != null) settings.setStoreName(request.getStoreName().trim());
        if (request.getLogoUrl() != null) settings.setLogoUrl(request.getLogoUrl().trim());
        if (request.getContactEmail() != null) settings.setContactEmail(request.getContactEmail().trim());
        if (request.getContactPhone() != null) settings.setContactPhone(request.getContactPhone().trim());
        if (request.getCurrency() != null) {
            String currency = request.getCurrency().trim().toUpperCase(Locale.ROOT);
            if (!currency.equals(settings.getCurrency()) && orderRepository.count() > 0) {
                throw new IllegalStateException("Currency cannot be changed after orders exist");
            }
            settings.setCurrency(currency);
        }
        if (request.getShippingPolicy() != null) settings.setShippingPolicy(request.getShippingPolicy());
        if (request.getReturnPolicy() != null) settings.setReturnPolicy(request.getReturnPolicy());
        if (request.getPrivacyPolicy() != null) settings.setPrivacyPolicy(request.getPrivacyPolicy());
        if (request.getTermsPolicy() != null) settings.setTermsPolicy(request.getTermsPolicy());

        StoreSettings saved = repository.save(settings);
        StoreSettingsAudit audit = new StoreSettingsAudit();
        audit.setSettingsId(saved.getSettingsId());
        audit.setPreviousValues(previous);
        audit.setUpdatedValues(snapshot(saved));
        audit.setChangedBy(currentActor());
        audit.setChangedAt(Instant.now());
        auditRepository.save(audit);
        return new AdminSettingsDTO(saved);
    }

    private String snapshot(StoreSettings settings) {
        return "storeName=" + settings.getStoreName()
                + ";logoUrl=" + settings.getLogoUrl()
                + ";contactEmail=" + settings.getContactEmail()
                + ";contactPhone=" + settings.getContactPhone()
                + ";currency=" + settings.getCurrency()
                + ";shippingPolicy=" + settings.getShippingPolicy()
                + ";returnPolicy=" + settings.getReturnPolicy()
                + ";privacyPolicy=" + settings.getPrivacyPolicy()
                + ";termsPolicy=" + settings.getTermsPolicy();
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || authentication.getName() == null ? "system" : authentication.getName();
    }

    private StoreSettings createDefaults() {
        StoreSettings settings = new StoreSettings();
        settings.setSettingsId(SETTINGS_ID);
        settings.setStoreName("Online Shop");
        settings.setCurrency("USD");
        return repository.save(settings);
    }
}
