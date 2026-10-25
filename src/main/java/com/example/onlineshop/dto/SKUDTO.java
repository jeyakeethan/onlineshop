package com.example.onlineshop.dto;

import com.example.onlineshop.model.Inventory;
import com.example.onlineshop.model.SKU;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SKUDTO {
    private String size;
    private String color;
    private String skuCode;
    private Long productId; // Reference to the product
    private boolean active;
    List<InventoryDTO> inventoryDTOS;

    public SKUDTO(SKU sku) {
        this.size = sku.getSize();
        this.color = sku.getColor();
        this.skuCode = sku.getSkuCode();
        this.productId = sku.getProduct().getProductId();
        this.active = sku.isActive();
        this.inventoryDTOS = sku.getInventories().stream().map(InventoryDTO::new).toList();
    }
}
