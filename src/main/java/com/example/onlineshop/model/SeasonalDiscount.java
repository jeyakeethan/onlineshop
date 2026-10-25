package com.example.onlineshop.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seasonal_discounts")
@Getter
@Setter
public class SeasonalDiscount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    private String offerName;
    private String discountType;  // Either percentage or fixed value
    private double discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
}
