package com.example.onlineshop.email.delivery.controller;

import com.example.onlineshop.email.delivery.dto.EmailDeliveryDTO;
import com.example.onlineshop.email.delivery.service.AdminEmailDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/admin/notification-deliveries") @RequiredArgsConstructor
public class AdminEmailDeliveryController {
    private final AdminEmailDeliveryService adminEmailDeliveryService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('notification-delivery:read')")
    @GetMapping
    public Page<EmailDeliveryDTO> getDeliveries(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(required = false) String status) {
        return adminEmailDeliveryService.getFailedDeliveries(page, size, status);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('notification-delivery:retry')")
    @org.springframework.web.bind.annotation.PostMapping("/{deliveryId}/retry")
    public EmailDeliveryDTO retry(@org.springframework.web.bind.annotation.PathVariable Long deliveryId) {
        return adminEmailDeliveryService.retry(deliveryId);
    }
}
