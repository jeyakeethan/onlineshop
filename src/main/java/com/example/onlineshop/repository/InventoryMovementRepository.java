package com.example.onlineshop.repository;

import com.example.onlineshop.model.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    Page<InventoryMovement> findByInventory_Sku_SkuIdOrderByOccurredAtDescMovementIdDesc(Long skuId, Pageable pageable);
}