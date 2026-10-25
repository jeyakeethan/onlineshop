package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.GuestOrderClaimRequest;
import com.example.onlineshop.order.dto.OrderDTO;
import com.example.onlineshop.common.service.BuyerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Guest orders", description = "Claim verified guest orders")
@RestController
@RequestMapping("/api/v1/me/guest-orders")
@RequiredArgsConstructor
public class GuestOrderClaimController {
    private final BuyerService buyerService;

    @Operation(summary = "Claim a guest order", description = "Associate a verified guest order with the authenticated account and record an audit event.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guest order claimed."),
            @ApiResponse(responseCode = "401", description = "Authentication required."),
            @ApiResponse(responseCode = "403", description = "Credential does not belong to the account."),
            @ApiResponse(responseCode = "409", description = "Order is already claimed or ineligible.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/claim")
    public OrderDTO claim(@RequestBody GuestOrderClaimRequest request) {
        return buyerService.claimGuestOrder(request);
    }
}
