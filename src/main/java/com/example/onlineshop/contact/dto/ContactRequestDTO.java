package com.example.onlineshop.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class ContactRequestDTO {
    @NotBlank @Size(max = 120)
    private String name;
    @NotBlank @Email @Size(max = 255)
    private String email;
    @NotBlank @Size(min = 10, max = 5000)
    private String message;
    @Size(max = 200)
    private String website;
}
