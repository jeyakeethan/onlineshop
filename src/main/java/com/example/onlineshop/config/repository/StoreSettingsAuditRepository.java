package com.example.onlineshop.config.repository;

import com.example.onlineshop.config.entity.StoreSettingsAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreSettingsAuditRepository extends JpaRepository<StoreSettingsAudit, Long> {
}
