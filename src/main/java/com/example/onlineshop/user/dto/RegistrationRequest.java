package com.example.onlineshop.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String email;
    private String firstName;
    private String lastName;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    private String dob;
}
