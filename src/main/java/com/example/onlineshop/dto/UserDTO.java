package com.example.onlineshop.dto;

import com.example.onlineshop.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String dob;
    private List<String> roles;
    private LocalDate lastLoginTime;
    private boolean emailVerified;

    public UserDTO(User user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.dob = user.getDob();
        this.lastLoginTime = LocalDate.now();

        // Convert List<Role> to List<String> (role names only)
        this.roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());
        this.lastLoginTime = user.getLastLoginTime();
    }

}
