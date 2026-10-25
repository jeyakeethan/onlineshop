package com.example.onlineshop.order.controller;

import com.example.onlineshop.order.dto.OrderDTO;
import com.example.onlineshop.order.dto.PaymentDTO;
import com.example.onlineshop.common.service.AdminService;
import com.example.onlineshop.common.service.BuyerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Admin orders", description = "Administrative order management")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final AdminService adminService;
    private final BuyerService buyerService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('order:read')")
    @GetMapping
    public List<OrderDTO> getAllOrders() { return adminService.getAllOrders(); }

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
