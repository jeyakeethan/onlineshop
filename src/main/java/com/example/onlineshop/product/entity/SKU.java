package com.example.onlineshop.product.entity;

import com.example.onlineshop.cart.entity.*;
import com.example.onlineshop.content.entity.*;
import com.example.onlineshop.order.entity.*;
import com.example.onlineshop.product.entity.*;
import com.example.onlineshop.promotion.entity.*;
import com.example.onlineshop.shipping.entity.*;
import com.example.onlineshop.user.entity.*;
import com.example.onlineshop.wishlist.entity.*;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skus", uniqueConstraints = {
    @UniqueConstraint(name = "uk_sku_code", columnNames = "sku_code"),
    @UniqueConstraint(name = "uk_product_variant_options", columnNames = {"product_id", "size", "color"})
})
@Getter
@Setter
@NoArgsConstructor
public class SKU implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skuId;  // Unique identifier for SKU

    private String size;  // Size of the SKU
    private String color;  // Color of the SKU
    private String skuCode;  // SKU code (unique identifier for the variant)
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)  // Link SKU to Product
    private Product product;  // The product this SKU belongs to

    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "sku", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();  // A SKU can have multiple images
}
