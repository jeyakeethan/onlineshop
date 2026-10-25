package com.example.onlineshop.dto;

import com.example.onlineshop.model.Inventory;
import lombok.Getter;

@Getter
public class AdminInventoryDTO {
    private final Long inventoryId;
    private final Long variantId;
    private final String skuCode;
    private final int onHand;
    private final int reserved;
    private final int available;
    private final int lowStockThreshold;
    private final boolean lowStock;

    public AdminInventoryDTO(Inventory inventory) {
        this.inventoryId = inventory.getInventoryId();
        this.variantId = inventory.getSku().getSkuId();
        this.skuCode = inventory.getSku().getSkuCode();
        this.available = inventory.getStockAvailable();
        this.reserved = inventory.getReservedQuantity();
        this.onHand = available + reserved;
        this.lowStockThreshold = inventory.getLowStockThreshold();
        this.lowStock = available <= lowStockThreshold;
    }
}