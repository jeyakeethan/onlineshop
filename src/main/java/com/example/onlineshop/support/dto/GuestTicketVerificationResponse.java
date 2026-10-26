package com.example.onlineshop.support.dto;

import lombok.Getter;

@Getter
public class GuestTicketVerificationResponse {
    private final String message = "If an eligible guest ticket exists, verification instructions have been sent.";
}
