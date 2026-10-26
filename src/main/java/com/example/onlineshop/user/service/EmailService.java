package com.example.onlineshop.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import com.example.onlineshop.notification.service.NotificationDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final NotificationDeliveryService notificationDeliveryService;

    public void sendOrderConfirmation(String to, Long orderId) {
        notificationDeliveryService.enqueueEmail("ORDER_CONFIRMATION", to, "Order Confirmation",
                "Your order with ID " + orderId + " has been placed successfully.", "order-confirmation:" + orderId + ":" + to);
    }

    public void sendVerificationEmail(String to, String token) {
        notificationDeliveryService.enqueueEmail("EMAIL_VERIFICATION", to, "Verify your email",
                "Use the following token to verify your email: " + token + "\n\n" +
                        "If you are using the web app, open the verification page and paste this token.",
                "email-verification:" + to + ":" + token);
    }

    public void sendPasswordResetEmail(String to, String token) {
        notificationDeliveryService.enqueueEmail("PASSWORD_RESET", to, "Password reset request",
                "Use the following token to reset your password: " + token + "\n\n" +
                        "If you are using the web app, submit this token in the reset form.",
                "password-reset:" + to + ":" + token);
    }

    public void sendGuestOrderLookupEmail(String to, String challenge) {
        notificationDeliveryService.enqueueEmail("GUEST_ORDER_LOOKUP", to, "Guest order verification",
                "Use this one-time guest order verification code: " + challenge
                        + "\n\nIt expires shortly and can only be used once.",
                "guest-order-lookup:" + to + ":" + challenge);
    }

    public void sendGuestTicketVerificationEmail(String to, String challenge) {
        notificationDeliveryService.enqueueEmail("GUEST_TICKET_VERIFICATION", to, "Guest ticket verification",
                "Use this one-time guest ticket verification code: " + challenge
                        + "\n\nIt expires shortly and can only be used once.",
                "guest-ticket-verification:" + to + ":" + challenge);
    }
}
