package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.CheckoutQuoteDTO;
import com.example.onlineshop.order.dto.CheckoutQuoteRequestDTO;
import com.example.onlineshop.order.dto.CouponValidationDTO;
import com.example.onlineshop.order.dto.CouponValidationRequestDTO;
import com.example.onlineshop.order.dto.GuestOrderDTO;
import com.example.onlineshop.order.dto.ShippingMethodDTO;
import com.example.onlineshop.order.dto.OrderDTO;
import com.example.onlineshop.order.dto.PaymentDTO;
import com.example.onlineshop.order.dto.MockPaymentCompletionDTO;
import com.example.onlineshop.order.dto.MockPaymentCompletionRequest;
import com.example.onlineshop.order.dto.OrderRequestDTO;
import com.example.onlineshop.order.service.AsyncOrderService;
import com.example.onlineshop.common.service.BuyerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

@Tag(name = "Orders", description = "Checkout, order history, and status management")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BuyerService buyerService;
    
    private final AsyncOrderService asyncOrderService;

    @Operation(summary = "Server-authoritative checkout quote", description = "Return itemized totals, taxes, coupon preview and shipping method for the current cart.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quote generated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid cart or unsupported shipping method."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/quote")
    public CheckoutQuoteDTO createCheckoutQuote(@RequestBody CheckoutQuoteRequestDTO request) {
        return buyerService.createCheckoutQuote(request);
    }

    @Operation(summary = "List address-eligible shipping methods", description = "Return shipping options with fees, estimates and free-shipping eligibility.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shipping methods returned."),
            @ApiResponse(responseCode = "400", description = "Invalid country or postal code."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/shipping/methods")
    public List<ShippingMethodDTO> getShippingMethods(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String postalCode,
            @RequestParam(required = false) Double subtotal) {
        return buyerService.getShippingMethods(country, postalCode, subtotal);
    }

    @Operation(summary = "Validate a coupon without consuming it", description = "Return preview eligibility and the discount amount without applying redemption.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon preview returned."),
            @ApiResponse(responseCode = "400", description = "Invalid request body."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/coupons/validate")
    public CouponValidationDTO validateCoupon(@RequestBody CouponValidationRequestDTO request) {
        return buyerService.validateCoupon(request);
    }

    @Operation(summary = "Checkout and create an order", description = "Create an order for logged-in users by processing checkout.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Order accepted and published to Kafka for asynchronous processing."),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or empty cart."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<?> checkout(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody(required = false) @Valid OrderRequestDTO request) {
        if (request == null) {
            return ResponseEntity.ok(buyerService.checkout());
        }
        return ResponseEntity.status(201).body(asyncOrderService.place(request, idempotencyKey));
    }

    @Operation(summary = "Guest checkout", description = "Create an order for guest users by processing their guest checkout.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest order created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid guest order data.")
    })
    @PostMapping("/guest")
    public OrderDTO guestCheckout(@RequestBody GuestOrderDTO guestOrderDTO) {
        return buyerService.processGuestCheckout(guestOrderDTO);
    }

    @Operation(summary = "List my orders", description = "Retrieve only the authenticated customer's orders with paging and optional status filtering.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all orders."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public Page<OrderDTO> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return buyerService.getCurrentUserOrders(page, size, status);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve an order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Order not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{orderId}")
    public OrderDTO getOrderById(@PathVariable Long orderId) {
        return buyerService.getOrderById(orderId);
    }

    @Operation(summary = "Get an immutable order receipt", description = "Return the customer's receipt using the financial and line-item snapshots stored on the order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Receipt returned."),
            @ApiResponse(responseCode = "401", description = "Unauthorized."),
            @ApiResponse(responseCode = "403", description = "The order belongs to another customer."),
            @ApiResponse(responseCode = "404", description = "Order not found.")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{orderId}/receipt")
    public com.example.onlineshop.order.dto.OrderReceiptDTO getOrderReceipt(@PathVariable Long orderId) {
        return buyerService.getOrderReceipt(orderId);
    }

    @Operation(summary = "Reorder an existing order", description = "Add currently available variants from an owned order to the customer's cart.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{orderId}/reorder")
    public com.example.onlineshop.order.dto.ReorderResultDTO reorderOrder(@PathVariable Long orderId) {
        return buyerService.reorderOrder(orderId);
    }

    @Operation(summary = "Complete a mock payment", description = "Development/test-only payment simulator for an existing payment intent.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/payments/mock/complete")
    public MockPaymentCompletionDTO completeMockPayment(
            @RequestHeader(value = "X-Mock-Payment-Token", required = false) String authorizationToken,
            @RequestBody MockPaymentCompletionRequest request) {
        return asyncOrderService.completeMockPayment(request, authorizationToken);
    }

    @Operation(summary = "Cancel an order", description = "Cancel an eligible customer order and release its reservation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully."),
            @ApiResponse(responseCode = "404", description = "Order not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{orderId}/cancel")
    public OrderDTO cancelOrder(@PathVariable Long orderId) {
        return buyerService.cancelCurrentUserOrder(orderId);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{orderId}")
    public String cancelOrderLegacy(@PathVariable Long orderId) {
        buyerService.cancelOrder(orderId);
        return "Order cancelled successfully.";
    }
}
