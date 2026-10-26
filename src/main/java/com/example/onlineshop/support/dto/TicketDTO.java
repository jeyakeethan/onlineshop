package com.example.onlineshop.support.dto;

import com.example.onlineshop.support.entity.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class TicketDTO {
    private Long ticketId;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TicketDTO(Ticket ticket) {
        this.ticketId = ticket.getTicketId();
        this.subject = ticket.getSubject();
        this.description = ticket.getDescription();
        this.priority = ticket.getPriority();
        this.status = ticket.getStatus();
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
    }
}
