package com.example.onlineshop.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmation(String to, Long orderId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Order Confirmation");
        message.setText("Your order with ID " + orderId + " has been placed successfully.");
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your email");
        message.setText("Use the following token to verify your email: " + token + "\n\n" +
                "If you are using the web app, open the verification page and paste this token.");
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password reset request");
        message.setText("Use the following token to reset your password: " + token + "\n\n" +
                "If you are using the web app, submit this token in the reset form.");
        mailSender.send(message);
    }

    public void sendGuestOrderLookupEmail(String to, String challenge) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Guest order verification");
        message.setText("Use this one-time guest order verification code: " + challenge
                + "\n\nIt expires shortly and can only be used once.");
        mailSender.send(message);
    }
}
