package com.example.onlineshop.admin.service;

import com.example.onlineshop.admin.dto.AdminDashboardDTO;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.product.dto.AdminInventoryDTO;
import com.example.onlineshop.product.dto.InventoryMovementDTO;
import com.example.onlineshop.product.repository.InventoryMovementRepository;
import com.example.onlineshop.product.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private static final int MAX_RANGE_DAYS = 366;
    private static final int MAX_RESULTS = 20;

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public AdminDashboardDTO getDashboard(LocalDate from, LocalDate to) {
        LocalDate effectiveTo = to == null ? LocalDate.now() : to;
        LocalDate effectiveFrom = from == null ? effectiveTo.minusDays(30) : from;
        if (effectiveFrom.isAfter(effectiveTo)) {
            throw new IllegalArgumentException("Dashboard start date must not be after end date");
        }
        if (effectiveFrom.plusDays(MAX_RANGE_DAYS).isBefore(effectiveTo)) {
            throw new IllegalArgumentException("Dashboard date range cannot exceed 366 days");
        }

        List<Order> orders = orderRepository.findByOrderDateBetween(effectiveFrom, effectiveTo);
        long paidCount = orders.stream().filter(this::isPaid).count();
        double paidTotal = orders.stream().filter(this::isPaid).mapToDouble(Order::getTotalPrice).sum();
        long pendingCount = orders.size() - paidCount;
        double pendingTotal = orders.stream().filter(order -> !isPaid(order))
                .mapToDouble(Order::getTotalPrice).sum();

        var page = PageRequest.of(0, MAX_RESULTS, Sort.by(Sort.Direction.DESC, "occurredAt"));
        List<AdminInventoryDTO> lowStock = inventoryRepository.findLowStock(PageRequest.of(0, MAX_RESULTS))
                .getContent().stream().map(AdminInventoryDTO::new).toList();
        List<InventoryMovementDTO> events = inventoryMovementRepository
                .findByOccurredAtBetweenOrderByOccurredAtDescMovementIdDesc(
                        effectiveFrom.atStartOfDay(), effectiveTo.plusDays(1).atStartOfDay().minusNanos(1), page)
                .getContent().stream().map(InventoryMovementDTO::new).toList();

        return new AdminDashboardDTO(effectiveFrom, effectiveTo, orders.size(), paidCount, paidTotal,
                pendingCount, pendingTotal, lowStock, events);
    }

    private boolean isPaid(Order order) {
        return order.getPaymentStatus() != null && "PAID".equalsIgnoreCase(order.getPaymentStatus());
    }
}
