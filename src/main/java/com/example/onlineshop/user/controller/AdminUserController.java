package com.example.onlineshop.user.controller;

import jakarta.validation.Valid;
import com.example.onlineshop.user.dto.RoleDTO;
import com.example.onlineshop.user.dto.UserDTO;
import com.example.onlineshop.user.dto.UserRolesUpdateDTO;
import com.example.onlineshop.user.dto.RolePermissionsUpdateDTO;
import com.example.onlineshop.common.service.BuyerService;
import com.example.onlineshop.user.service.RoleService;
import com.example.onlineshop.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Tag(name = "Admin users", description = "Administrative user and role management")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;
    private final BuyerService buyerService;
    private final RoleService roleService;

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:create')")
    @PostMapping("/users")
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) { return ResponseEntity.ok(userService.registerUser(userDTO)); }

    @PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATOR') or hasAuthority('account:anonymize')")
    @PostMapping("/users/{userId}/anonymize")
    public ResponseEntity<String> anonymizeUser(@PathVariable String userId) { return ResponseEntity.ok(buyerService.anonymizeUser(userId)); }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:read')")
    @GetMapping("/users")
    public Page<UserDTO> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return userService.getAllUsers(page, size);
    }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:read')")
    @GetMapping("/users/{userId}")
    public UserDTO getUserById(@PathVariable String userId) { return userService.getUserById(userId); }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:update')")
    @PutMapping("/users/{userId}")
    public String updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO) { userService.updateUser(userId, userDTO); return "User updated successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('user:delete')")
    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable String userId) { userService.deleteUser(userId); return "User deleted successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:assign')")
    @PostMapping("/users/{userId}/roles/{roleId}")
    public String assignRole(@PathVariable String userId, @PathVariable Long roleId) { roleService.assignRoleToUser(userId, roleId); return "Role assigned to user successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:assign')")
    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public String removeRole(@PathVariable String userId, @PathVariable Long roleId) { roleService.removeRoleFromUser(userId, roleId); return "Role removed from user successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:assign')")
    @PatchMapping("/users/{userId}/roles")
    public UserDTO updateRoles(@PathVariable String userId, @Valid @RequestBody UserRolesUpdateDTO request) {
        return roleService.updateUserRoles(userId, request.getRoles());
    }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:create')")
    @PostMapping("/roles")
    public String createRole(@RequestBody RoleDTO roleDTO) { roleService.createRole(roleDTO); return "Role created successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:read')")
    @GetMapping("/roles")
    public List<RoleDTO> getAllRoles() { return roleService.getAllRoles(); }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:update')")
    @PatchMapping("/roles/{roleId}/permissions")
    public RoleDTO updateRolePermissions(@PathVariable Long roleId,
                                         @Valid @RequestBody RolePermissionsUpdateDTO request) {
        return roleService.updateRolePermissions(roleId, request.getPermissions());
    }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:read')")
    @GetMapping("/roles/{roleId}")
    public RoleDTO getRoleById(@PathVariable Long roleId) { return roleService.getRoleById(roleId); }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:update')")
    @PutMapping("/roles/{roleId}")
    public String updateRole(@PathVariable Long roleId, @RequestBody RoleDTO roleDTO) { roleService.updateRole(roleId, roleDTO); return "Role updated successfully."; }

    @PreAuthorize("hasAnyRole('ADMIN') or hasAuthority('role:delete')")
    @DeleteMapping("/roles/{roleId}")
    public String deleteRole(@PathVariable Long roleId) { roleService.deleteRole(roleId); return "Role deleted successfully."; }
}
