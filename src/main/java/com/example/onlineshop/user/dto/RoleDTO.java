package com.example.onlineshop.user.dto;

import com.example.onlineshop.user.entity.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RoleDTO {
    private Long id;
    private String name;
    private List<String> permissions = new ArrayList<>();

    public RoleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public RoleDTO(Role role) {
        this.id = role.getRoleId();
        this.name = role.getName();
        if (role.getPermissions() != null) {
            this.permissions = role.getPermissions().stream()
                    .map(p -> p.getName())
                    .collect(Collectors.toList());
        }
    }
}
