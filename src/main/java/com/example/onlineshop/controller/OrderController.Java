package com.example.onlineshop.controller;

import com.example.onlineshop.dto.CheckoutQuoteDTO;
import com.example.onlineshop.dto.CheckoutQuoteRequestDTO;
import com.example.onlineshop.dto.CouponValidationDTO;
import com.example.onlineshop.dto.CouponValidationRequestDTO;
import com.example.onlineshop.dto.GuestOrderDTO;
import com.example.onlineshop.dto.ShippingMethodDTO;
import com.example.onlineshop.dto.OrderDTO;
import com.example.onlineshop.dto.PaymentDTO;
import com.example.onlineshop.dto.OrderRequestDTO;
import com.example.onlineshop.service.AsyncOrderService;
import com.example.onlineshop.service.AdminService;
import com.example.onlineshop.service.BuyerService;
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

@Tag(name = "Orders", description = "Checkout, order history, and status management")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BuyerService buyerService;
    private final AdminService adminService;
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

    @Operation(summary = "Get all orders (admin)", description = "Retrieve all orders placed in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all orders."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read')")
    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return adminService.getAllOrders();
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

    @Operation(summary = "Get orders by user", description = "Retrieve all orders placed by a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No orders found for the user."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read') or #userId == authentication.name")
    @GetMapping("/users/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable String userId) {
        return buyerService.getOrdersByUser(userId);
    }

    @Operation(summary = "Update order status", description = "Update the status of an existing order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid order status."),
            @ApiResponse(responseCode = "404", description = "Order not found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PatchMapping("/{orderId}/status")
    public String updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        buyerService.updateOrderStatus(orderId, body.get("status"));
        return "Order status updated successfully.";
    }

    @Operation(summary = "Process order payment", description = "Process the payment for an existing order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid payment details."),
            @ApiResponse(responseCode = "404", description = "Order not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{orderId}/payments")
    public String processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentDTO paymentDTO) {
        buyerService.processPayment(orderId, paymentDTO);
        return "Payment processed successfully.";
    }

    @Operation(summary = "Get order history for a user", description = "Retrieve all orders placed by a user, including past orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order history retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "No order history found."),
            @ApiResponse(responseCode = "403", description = "Forbidden.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.name")
    @GetMapping("/users/{userId}/history")
    public List<OrderDTO> getOrderHistory(@PathVariable String userId) {
        return buyerService.getOrderHistory(userId);
    }

    @Operation(summary = "Cancel an order", description = "Cancel an existing order by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully."),
            @ApiResponse(responseCode = "404", description = "Order not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized.")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{orderId}")
    public String cancelOrder(@PathVariable Long orderId) {
        buyerService.cancelOrder(orderId);
        return "Order cancelled successfully.";
    }
}
