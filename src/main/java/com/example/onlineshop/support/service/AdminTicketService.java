package com.example.onlineshop.support.service;

import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.support.dto.AdminTicketDTO;
import com.example.onlineshop.support.dto.AdminTicketUpdateDTO;
import com.example.onlineshop.support.dto.AdminTicketMessageCreateDTO;
import com.example.onlineshop.support.dto.TicketMessageDTO;
import com.example.onlineshop.support.entity.Ticket;
import com.example.onlineshop.support.entity.TicketMessage;
import com.example.onlineshop.support.repository.TicketRepository;
import com.example.onlineshop.support.repository.TicketMessageRepository;
import com.example.onlineshop.notification.entity.Notification;
import com.example.onlineshop.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service @RequiredArgsConstructor
public class AdminTicketService {
    private final TicketRepository ticketRepository;
    private final AuthenticationHelper authenticationHelper;
    private final TicketMessageRepository ticketMessageRepository;
    private final NotificationRepository notificationRepository;

    public Page<AdminTicketDTO> getTickets(int page, int size, String status, String assignedTo) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }
        String normalizedStatus = status == null ? "" : status.trim();
        String normalizedAssignee = assignedTo == null ? "" : assignedTo.trim().toLowerCase(Locale.ROOT);
        List<Ticket> filtered = ticketRepository.findAll().stream()
                .filter(ticket -> normalizedStatus.isBlank() || normalizedStatus.equalsIgnoreCase(ticket.getStatus()))
                .filter(ticket -> normalizedAssignee.isBlank()
                        || ticket.getAssignedTo() != null
                        && normalizedAssignee.equals(ticket.getAssignedTo().toLowerCase(Locale.ROOT)))
                .sorted(java.util.Comparator.comparing(Ticket::getCreatedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder()))
                        .thenComparing(Ticket::getTicketId,
                                java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .toList();
        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());
        return new PageImpl<>(filtered.subList(start, end), PageRequest.of(page, size), filtered.size())
                .map(AdminTicketDTO::new);
    }

    @Transactional
    public AdminTicketDTO updateTicket(Long ticketId, AdminTicketUpdateDTO request) {
        if (request == null || request.getPriority() == null && request.getStatus() == null
                && request.getAssignedTo() == null) {
            throw new IllegalArgumentException("At least one ticket field must be provided");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (request.getPriority() != null) {
            String priority = request.getPriority().trim().toUpperCase(Locale.ROOT);
            if (!java.util.Set.of("LOW", "NORMAL", "HIGH", "URGENT").contains(priority)) {
                throw new IllegalArgumentException("Invalid ticket priority");
            }
            ticket.setPriority(priority);
        }
        if (request.getStatus() != null) {
            String status = request.getStatus().trim().toUpperCase(Locale.ROOT);
            if (!java.util.Set.of("OPEN", "IN_PROGRESS", "WAITING_FOR_CUSTOMER", "RESOLVED", "CLOSED").contains(status)) {
                throw new IllegalArgumentException("Invalid ticket status");
            }
            ticket.setStatus(status);
        }
        if (request.getAssignedTo() != null) {
            String assignee = request.getAssignedTo().trim();
            ticket.setAssignedTo(assignee.isBlank() ? null : assignee);
        }
        LocalDateTime now = LocalDateTime.now();
        ticket.setUpdatedAt(now);
        ticket.setLastUpdatedAt(now);
        ticket.setLastUpdatedBy(authenticationHelper.getCurrentUserId());
        return new AdminTicketDTO(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketMessageDTO addStaffMessage(Long ticketId, AdminTicketMessageCreateDTO request) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        LocalDateTime now = LocalDateTime.now();
        TicketMessage message = new TicketMessage();
        message.setTicketId(ticketId);
        message.setAuthorUserId(authenticationHelper.getCurrentUserId());
        message.setAuthorType(request.isInternalNote() ? "INTERNAL_NOTE" : "STAFF");
        message.setInternalNote(request.isInternalNote());
        message.setMessage(request.getMessage().trim());
        message.setCreatedAt(now);
        TicketMessage saved = ticketMessageRepository.save(message);
        if (!request.isInternalNote()) {
            ticket.setStatus("WAITING_FOR_CUSTOMER");
            if (ticket.getUserId() != null && !ticket.getUserId().isBlank()) {
                Notification notification = new Notification();
                notification.setUserId(ticket.getUserId());
                notification.setType("TICKET_REPLY");
                notification.setTitle("Support ticket updated");
                notification.setMessage("Your support ticket has a new staff reply.");
                notification.setCreatedAt(now);
                notificationRepository.save(notification);
            }
        }
        ticket.setUpdatedAt(now);
        ticket.setLastUpdatedAt(now);
        ticket.setLastUpdatedBy(authenticationHelper.getCurrentUserId());
        ticketRepository.save(ticket);
        return new TicketMessageDTO(saved);
    }
}
