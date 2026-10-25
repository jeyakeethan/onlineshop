package com.example.onlineshop.wishlist.entity;

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
import lombok.Setter;
import java.io.Serializable;

import java.util.List;

@Entity
@Table(name = "wishlists")
@Getter
@Setter
public class WishList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "email")
    private User user; // A user can have only one wishlist

    @OneToMany(mappedBy = "wishlist") // Each inventory is related to one wishlist
    private List<Inventory> inventories; // Store inventories in the wishlist (based on SKU)
}
