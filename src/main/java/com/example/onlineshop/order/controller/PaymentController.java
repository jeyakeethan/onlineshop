package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.PaymentIntentCreateRequest;
import com.example.onlineshop.order.dto.PaymentIntentDTO;
import com.example.onlineshop.order.service.PaymentIntentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments", description = "Provider payment intent creation")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentIntentService paymentIntentService;

    @Operation(summary = "Create a server-authoritative payment intent",
            description = "Create or replay a provider payment intent using the persisted order total.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment intent created or replayed."),
            @ApiResponse(responseCode = "400", description = "Invalid order, currency, provider or idempotency key."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "403", description = "The order belongs to another customer.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/intents")
    public PaymentIntentDTO createPaymentIntent(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody PaymentIntentCreateRequest request) {
        return paymentIntentService.create(request, idempotencyKey);
    }
}
