package com.example.onlineshop.support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class TicketMessageCreateDTO {
    @NotBlank @Size(min = 1, max = 5000)
    private String message;
}
