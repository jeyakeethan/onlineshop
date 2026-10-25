package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.example.onlineshop.order.entity.Order;

@Getter
@Setter
public class OrderDTO {

    private Long orderId;
    private LocalDate orderDate;
    private String orderStatus;
    private double totalPrice;
    private String paymentStatus;
    private String paymentIntentId;
    private List<OrderItemDTO> orderItems;
    private AddressDTO shippingAddress;
    private List<OrderStatusHistoryDTO> statusHistory;

    // Constructor to convert Order entity to OrderDTO
    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus();
        this.totalPrice = order.getTotalPrice();
        this.paymentStatus = order.getPaymentStatus();
        this.paymentIntentId = order.getPaymentIntentId();
        this.shippingAddress = order.getShippingAddress() == null ? null : new AddressDTO(order.getShippingAddress());

        // Convert OrderItem entities to OrderItemDTO
        this.orderItems = (order.getOrderItems() == null ? List.<com.example.onlineshop.order.entity.OrderItem>of() : order.getOrderItems()).stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList());
        this.statusHistory = (order.getStatusHistory() == null ? List.<com.example.onlineshop.order.entity.OrderStatusHistory>of() : order.getStatusHistory()).stream()
                .map(OrderStatusHistoryDTO::new)
                .collect(Collectors.toList());
    }
}
