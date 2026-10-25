package com.example.onlineshop.product.entity;

import com.example.onlineshop.cart.entity.*;
import com.example.onlineshop.content.entity.*;
import com.example.onlineshop.order.entity.*;
import com.example.onlineshop.product.entity.*;
import com.example.onlineshop.promotion.entity.*;
import com.example.onlineshop.shipping.entity.*;
import com.example.onlineshop.user.entity.*;
import com.example.onlineshop.wishlist.entity.*;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category_attributes", uniqueConstraints = @UniqueConstraint(
        name = "uk_category_attribute_name",
        columnNames = {"category", "subcategory", "name"}))
@Getter
@Setter
@NoArgsConstructor
public class CategoryAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String category;

    @Column(nullable = false, length = 120)
    private String subcategory;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 32)
    private String dataType;

    @ElementCollection
    @CollectionTable(name = "category_attribute_allowed_values", joinColumns = @JoinColumn(name = "attribute_id"))
    @OrderColumn(name = "display_order")
    @Column(name = "allowed_value", nullable = false, length = 255)
    private List<String> allowedValues = new ArrayList<>();

    @Column(nullable = false)
    private boolean filterable;
}
