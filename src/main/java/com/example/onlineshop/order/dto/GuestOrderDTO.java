package com.example.onlineshop.order.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.example.onlineshop.order.entity.Order;

@Getter
@Setter
public class GuestOrderDTO {

    private Long orderId;
    private LocalDate orderDate;
    private String orderStatus;
    private double totalPrice;
    private List<OrderItemDTO> orderItemDTOs;
    private AddressDTO shippingAddress;

    // Constructor to convert Order entity to OrderDTO
    public GuestOrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus();
        this.totalPrice = order.getTotalPrice();
        this.shippingAddress = order.getShippingAddress() == null ? null : new AddressDTO(order.getShippingAddress());

        // Convert OrderItem entities to OrderItemDTO
        this.orderItemDTOs = (order.getOrderItems() == null ? List.<OrderItemDTO>of() : order.getOrderItems().stream()
                .map(OrderItemDTO::new)
                .collect(Collectors.toList()));
    }
}
