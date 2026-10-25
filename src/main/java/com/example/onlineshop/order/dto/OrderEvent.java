package com.example.onlineshop.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    public enum Status { PENDING, FULFILLED, PAID, FAILED }

    private Long orderId;
    private Status status;
    private OrderRequestDTO payload;
}
