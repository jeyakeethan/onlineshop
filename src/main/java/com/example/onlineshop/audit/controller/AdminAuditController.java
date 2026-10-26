package com.example.onlineshop.audit.controller;

import com.example.onlineshop.audit.dto.AdminAuditDTO;
import com.example.onlineshop.audit.service.AdminAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
public class AdminAuditController {
    private final AdminAuditService auditService;

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATOR') or hasAuthority('audit:read')")
    @GetMapping
    public Page<AdminAuditDTO> getAudit(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return auditService.getAudit(page, size);
    }
}
