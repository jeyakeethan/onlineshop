package com.example.onlineshop.config.controller;

import com.example.onlineshop.config.dto.AdminSettingsDTO;
import com.example.onlineshop.config.dto.AdminSettingsUpdateDTO;
import jakarta.validation.Valid;
import com.example.onlineshop.config.service.StoreSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {
    private final StoreSettingsService settingsService;

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATOR') or hasAuthority('settings:read')")
    @GetMapping
    public AdminSettingsDTO getSettings() {
        return settingsService.getSettings();
    }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATOR') or hasAuthority('settings:update')")
    @org.springframework.web.bind.annotation.PatchMapping
    public AdminSettingsDTO updateSettings(@Valid @org.springframework.web.bind.annotation.RequestBody AdminSettingsUpdateDTO request) {
        return settingsService.updateSettings(request);
    }
}
