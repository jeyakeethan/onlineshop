package com.example.onlineshop.product.dto;

import com.example.onlineshop.product.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyReviewDTO {
    private Long reviewId;
    private Long productId;
    private String productName;
    private String reviewText;
    private Integer rating;
    private String moderationStatus;

    public MyReviewDTO(Review review) {
        this.reviewId = review.getId();
        this.productId = review.getProduct() == null ? null : review.getProduct().getProductId();
        this.productName = review.getProduct() == null ? null : review.getProduct().getName();
        this.reviewText = review.getReviewText();
        this.rating = review.getRating();
        this.moderationStatus = review.getModerationStatus() == null || review.getModerationStatus().isBlank()
                ? "PENDING" : review.getModerationStatus();
    }
}
