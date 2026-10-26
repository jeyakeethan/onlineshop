package com.example.onlineshop.contact.service;

import com.example.onlineshop.contact.dto.ContactRequestDTO;
import com.example.onlineshop.contact.dto.ContactResponseDTO;
import com.example.onlineshop.contact.entity.ContactSubmission;
import com.example.onlineshop.contact.repository.ContactSubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service @RequiredArgsConstructor
public class ContactService {
    private final ContactSubmissionRepository contactSubmissionRepository;

    @Transactional
    public ContactResponseDTO submit(ContactRequestDTO request) {
        if (request == null || request.getWebsite() != null && !request.getWebsite().isBlank()) {
            throw new IllegalArgumentException("Invalid contact submission");
        }
        String email = request.getEmail().trim().toLowerCase();
        if (contactSubmissionRepository.countByEmailIgnoreCaseAndSubmittedAtAfter(
                email, LocalDateTime.now().minusHours(1)) >= 3) {
            throw new IllegalStateException("Too many contact submissions; try again later");
        }
        ContactSubmission submission = new ContactSubmission();
        submission.setName(request.getName().trim());
        submission.setEmail(email);
        submission.setMessage(request.getMessage().trim());
        submission.setStatus("NEW");
        ContactSubmission saved = contactSubmissionRepository.save(submission);
        return new ContactResponseDTO(saved.getContactId(), saved.getStatus());
    }
}
