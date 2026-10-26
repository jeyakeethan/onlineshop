package com.example.onlineshop.contact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_submissions")
@Getter @Setter @NoArgsConstructor
public class ContactSubmission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;
    private String name;
    private String email;
    @Lob private String message;
    private String status;
    private LocalDateTime submittedAt;

    @PrePersist
    void initializeSubmission() {
        if (status == null || status.isBlank()) status = "NEW";
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }
}
