package com.example.onlineshop.product.dto;

import com.example.onlineshop.product.entity.InventoryMovement;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InventoryMovementDTO {
    private final Long movementId;
    private final Long inventoryId;
    private final Long variantId;
    private final int quantityDelta;
    private final String reason;
    private final String actorId;
    private final String reference;
    private final LocalDateTime occurredAt;

    public InventoryMovementDTO(InventoryMovement movement) {
        this.movementId = movement.getMovementId();
        this.inventoryId = movement.getInventory().getInventoryId();
        this.variantId = movement.getInventory().getSku().getSkuId();
        this.quantityDelta = movement.getQuantityDelta();
        this.reason = movement.getReason();
        this.actorId = movement.getActorId();
        this.reference = movement.getReference();
        this.occurredAt = movement.getOccurredAt();
    }
}
