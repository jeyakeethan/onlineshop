package com.example.onlineshop.support.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AdminTicketUpdateDTO {
    @Size(max = 20)
    private String priority;
    @Size(max = 32)
    private String status;
    @Size(max = 255)
    private String assignedTo;
}
