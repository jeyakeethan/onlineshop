package com.example.onlineshop.user.service;

import com.example.onlineshop.user.dto.UserDTO;
import com.example.onlineshop.user.dto.RoleDTO;
import com.example.onlineshop.user.entity.Permission;
import com.example.onlineshop.user.entity.Role;
import com.example.onlineshop.user.entity.RolePermissionAudit;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.user.repository.PermissionRepository;
import com.example.onlineshop.user.repository.RolePermissionAuditRepository;
import com.example.onlineshop.user.repository.RoleRepository;
import com.example.onlineshop.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Locale;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionAuditRepository rolePermissionAuditRepository;

    public void createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setPermissions(resolvePermissions(roleDTO.getPermissions()));
        roleRepository.save(role);
    }

    public void updateRole(Long roleId, RoleDTO roleDTO) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(roleDTO.getName());
        role.setPermissions(resolvePermissions(roleDTO.getPermissions()));
        roleRepository.save(role);
    }

    // Delete role by ID
    public void deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
    }

    // Get role by ID
    public RoleDTO getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return new RoleDTO(role);
    }

    // Get all roles
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleDTO updateRolePermissions(Long roleId, List<String> permissionNames) {
        if (permissionNames == null) {
            throw new IllegalArgumentException("Permissions are required");
        }
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        List<String> normalizedNames = permissionNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        List<Permission> permissions = normalizedNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name)))
                .collect(Collectors.toList());

        List<String> previousNames = role.getPermissions() == null ? List.of() : role.getPermissions().stream()
                .map(Permission::getName)
                .filter(name -> name != null)
                .sorted()
                .collect(Collectors.toList());
        role.setPermissions(new ArrayList<>(permissions));
        Role saved = roleRepository.save(role);

        RolePermissionAudit audit = new RolePermissionAudit();
        audit.setRoleId(saved.getRoleId());
        audit.setRoleName(saved.getName());
        audit.setPreviousPermissions(String.join(",", previousNames));
        audit.setUpdatedPermissions(String.join(",", normalizedNames));
        audit.setChangedBy(currentActor());
        audit.setChangedAt(java.time.Instant.now());
        rolePermissionAuditRepository.save(audit);
        return new RoleDTO(saved);
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || authentication.getName() == null
                ? "system" : authentication.getName();
    }

    // Assign role to user
    public void assignRoleToUser(String userEmail, Long roleId) {
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    // Remove role from user
    public void removeRoleFromUser(String userEmail, Long roleId) {
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().remove(role);
        userRepository.save(user);
    }

    @Transactional
    public UserDTO updateUserRoles(String userEmail, List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("At least one role is required");
        }
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<String> normalizedNames = roleNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());
        if (normalizedNames.isEmpty()) throw new IllegalArgumentException("At least one valid role is required");
        List<Role> roles = normalizedNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name)))
                .collect(Collectors.toList());
        boolean currentlyAdmin = user.getRoles().stream().anyMatch(this::isAdminRole);
        boolean remainsAdmin = roles.stream().anyMatch(this::isAdminRole);
        if (currentlyAdmin && !remainsAdmin) {
            long otherActiveAdmins = userRepository.findAll().stream()
                    .filter(candidate -> candidate.isActive() && !candidate.isDeleted())
                    .filter(candidate -> !userEmail.equalsIgnoreCase(candidate.getEmail()))
                    .filter(candidate -> candidate.getRoles().stream().anyMatch(this::isAdminRole))
                    .count();
            if (otherActiveAdmins == 0) {
                throw new IllegalStateException("Cannot remove the last active administrator");
            }
        }
        user.setRoles(roles);
        return new UserDTO(userRepository.save(user));
    }

    private boolean isAdminRole(Role role) {
        return role != null && role.getName() != null
                && Set.of("ADMIN", "ADMINISTRATOR").contains(role.getName().trim().toUpperCase(Locale.ROOT));
    }

    private List<Permission> resolvePermissions(List<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return List.of();
        }
        return permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseGet(() -> {
                            Permission permission = new Permission();
                            permission.setName(name);
                            return permissionRepository.save(permission);
                        }))
                .collect(Collectors.toList());
    }
}
