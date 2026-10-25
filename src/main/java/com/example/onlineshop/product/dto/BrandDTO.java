package com.example.onlineshop.product.dto;

import com.example.onlineshop.product.entity.Brand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BrandDTO {
    private Long brandId;
    private String name;
    private boolean active;

    public BrandDTO(Brand brand) {
        this.brandId = brand.getBrandId();
        this.name = brand.getName();
        this.active = brand.isActive();
    }
}
