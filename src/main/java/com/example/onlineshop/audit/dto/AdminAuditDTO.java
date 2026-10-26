package com.example.onlineshop.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class AdminAuditDTO {
    private String auditId;
    private String action;
    private String resource;
    private String actor;
    private Instant occurredAt;
    private String summary;
}
