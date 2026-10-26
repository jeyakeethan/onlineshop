package com.example.onlineshop.support.controller;

import com.example.onlineshop.support.dto.TicketDTO;
import com.example.onlineshop.support.dto.TicketCreateDTO;
import com.example.onlineshop.support.dto.TicketDetailDTO;
import com.example.onlineshop.support.dto.TicketMessageCreateDTO;
import com.example.onlineshop.support.dto.TicketMessageDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.onlineshop.support.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/tickets") @RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public Page<TicketDTO> getOwnTickets(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return ticketService.getOwnTickets(page, size);
    }

    @PreAuthorize("isAuthenticated()")
    @org.springframework.web.bind.annotation.PostMapping
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public TicketDTO createTicket(@Valid @org.springframework.web.bind.annotation.RequestBody TicketCreateDTO request) {
        return ticketService.createTicket(request);
    }

    @PreAuthorize("isAuthenticated()")
    @org.springframework.web.bind.annotation.GetMapping("/{ticketId}")
    public TicketDetailDTO getOwnTicket(@org.springframework.web.bind.annotation.PathVariable Long ticketId) {
        return ticketService.getOwnTicket(ticketId);
    }

    @PreAuthorize("isAuthenticated()")
    @org.springframework.web.bind.annotation.PostMapping("/{ticketId}/messages")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public TicketMessageDTO addMessage(
            @org.springframework.web.bind.annotation.PathVariable Long ticketId,
            @Valid @org.springframework.web.bind.annotation.RequestBody TicketMessageCreateDTO request) {
        return ticketService.addOwnTicketMessage(ticketId, request);
    }
}
