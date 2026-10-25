package com.example.onlineshop.repository;

import com.example.onlineshop.model.TokenSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TokenSessionRepository extends JpaRepository<TokenSession, String> {
    boolean existsByTokenIdAndUserEmailAndRevokedAtIsNullAndExpiresAtAfter(
            String tokenId, String userEmail, Instant now);

    Optional<TokenSession> findByTokenIdAndUserEmailAndRevokedAtIsNull(String tokenId, String userEmail);

    List<TokenSession> findByUserEmailAndRevokedAtIsNull(String userEmail);
}