package com.example.onlineshop.content.dto;

import com.example.onlineshop.content.entity.FeaturedCollection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FeaturedCollectionDTO {
    private Long collectionId;
    private String name;
    private String slug;
    private boolean active;
    private int displayOrder;
    private List<Long> productIds = List.of();

    public FeaturedCollectionDTO(FeaturedCollection collection) {
        this.collectionId = collection.getCollectionId();
        this.name = collection.getName();
        this.slug = collection.getSlug();
        this.active = collection.isActive();
        this.displayOrder = collection.getDisplayOrder();
        this.productIds = collection.getProducts().stream().map(product -> product.getProductId()).toList();
    }
}
