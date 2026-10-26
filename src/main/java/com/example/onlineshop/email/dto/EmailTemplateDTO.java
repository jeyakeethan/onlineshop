package com.example.onlineshop.email.dto;

import com.example.onlineshop.email.entity.EmailTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class EmailTemplateDTO {
    private Long templateId;
    private String templateKey;
    private String subject;
    private String body;
    private String allowedVariables;
    private boolean active;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;

    public EmailTemplateDTO(EmailTemplate template) {
        this.templateId = template.getTemplateId();
        this.templateKey = template.getTemplateKey();
        this.subject = template.getSubject();
        this.body = template.getBody();
        this.allowedVariables = template.getAllowedVariables();
        this.active = template.isActive();
        this.lastUpdatedBy = template.getLastUpdatedBy();
        this.lastUpdatedAt = template.getLastUpdatedAt();
    }
}
