package com.example.onlineshop.config.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminSettingsUpdateDTO {
    @Size(max = 255)
    private String storeName;

    @Size(max = 255)
    private String logoUrl;

    @Email
    @Size(max = 255)
    private String contactEmail;

    @Size(max = 255)
    private String contactPhone;

    @Pattern(regexp = "[A-Za-z]{3}")
    private String currency;

    private String shippingPolicy;
    private String returnPolicy;
    private String privacyPolicy;
    private String termsPolicy;
}
