package com.example.onlineshop.support.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class GuestTicketAccessDTO {
    private Long ticketId;
    private String accessToken;
    private LocalDateTime expiresAt;
}
