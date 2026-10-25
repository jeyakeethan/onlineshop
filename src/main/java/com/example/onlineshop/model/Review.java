package com.example.onlineshop.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.io.Serializable;

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

    public Review(String userId, Product product, String reviewText, Integer rating) {
        this.userId = userId;
        this.product = product;
        this.reviewText = reviewText;
        this.rating = rating;
    }
}
