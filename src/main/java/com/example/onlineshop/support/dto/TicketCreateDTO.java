package com.example.onlineshop.support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TicketCreateDTO {
    @NotBlank @Size(max = 160)
    private String subject;

    @NotBlank @Size(min = 10, max = 5000)
    private String description;

    @Size(max = 20)
    private String priority;
}
