package com.example.onlineshop.dto;

import com.example.onlineshop.model.Image;
import com.example.onlineshop.model.Product;
import com.example.onlineshop.model.SKU;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

    private Long productId;
    private String name;
    private String description;
    private String slug;
    private CategoryDTO categoryDTO;  // CategoryDTO for mapping the subcategory
    private String brandName;
    private List<SKUDTO> skuDTOs;    // List of SKUs for the product
    @JsonProperty("isAvailable")
    @JsonAlias("available")
    private boolean isAvailable;
    private boolean approved;

    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.slug = product.getSlug();
        this.categoryDTO = product.getCategory() != null ? new CategoryDTO(product.getCategory()) : null;
        this.brandName = product.getBrand() != null ? product.getBrand().getName() : null;
        this.skuDTOs = product.getSkus() == null ? List.of() : product.getSkus().stream().map(SKUDTO::new).toList();
        this.isAvailable = product.getSkus() != null && product.getSkus().stream()
            .flatMap(sku -> sku.getInventories().stream())
            .anyMatch(inventory -> inventory.getStockAvailable() > 0);
        this.approved = product.isApproved();
    }

    // No price or stock here, as it's handled in Inventory and SKU entities
}
