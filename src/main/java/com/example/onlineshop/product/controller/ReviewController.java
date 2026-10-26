package com.example.onlineshop.product.controller;

import com.example.onlineshop.product.dto.ReviewCreateDTO;
import com.example.onlineshop.product.dto.ReviewUpdateDTO;
import com.example.onlineshop.product.entity.Review;
import com.example.onlineshop.common.service.BuyerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final BuyerService buyerService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Review createReview(@Valid @RequestBody ReviewCreateDTO request) {
        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        return buyerService.createEligibleReview(request.getProductId(), request);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{reviewId}")
    public Review updateReview(@PathVariable Long reviewId, @RequestBody ReviewUpdateDTO request) {
        return buyerService.updateOwnReview(reviewId, request);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable Long reviewId) {
        buyerService.deleteOwnReview(reviewId);
    }
}
