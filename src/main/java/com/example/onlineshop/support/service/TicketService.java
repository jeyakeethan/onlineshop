package com.example.onlineshop.support.service;

import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.support.dto.TicketDTO;
import com.example.onlineshop.support.dto.TicketCreateDTO;
import com.example.onlineshop.support.dto.TicketDetailDTO;
import com.example.onlineshop.support.dto.TicketMessageCreateDTO;
import com.example.onlineshop.support.dto.TicketMessageDTO;
import com.example.onlineshop.support.entity.Ticket;
import com.example.onlineshop.support.entity.TicketMessage;
import com.example.onlineshop.support.repository.TicketRepository;
import com.example.onlineshop.support.repository.TicketMessageRepository;
import com.example.onlineshop.notification.entity.Notification;
import com.example.onlineshop.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Locale;

@Service @RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final AuthenticationHelper authenticationHelper;
    private final TicketMessageRepository ticketMessageRepository;
    private final NotificationRepository notificationRepository;

    public Page<TicketDTO> getOwnTickets(int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }
        return ticketRepository.findByUserIdOrderByCreatedAtDesc(
                        authenticationHelper.getCurrentUserId(), PageRequest.of(page, size))
                .map(TicketDTO::new);
    }

    @Transactional
    public TicketDTO createTicket(TicketCreateDTO request) {
        if (request == null || request.getSubject() == null || request.getSubject().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Subject and description are required");
        }
        String priority = request.getPriority() == null || request.getPriority().isBlank()
                ? "NORMAL" : request.getPriority().trim().toUpperCase(Locale.ROOT);
        if (!java.util.Set.of("LOW", "NORMAL", "HIGH", "URGENT").contains(priority)) {
            throw new IllegalArgumentException("Priority must be LOW, NORMAL, HIGH or URGENT");
        }
        Ticket ticket = new Ticket();
        ticket.setUserId(authenticationHelper.getCurrentUserId());
        ticket.setSubject(request.getSubject().trim());
        ticket.setDescription(request.getDescription().trim());
        ticket.setPriority(priority);
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(ticket.getCreatedAt());
        return new TicketDTO(ticketRepository.save(ticket));
    }

    public TicketDetailDTO getOwnTicket(Long ticketId) {
        String userId = authenticationHelper.getCurrentUserId();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!userId.equalsIgnoreCase(ticket.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not own this ticket");
        }
        return new TicketDetailDTO(ticket,
                ticketMessageRepository.findByTicketIdOrderByCreatedAtAscMessageIdAsc(ticketId));
    }

    @Transactional
    public TicketMessageDTO addOwnTicketMessage(Long ticketId, TicketMessageCreateDTO request) {
        String userId = authenticationHelper.getCurrentUserId();
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!userId.equalsIgnoreCase(ticket.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not own this ticket");
        }
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }
        LocalDateTime now = LocalDateTime.now();
        TicketMessage message = new TicketMessage();
        message.setTicketId(ticketId);
        message.setAuthorUserId(userId);
        message.setAuthorType("CUSTOMER");
        message.setMessage(request.getMessage().trim());
        message.setCreatedAt(now);
        TicketMessage savedMessage = ticketMessageRepository.save(message);
        ticket.setStatus("OPEN");
        ticket.setUpdatedAt(now);
        ticketRepository.save(ticket);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType("TICKET_REPLY");
        notification.setTitle("Ticket updated");
        notification.setMessage("Your support ticket has a new reply.");
        notification.setCreatedAt(now);
        notificationRepository.save(notification);
        return new TicketMessageDTO(savedMessage);
    }
}
