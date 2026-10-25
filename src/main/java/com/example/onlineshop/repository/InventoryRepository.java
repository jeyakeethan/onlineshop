package com.example.onlineshop.repository;

import com.example.onlineshop.model.Inventory;
import com.example.onlineshop.model.SKU;
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
