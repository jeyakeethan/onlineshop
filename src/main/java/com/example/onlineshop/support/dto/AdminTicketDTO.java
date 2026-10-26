package com.example.onlineshop.support.dto;

import com.example.onlineshop.support.entity.Ticket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class AdminTicketDTO {
    private Long ticketId;
    private String userId;
    private String guestEmail;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;

    public AdminTicketDTO(Ticket ticket) {
        this.ticketId = ticket.getTicketId();
        this.userId = ticket.getUserId();
        this.guestEmail = ticket.getGuestEmail();
        this.subject = ticket.getSubject();
        this.description = ticket.getDescription();
        this.priority = ticket.getPriority();
        this.status = ticket.getStatus();
        this.assignedTo = ticket.getAssignedTo();
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.lastUpdatedBy = ticket.getLastUpdatedBy();
        this.lastUpdatedAt = ticket.getLastUpdatedAt();
    }
}
