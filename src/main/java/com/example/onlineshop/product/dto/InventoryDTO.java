package com.example.onlineshop.product.dto;

import com.example.onlineshop.product.entity.Inventory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryDTO {

    private Long inventoryId;
    private Long skuId;  // SKU ID for this inventory record
    private double costPrice;
    private double sellingPrice;
    private int stockAvailable;
    private int lowStockThreshold;

    public InventoryDTO(Inventory inventory) {
        this.inventoryId = inventory.getInventoryId();
        this.skuId = inventory.getSku().getSkuId();
        this.costPrice = inventory.getCostPrice();
        this.sellingPrice = inventory.getSellingPrice();
        this.stockAvailable = inventory.getStockAvailable();
        this.lowStockThreshold = inventory.getLowStockThreshold();
    }
}
