package com.example.onlineshop.support.repository;

import com.example.onlineshop.support.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
