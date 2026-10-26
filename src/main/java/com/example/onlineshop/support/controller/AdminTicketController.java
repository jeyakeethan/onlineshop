package com.example.onlineshop.support.controller;

import com.example.onlineshop.support.dto.AdminTicketDTO;
import com.example.onlineshop.support.dto.AdminTicketUpdateDTO;
import com.example.onlineshop.support.dto.AdminTicketMessageCreateDTO;
import com.example.onlineshop.support.dto.TicketMessageDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import com.example.onlineshop.support.service.AdminTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/v1/admin/tickets") @RequiredArgsConstructor
public class AdminTicketController {
    private final AdminTicketService adminTicketService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('ticket:read')")
    @GetMapping
    public Page<AdminTicketDTO> getTickets(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(required = false) String assignedTo) {
        return adminTicketService.getTickets(page, size, status, assignedTo);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('ticket:update')")
    @org.springframework.web.bind.annotation.PatchMapping("/{ticketId}")
    public AdminTicketDTO updateTicket(@org.springframework.web.bind.annotation.PathVariable Long ticketId,
                                       @Valid @org.springframework.web.bind.annotation.RequestBody AdminTicketUpdateDTO request) {
        return adminTicketService.updateTicket(ticketId, request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('ticket:reply')")
    @org.springframework.web.bind.annotation.PostMapping("/{ticketId}/messages")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public TicketMessageDTO addStaffMessage(
            @org.springframework.web.bind.annotation.PathVariable Long ticketId,
            @Valid @org.springframework.web.bind.annotation.RequestBody AdminTicketMessageCreateDTO request) {
        return adminTicketService.addStaffMessage(ticketId, request);
    }
}
