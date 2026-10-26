package com.example.onlineshop.admin.dto;

import com.example.onlineshop.product.dto.AdminInventoryDTO;
import com.example.onlineshop.product.dto.InventoryMovementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class AdminDashboardDTO {
    private LocalDate from;
    private LocalDate to;
    private long totalOrders;
    private long paidOrderCount;
    private double paidOrderTotal;
    private long pendingOrderCount;
    private double pendingOrderTotal;
    private List<AdminInventoryDTO> lowStockItems;
    private List<InventoryMovementDTO> recentEvents;
}
