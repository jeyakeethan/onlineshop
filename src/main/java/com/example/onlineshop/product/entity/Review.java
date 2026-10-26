package com.example.onlineshop.product.entity;

import com.example.onlineshop.cart.entity.*;
import com.example.onlineshop.content.entity.*;
import com.example.onlineshop.order.entity.*;
import com.example.onlineshop.product.entity.*;
import com.example.onlineshop.promotion.entity.*;
import com.example.onlineshop.shipping.entity.*;
import com.example.onlineshop.user.entity.*;
import com.example.onlineshop.wishlist.entity.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String userId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Lob
    @NotBlank
    private String reviewText;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(length = 32)
    private String moderationStatus = "PENDING";

    @Column(length = 500)
    private String moderationReason;

    @Column(length = 255)
    private String moderatedBy;

    private LocalDateTime moderatedAt;

    public Review(String userId, Product product, String reviewText, Integer rating) {
        this.userId = userId;
        this.product = product;
        this.reviewText = reviewText;
        this.rating = rating;
        this.moderationStatus = "PENDING";
    }
}
