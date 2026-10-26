package com.example.onlineshop.email.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class EmailTemplateUpdateDTO {
    @Size(max = 200)
    private String subject;
    @Size(max = 100000)
    private String body;
    @Size(max = 2000)
    private String allowedVariables;
    private Boolean active;
}
