package com.example.onlineshop.content.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class AdminPageUpdateDTO {
    @Size(max = 160)
    private String slug;
    @Size(max = 200)
    private String title;
    @Size(max = 100000)
    private String content;
    private Boolean published;
}
