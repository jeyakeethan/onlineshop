package com.example.onlineshop.contact.controller;

import com.example.onlineshop.contact.dto.ContactRequestDTO;
import com.example.onlineshop.contact.dto.ContactResponseDTO;
import com.example.onlineshop.contact.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/contact") @RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ContactResponseDTO submit(@Valid @RequestBody ContactRequestDTO request) {
        return contactService.submit(request);
    }
}
