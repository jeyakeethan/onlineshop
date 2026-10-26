package com.example.onlineshop.order.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private LocalDate orderDate;
    private String orderStatus;
    private double totalPrice;

    @Column(length = 100)
    private String idempotencyKey;

    @Column(length = 32, nullable = false)
    private String paymentStatus = "REQUIRES_PAYMENT";

    @Column(length = 100)
    private String paymentIntentId = "pi_" + UUID.randomUUID();

    @Column(length = 32)
    private String paymentProvider = "mock";

    @Column(length = 3)
    private String paymentCurrency = "USD";

    @Column(name = "guest_order_reference", length = 100, unique = true)
    private String guestOrderReference;

    @Column(name = "guest_email", length = 255)
    private String guestEmail;

    private double subtotal;
    private double discount;
    private double shippingFee;
    private double tax;
    private String shippingMethod;

    @Column(length = 80)
    private String shipmentCarrier;

    @Column(length = 160)
    private String trackingNumber;

    private LocalDateTime shippedAt;

    private LocalDateTime deliveredAt;

    private LocalDateTime reservationExpiresAt;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "email")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

}
