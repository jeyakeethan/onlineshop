package com.example.onlineshop.product.dto;

import com.example.onlineshop.product.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminReviewDTO {
    private Long reviewId;
    private String userId;
    private Long productId;
    private String productName;
    private String reviewText;
    private Integer rating;
    private String moderationStatus;
    private String moderationReason;
    private String moderatedBy;
    private java.time.LocalDateTime moderatedAt;

    public AdminReviewDTO(Review review) {
        this.reviewId = review.getId();
        this.userId = review.getUserId();
        this.productId = review.getProduct() == null ? null : review.getProduct().getProductId();
        this.productName = review.getProduct() == null ? null : review.getProduct().getName();
        this.reviewText = review.getReviewText();
        this.rating = review.getRating();
        this.moderationStatus = review.getModerationStatus() == null || review.getModerationStatus().isBlank()
                ? "PENDING" : review.getModerationStatus();
        this.moderationReason = review.getModerationReason();
        this.moderatedBy = review.getModeratedBy();
        this.moderatedAt = review.getModeratedAt();
    }
}
