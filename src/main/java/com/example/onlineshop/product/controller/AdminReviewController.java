package com.example.onlineshop.product.controller;

import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.product.dto.AdminReviewDTO;
import com.example.onlineshop.product.dto.ReviewRejectDTO;
import com.example.onlineshop.product.dto.ReviewHideDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin reviews", description = "Review moderation queue and history")
@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {
    private final AdminService adminService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('review:read')")
    @GetMapping
    public List<AdminReviewDTO> getReviews(@RequestParam(required = false) String status) {
        return adminService.getAdminReviews(status);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('review:moderate')")
    @PostMapping("/{reviewId}/approve")
    public AdminReviewDTO approveReview(@PathVariable Long reviewId) {
        return adminService.approveReview(reviewId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('review:moderate')")
    @PostMapping("/{reviewId}/reject")
    public AdminReviewDTO rejectReview(@PathVariable Long reviewId, @Valid @org.springframework.web.bind.annotation.RequestBody ReviewRejectDTO request) {
        return adminService.rejectReview(reviewId, request == null ? null : request.getReason());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('review:moderate')")
    @PostMapping("/{reviewId}/hide")
    public AdminReviewDTO hideReview(@PathVariable Long reviewId,
                                     @Valid @org.springframework.web.bind.annotation.RequestBody(required = false) ReviewHideDTO request) {
        return adminService.hideReview(reviewId, request == null ? null : request.getReason());
    }
}
