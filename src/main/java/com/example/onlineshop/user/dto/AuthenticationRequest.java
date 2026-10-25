package com.example.onlineshop.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    @JsonAlias({"email", "username"})
    private String userId;
    private String password;
}
