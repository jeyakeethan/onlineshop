package com.example.onlineshop.product.repository;

import com.example.onlineshop.product.entity.Inventory;
import com.example.onlineshop.product.entity.SKU;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findBySku(SKU sku);

    @Query("select inventory from Inventory inventory where inventory.stockAvailable <= inventory.lowStockThreshold")
    Page<Inventory> findLowStock(Pageable pageable);
}
