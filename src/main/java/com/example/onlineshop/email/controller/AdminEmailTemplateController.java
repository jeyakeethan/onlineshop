package com.example.onlineshop.email.controller;

import com.example.onlineshop.email.dto.EmailTemplateDTO;
import com.example.onlineshop.email.dto.EmailTemplateUpdateDTO;
import jakarta.validation.Valid;
import com.example.onlineshop.email.service.AdminEmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/admin/email-templates") @RequiredArgsConstructor
public class AdminEmailTemplateController {
    private final AdminEmailTemplateService adminEmailTemplateService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('email-template:read')")
    @GetMapping
    public Page<EmailTemplateDTO> getTemplates(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return adminEmailTemplateService.getTemplates(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('email-template:update')")
    @org.springframework.web.bind.annotation.PatchMapping("/{templateId}")
    public EmailTemplateDTO updateTemplate(@org.springframework.web.bind.annotation.PathVariable Long templateId,
                                           @Valid @org.springframework.web.bind.annotation.RequestBody EmailTemplateUpdateDTO request) {
        return adminEmailTemplateService.updateTemplate(templateId, request);
    }
}
