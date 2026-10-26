package com.example.onlineshop.product.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewUpdateDTO {
    private String reviewText;

    @Min(1)
    @Max(5)
    private Integer rating;
}
