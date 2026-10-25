package com.example.onlineshop.order.dto;

import com.example.onlineshop.order.entity.OrderStatusHistory;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class OrderStatusHistoryDTO {
    private final String status;
    private final LocalDateTime changedAt;
    private final String note;

    public OrderStatusHistoryDTO(OrderStatusHistory history) {
        this.status = history.getStatus();
        this.changedAt = history.getChangedAt();
        this.note = history.getNote();
    }
}
