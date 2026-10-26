package com.example.onlineshop.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class UserRolesUpdateDTO {
    @NotEmpty
    private List<String> roles;
}
