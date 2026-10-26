package com.example.onlineshop.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RolePermissionsUpdateDTO {
    @NotNull
    private List<String> permissions;
}
