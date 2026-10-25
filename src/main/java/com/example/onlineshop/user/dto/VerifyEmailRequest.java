package com.example.onlineshop.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailRequest {
    private String email;
    private String token;
}
