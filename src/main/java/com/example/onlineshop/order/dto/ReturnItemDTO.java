package com.example.onlineshop.order.dto;

import com.example.onlineshop.order.entity.ReturnRequestItem;
import lombok.Getter;

@Getter
public class ReturnItemDTO {
    private final Long orderItemId;
    private final int quantity;
    private final String reason;

    public ReturnItemDTO(ReturnRequestItem item) {
        this.orderItemId = item.getOrderItem().getOrderItemId();
        this.quantity = item.getQuantity();
        this.reason = item.getReason();
    }
}
