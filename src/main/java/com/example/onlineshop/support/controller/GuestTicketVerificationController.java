package com.example.onlineshop.support.controller;

import com.example.onlineshop.support.dto.GuestTicketVerificationRequest;
import com.example.onlineshop.support.dto.GuestTicketVerificationResponse;
import com.example.onlineshop.support.dto.GuestTicketVerificationVerifyRequest;
import com.example.onlineshop.support.dto.GuestTicketAccessDTO;
import com.example.onlineshop.support.service.GuestTicketVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/guest-tickets/verification") @RequiredArgsConstructor
public class GuestTicketVerificationController {
    private final GuestTicketVerificationService verificationService;

    @PostMapping("/request")
    public ResponseEntity<GuestTicketVerificationResponse> request(
            @Valid @RequestBody GuestTicketVerificationRequest request) {
        return ResponseEntity.accepted().body(verificationService.request(request));
    }

    @PostMapping("/verify")
    public GuestTicketAccessDTO verify(@Valid @RequestBody GuestTicketVerificationVerifyRequest request) {
        return verificationService.verify(request);
    }
}
