package com.example.onlineshop.notification.service;

import com.example.onlineshop.email.delivery.entity.EmailDelivery;
import com.example.onlineshop.email.delivery.repository.EmailDeliveryRepository;
import com.example.onlineshop.notification.entity.Notification;
import com.example.onlineshop.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {
    private static final int MAX_ATTEMPTS = 5;
    private final NotificationRepository notificationRepository;
    private final EmailDeliveryRepository emailDeliveryRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public Notification enqueueInApp(String userId, String type, String title, String message, String deduplicationKey) {
        if (userId == null || userId.isBlank() || deduplicationKey == null || deduplicationKey.isBlank()) {
            throw new IllegalArgumentException("User and notification deduplication key are required");
        }
        return notificationRepository.findByDeduplicationKey(deduplicationKey).orElseGet(() -> {
            Notification notification = new Notification();
            notification.setDeduplicationKey(deduplicationKey);
            notification.setUserId(userId);
            notification.setType(type);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        });
    }

    @Transactional
    public EmailDelivery enqueueEmail(String templateKey, String recipient, String subject, String body,
                                      String deduplicationKey) {
        if (recipient == null || recipient.isBlank() || deduplicationKey == null || deduplicationKey.isBlank()) {
            throw new IllegalArgumentException("Recipient and email deduplication key are required");
        }
        return emailDeliveryRepository.findByDeduplicationKey(deduplicationKey).orElseGet(() -> {
            EmailDelivery delivery = new EmailDelivery();
            delivery.setDeduplicationKey(deduplicationKey);
            delivery.setTemplateKey(templateKey);
            delivery.setRecipient(recipient);
            delivery.setSubject(subject);
            delivery.setBody(body);
            delivery.setStatus("PENDING");
            delivery.setAttempts(0);
            delivery.setCreatedAt(LocalDateTime.now());
            delivery.setNextAttemptAt(delivery.getCreatedAt());
            return emailDeliveryRepository.save(delivery);
        });
    }

    @Scheduled(fixedDelayString = "${app.notifications.fixed-delay-ms:15000}")
    public void deliverPendingEmails() {
        emailDeliveryRepository.findTop100ByStatusInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                        List.of("PENDING", "RETRYING"), LocalDateTime.now())
                .forEach(this::deliverEmail);
    }

    @Transactional
    public void deliverEmail(EmailDelivery delivery) {
        if (delivery == null || !List.of("PENDING", "RETRYING").contains(delivery.getStatus())) return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(delivery.getRecipient());
            message.setSubject(delivery.getSubject());
            message.setText(delivery.getBody());
            mailSender.send(message);
            delivery.setStatus("SENT");
            delivery.setLastError(null);
        } catch (Exception exception) {
            delivery.setAttempts(delivery.getAttempts() + 1);
            delivery.setStatus(delivery.getAttempts() >= MAX_ATTEMPTS ? "FAILED" : "RETRYING");
            delivery.setLastError(exception.getMessage() == null ? "Email delivery failed" :
                    exception.getMessage().substring(0, Math.min(500, exception.getMessage().length())));
            long delaySeconds = Math.min(3600, 1L << Math.min(delivery.getAttempts(), 12));
            delivery.setNextAttemptAt(LocalDateTime.now().plusSeconds(delaySeconds));
        }
        emailDeliveryRepository.save(delivery);
    }
}
