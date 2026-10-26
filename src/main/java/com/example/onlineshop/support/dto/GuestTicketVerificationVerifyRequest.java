package com.example.onlineshop.support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class GuestTicketVerificationVerifyRequest {
    @NotNull
    private Long ticketId;
    @NotBlank @Pattern(regexp = "\\d{6}")
    private String code;
}
