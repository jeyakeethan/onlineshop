package com.example.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForgotPasswordResponse {
    private String message;
    private String status;
    private String resetToken;
    private String emailTemplateSubject;
    private String emailTemplateBody;
}
