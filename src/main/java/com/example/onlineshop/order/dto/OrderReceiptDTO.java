package com.example.onlineshop.order.dto;

import com.example.onlineshop.order.entity.Order;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderReceiptDTO {
    private final Long orderId;
    private final LocalDate orderDate;
    private final String orderStatus;
    private final String paymentStatus;
    private final double subtotal;
    private final double discount;
    private final double shippingFee;
    private final double tax;
    private final double totalPrice;
    private final List<OrderItemDTO> items;
    private final AddressDTO shippingAddress;

    public OrderReceiptDTO(Order order) {
        this.orderId = order.getOrderId();
        this.orderDate = order.getOrderDate();
        this.orderStatus = order.getOrderStatus();
        this.paymentStatus = order.getPaymentStatus();
        this.subtotal = order.getSubtotal();
        this.discount = order.getDiscount();
        this.shippingFee = order.getShippingFee();
        this.tax = order.getTax();
        this.totalPrice = order.getTotalPrice();
        this.items = (order.getOrderItems() == null ? List.<com.example.onlineshop.order.entity.OrderItem>of() : order.getOrderItems())
                .stream().map(OrderItemDTO::new).collect(Collectors.toUnmodifiableList());
        this.shippingAddress = order.getShippingAddress() == null ? null : new AddressDTO(order.getShippingAddress());
    }
}
