package com.example.onlineshop.product.repository;

import com.example.onlineshop.product.entity.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    Page<InventoryMovement> findByInventory_Sku_SkuIdOrderByOccurredAtDescMovementIdDesc(Long skuId, Pageable pageable);
}
