package com.example.onlineshop.order.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GuestOrderAccessDTO {
    private final Long orderId;
    private final String accessToken;
    private final LocalDateTime expiresAt;

    public GuestOrderAccessDTO(Long orderId, String accessToken, LocalDateTime expiresAt) {
        this.orderId = orderId;
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }
}
