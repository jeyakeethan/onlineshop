package com.example.onlineshop.user.service;

import com.example.onlineshop.user.dto.RoleDTO;
import com.example.onlineshop.user.entity.Permission;
import com.example.onlineshop.user.entity.Role;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.user.repository.PermissionRepository;
import com.example.onlineshop.user.repository.RoleRepository;
import com.example.onlineshop.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

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
