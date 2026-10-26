package com.example.onlineshop.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "role_permission_audits")
@Getter
@Setter
public class RolePermissionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @Column(nullable = false)
    private Long roleId;

    @Column(nullable = false)
    private String roleName;

    @Lob
    @Column(nullable = false)
    private String previousPermissions;

    @Lob
    @Column(nullable = false)
    private String updatedPermissions;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private Instant changedAt;
}
