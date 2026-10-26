package com.example.onlineshop.contact.repository;

import com.example.onlineshop.contact.entity.ContactSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ContactSubmissionRepository extends JpaRepository<ContactSubmission, Long> {
    long countByEmailIgnoreCaseAndSubmittedAtAfter(String email, LocalDateTime after);
}
