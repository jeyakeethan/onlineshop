package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@NoArgsConstructor
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne
    @JoinColumn(name = "sku_id")  // Link Inventory to SKU
    private SKU sku;  // The SKU this inventory belongs to

    private double costPrice;  // Cost price of this SKU variant
    private double sellingPrice;  // Selling price of this SKU variant
    private int stockAvailable;  // Available stock for this SKU variant

    @Column(nullable = false)
    private int reservedQuantity;

    @Column(nullable = false)
    private int lowStockThreshold;

    @ManyToOne
    @JoinColumn(name = "wishlist_id")  // Foreign key to wishlist table
    private WishList wishlist;
}
