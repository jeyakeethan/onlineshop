package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "card_items")
@Getter
@Setter
@NoArgsConstructor
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    private int quantity;

    public CartItem (Cart cart, Inventory inventory, int quantity) {
        this.cart = cart;
        this.inventory = inventory;
        this.quantity = quantity;
    }
}
