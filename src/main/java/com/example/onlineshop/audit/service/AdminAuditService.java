package com.example.onlineshop.audit.service;

import com.example.onlineshop.audit.dto.AdminAuditDTO;
import com.example.onlineshop.config.entity.StoreSettingsAudit;
import com.example.onlineshop.config.repository.StoreSettingsAuditRepository;
import com.example.onlineshop.user.entity.RolePermissionAudit;
import com.example.onlineshop.user.repository.RolePermissionAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAuditService {
    private static final int MAX_PAGE_SIZE = 100;

    private final RolePermissionAuditRepository roleAuditRepository;
    private final StoreSettingsAuditRepository settingsAuditRepository;

    public Page<AdminAuditDTO> getAudit(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page must be nonnegative and size must be between 1 and 100");
        }
        List<AdminAuditDTO> entries = new ArrayList<>();
        for (RolePermissionAudit audit : roleAuditRepository.findAll()) {
            entries.add(new AdminAuditDTO(
                    "role-permission:" + audit.getAuditId(), "UPDATE_PERMISSIONS",
                    "role:" + audit.getRoleId(), audit.getChangedBy(), audit.getChangedAt(),
                    "Role permissions changed for " + audit.getRoleName()));
        }
        for (StoreSettingsAudit audit : settingsAuditRepository.findAll()) {
            entries.add(new AdminAuditDTO(
                    "settings:" + audit.getAuditId(), "UPDATE_SETTINGS", "store-settings",
                    audit.getChangedBy(), audit.getChangedAt(), "Store settings updated"));
        }
        entries.sort(Comparator.comparing(AdminAuditDTO::getOccurredAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        int start = Math.min(page * size, entries.size());
        int end = Math.min(start + size, entries.size());
        return new PageImpl<>(entries.subList(start, end), PageRequest.of(page, size), entries.size());
    }
}
