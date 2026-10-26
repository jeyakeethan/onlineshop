package com.example.onlineshop.support.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class GuestTicketVerificationRequest {
    @NotNull
    private Long ticketId;
    @NotBlank @Email @Size(max = 255)
    private String email;
}
