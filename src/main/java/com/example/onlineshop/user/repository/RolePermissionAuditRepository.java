package com.example.onlineshop.user.repository;

import com.example.onlineshop.user.entity.RolePermissionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolePermissionAuditRepository extends JpaRepository<RolePermissionAudit, Long> {
}
