package com.example.onlineshop.product.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewHideDTO {
    @Size(max = 500)
    private String reason;
}
