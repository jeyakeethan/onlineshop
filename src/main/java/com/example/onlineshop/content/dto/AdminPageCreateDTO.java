package com.example.onlineshop.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AdminPageCreateDTO {
    @Size(max = 160)
    private String slug;
    @NotBlank @Size(max = 200)
    private String title;
    @NotBlank @Size(max = 100000)
    private String content;
    private boolean published;
}
