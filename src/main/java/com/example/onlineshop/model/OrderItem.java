package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "sku_id", nullable = false)
    private SKU sku;

    private int quantity;
    private double priceAtPurchase;  // Storing price at the time of purchase

    @Column(length = 100)
    private String skuCodeSnapshot;

    private boolean isReturned;

    public OrderItem(SKU sku, int quantity, double priceAtPurchase) {
        this.sku = sku;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.skuCodeSnapshot = sku == null ? null : sku.getSkuCode();
    }
}
