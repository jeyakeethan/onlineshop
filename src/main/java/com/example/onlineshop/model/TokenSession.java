package com.example.onlineshop.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "token_sessions")
@Getter
@Setter
public class TokenSession {

    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String tokenId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;
}