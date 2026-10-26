package com.example.onlineshop.email.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Table(name = "email_templates")
@Getter @Setter @NoArgsConstructor
public class EmailTemplate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;
    private String templateKey;
    private String subject;
    @Lob private String body;
    @Lob private String allowedVariables;
    private boolean active;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
}
