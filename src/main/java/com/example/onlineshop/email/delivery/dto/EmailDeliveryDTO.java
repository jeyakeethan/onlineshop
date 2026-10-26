package com.example.onlineshop.email.delivery.dto;

import com.example.onlineshop.email.delivery.entity.EmailDelivery;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class EmailDeliveryDTO {
    private Long deliveryId;
    private String templateKey;
    private String recipientMasked;
    private String status;
    private int attempts;
    private String lastError;
    private LocalDateTime nextAttemptAt;
    private LocalDateTime createdAt;

    public EmailDeliveryDTO(EmailDelivery delivery) {
        this.deliveryId = delivery.getDeliveryId();
        this.templateKey = delivery.getTemplateKey();
        this.recipientMasked = mask(delivery.getRecipient());
        this.status = delivery.getStatus();
        this.attempts = delivery.getAttempts();
        this.lastError = delivery.getLastError();
        this.nextAttemptAt = delivery.getNextAttemptAt();
        this.createdAt = delivery.getCreatedAt();
    }

    private static String mask(String recipient) {
        if (recipient == null || recipient.isBlank()) return null;
        int at = recipient.indexOf('@');
        if (at <= 0) return "***";
        String local = recipient.substring(0, at);
        String domain = recipient.substring(at + 1);
        return (local.length() <= 2 ? "*" : local.substring(0, 2)) + "***@" + domain;
    }
}
