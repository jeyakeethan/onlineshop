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

@Entity
@Table(name = "brands", indexes = {
        @Index(name = "idx_brand_active_name", columnList = "active, name")
})
@Getter
@Setter
@NoArgsConstructor
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active = true;
}
