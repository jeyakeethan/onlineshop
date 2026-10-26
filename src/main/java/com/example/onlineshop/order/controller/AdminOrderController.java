package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.OrderDTO;
import com.example.onlineshop.order.dto.PaymentDTO;
import com.example.onlineshop.order.dto.FulfillmentPackRequest;
import com.example.onlineshop.order.dto.FulfillmentShipRequest;
import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.common.service.BuyerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

@Tag(name = "Admin orders", description = "Administrative order management")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final AdminService adminService;
    private final BuyerService buyerService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read')")
    @GetMapping
    public Page<OrderDTO> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        return adminService.getAdminOrders(page, size, status, paymentStatus, customerEmail, from, to);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read')")
    @GetMapping("/{orderId}")
    public OrderDTO getOrder(@PathVariable Long orderId) {
        return adminService.getAdminOrder(orderId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{orderId}/fulfillment/start")
    public OrderDTO startFulfillment(@PathVariable Long orderId) {
        return adminService.startFulfillment(orderId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{orderId}/fulfillment/pack")
    public OrderDTO packFulfillment(@PathVariable Long orderId,
                                    @RequestBody(required = false) FulfillmentPackRequest request) {
        return adminService.packFulfillment(orderId, request == null ? null : request.getPackedQuantities());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{orderId}/fulfillment/ship")
    public OrderDTO shipFulfillment(@PathVariable Long orderId,
                                    @RequestBody FulfillmentShipRequest request) {
        return adminService.shipFulfillment(orderId, request == null ? null : request.getCarrier(),
                request == null ? null : request.getTrackingNumber());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{orderId}/fulfillment/deliver")
    public OrderDTO deliverFulfillment(@PathVariable Long orderId) {
        return adminService.deliverFulfillment(orderId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read') or #userId == authentication.name")
    @GetMapping("/users/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable String userId) { return buyerService.getOrdersByUser(userId); }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PatchMapping("/{orderId}/status")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
        buyerService.updateOrderStatus(orderId, body.get("status"));
        return "Order status updated successfully.";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:update')")
    @PostMapping("/{orderId}/payments")
    public String processPayment(@PathVariable Long orderId, @RequestBody PaymentDTO paymentDTO) {
        buyerService.processPayment(orderId, paymentDTO);
        return "Payment processed successfully.";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.name")
    @GetMapping("/users/{userId}/history")
    public List<OrderDTO> getOrderHistory(@PathVariable String userId) { return buyerService.getOrderHistory(userId); }
}
