package com.example.onlineshop.content.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeaturedCollectionUpdateDTO {
    private Boolean active;
    private Integer displayOrder;
    private List<Long> productIds;
}
