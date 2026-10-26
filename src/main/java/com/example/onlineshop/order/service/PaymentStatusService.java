package com.example.onlineshop.order.service;

import com.example.onlineshop.common.security.AuthenticationHelper;
import com.example.onlineshop.order.dto.PaymentStatusDTO;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {
    private final OrderRepository orderRepository;
    private final AuthenticationHelper authenticationHelper;

    public PaymentStatusDTO getStatus(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        String currentUser = authenticationHelper.getCurrentUserId();
        if (order.getUser() == null || !currentUser.equalsIgnoreCase(order.getUser().getEmail())) {
            throw new AccessDeniedException("Order does not belong to the authenticated user");
        }
        return new PaymentStatusDTO(order.getOrderId(), order.getPaymentIntentId(),
                order.getPaymentStatus(), order.getOrderStatus());
    }
}
