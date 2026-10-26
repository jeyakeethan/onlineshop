package com.example.onlineshop.email.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Table(name = "email_deliveries")
@Getter @Setter @NoArgsConstructor
public class EmailDelivery {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;
    @Column(unique = true, length = 255)
    private String deduplicationKey;
    private String templateKey;
    private String recipient;
    @Column(length = 255)
    private String subject;
    @Column(columnDefinition = "text")
    private String body;
    private String status;
    private int attempts;
    private String lastError;
    private LocalDateTime nextAttemptAt;
    private LocalDateTime createdAt;
}
