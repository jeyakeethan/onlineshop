package com.example.onlineshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    private double subtotal;
    private double discount;
    private double shippingFee;
    private double tax;
    private String shippingMethod;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "email")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

}
