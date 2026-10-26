package com.example.onlineshop.email.repository;

import com.example.onlineshop.email.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> { }
