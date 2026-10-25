package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.GuestOrderLookupRequest;
import com.example.onlineshop.order.dto.GuestOrderLookupResponse;
import com.example.onlineshop.order.dto.GuestOrderLookupVerifyRequest;
import com.example.onlineshop.order.dto.GuestOrderAccessDTO;
import com.example.onlineshop.order.dto.GuestOrderDTO;
import com.example.onlineshop.common.service.BuyerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Guest orders", description = "Privacy-preserving guest order verification")
@RestController
@RequestMapping("/api/v1/guest-orders")
@RequiredArgsConstructor
public class GuestOrderController {
    private final BuyerService buyerService;

    @Operation(summary = "Request guest order verification", description = "Request verification by order reference and email without revealing whether the order exists.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Generic verification response returned."),
            @ApiResponse(responseCode = "400", description = "Invalid request format.")
    })
    @PostMapping("/lookup/request")
    public ResponseEntity<GuestOrderLookupResponse> requestLookup(@RequestBody GuestOrderLookupRequest request) {
        return ResponseEntity.accepted().body(buyerService.requestGuestOrderLookup(request));
    }

    @Operation(summary = "Verify guest order lookup", description = "Consume a guest order challenge and issue a short-lived scoped access credential.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Guest order access granted."),
            @ApiResponse(responseCode = "400", description = "Invalid or expired challenge.")
    })
    @PostMapping("/lookup/verify")
    public GuestOrderAccessDTO verifyLookup(@RequestBody GuestOrderLookupVerifyRequest request) {
        return buyerService.verifyGuestOrderLookup(request);
    }

    @Operation(summary = "Get a verified guest order", description = "Return a guest order only when a valid scoped verification credential is supplied.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verified guest order returned."),
            @ApiResponse(responseCode = "403", description = "Missing, invalid, or expired scoped credential."),
            @ApiResponse(responseCode = "404", description = "Guest order not found.")
    })
    @GetMapping("/{orderId}")
    public GuestOrderDTO getVerifiedOrder(
            @PathVariable Long orderId,
            @RequestHeader(value = "X-Guest-Order-Access", required = false) String accessToken) {
        return buyerService.getVerifiedGuestOrder(orderId, accessToken);
    }
}
