package com.example.onlineshop.support.service;

import com.example.onlineshop.support.dto.GuestTicketVerificationRequest;
import com.example.onlineshop.support.dto.GuestTicketVerificationResponse;
import com.example.onlineshop.support.dto.GuestTicketVerificationVerifyRequest;
import com.example.onlineshop.support.dto.GuestTicketAccessDTO;
import com.example.onlineshop.support.entity.Ticket;
import com.example.onlineshop.support.repository.TicketRepository;
import com.example.onlineshop.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GuestTicketVerificationService {
    private static final long CHALLENGE_TTL_MILLIS = 10 * 60 * 1000L;
    private static final long ACCESS_TTL_MILLIS = 15 * 60 * 1000L;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<Long, GuestTicketChallenge> challenges = new ConcurrentHashMap<>();
    private final Map<String, GuestTicketAccess> accessTokens = new ConcurrentHashMap<>();

    public GuestTicketVerificationResponse request(GuestTicketVerificationRequest request) {
        if (request == null || request.getTicketId() == null || request.getEmail() == null
                || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Ticket ID and email are required");
        }
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        Ticket ticket = ticketRepository.findById(request.getTicketId()).orElse(null);
        if (ticket != null && ticket.getGuestEmail() != null
                && email.equalsIgnoreCase(ticket.getGuestEmail())) {
            String code = String.format("%06d", secureRandom.nextInt(1_000_000));
            challenges.put(ticket.getTicketId(), new GuestTicketChallenge(code, System.currentTimeMillis() + CHALLENGE_TTL_MILLIS));
            emailService.sendGuestTicketVerificationEmail(email, code);
        }
        return new GuestTicketVerificationResponse();
    }

    public String consumeChallenge(Long ticketId, String code) {
        GuestTicketChallenge challenge = challenges.get(ticketId);
        if (challenge == null || code == null || !challenge.code().equals(code)
                || challenge.expiresAtMillis() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("Invalid or expired verification code");
        }
        challenges.remove(ticketId, challenge);
        return challenge.code();
    }

    public GuestTicketAccessDTO verify(GuestTicketVerificationVerifyRequest request) {
        if (request == null || request.getTicketId() == null || request.getCode() == null) {
            throw new IllegalArgumentException("Ticket ID and verification code are required");
        }
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification code"));
        consumeChallenge(ticket.getTicketId(), request.getCode());
        String accessToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
        accessTokens.put(accessToken, new GuestTicketAccess(ticket.getTicketId(), expiresAt));
        return new GuestTicketAccessDTO(ticket.getTicketId(), accessToken, expiresAt);
    }

    public boolean hasAccess(Long ticketId, String accessToken) {
        GuestTicketAccess access = accessTokens.get(accessToken);
        if (access == null || !access.ticketId().equals(ticketId) || access.expiresAt().isBefore(LocalDateTime.now())) {
            if (access != null && access.expiresAt().isBefore(LocalDateTime.now())) accessTokens.remove(accessToken);
            return false;
        }
        return true;
    }

    private record GuestTicketChallenge(String code, long expiresAtMillis) { }
    private record GuestTicketAccess(Long ticketId, LocalDateTime expiresAt) { }
}
