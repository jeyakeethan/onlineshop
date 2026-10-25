package com.example.onlineshop.cart.entity;

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
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "email")
    private User user;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems = new ArrayList<>();
}
