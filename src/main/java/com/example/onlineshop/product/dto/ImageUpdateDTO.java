package com.example.onlineshop.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageUpdateDTO {
    private String altText;
    private Integer displayOrder;
    private Boolean primaryImage;
}
