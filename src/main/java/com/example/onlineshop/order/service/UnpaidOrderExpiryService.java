package com.example.onlineshop.order.service;

import com.example.onlineshop.order.dto.OrderEvent;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.order.entity.OrderItem;
import com.example.onlineshop.order.entity.OrderStatusHistory;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.product.entity.Inventory;
import com.example.onlineshop.product.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnpaidOrderExpiryService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderProducerService orderProducerService;
    private final TransactionalOutboxService outboxService;

    @Scheduled(fixedDelayString = "${app.orders.expiry.fixed-delay-ms:60000}")
    @Transactional
    public void expireUnpaidOrders() {
        expireUnpaidOrders(LocalDateTime.now());
    }

    @Transactional
    public int expireUnpaidOrders(LocalDateTime now) {
        List<Order> candidates = orderRepository
                .findByReservationExpiresAtLessThanEqualAndPaymentStatusNotIn(
                        now, List.of("PAID", "EXPIRED"));
        int expired = 0;
        for (Order order : candidates) {
            if (order.getReservationExpiresAt() == null
                    || order.getReservationExpiresAt().isAfter(now)
                    || "PAID".equalsIgnoreCase(order.getPaymentStatus())
                    || "EXPIRED".equalsIgnoreCase(order.getPaymentStatus())) {
                continue;
            }
            releaseReservation(order);
            order.setPaymentStatus("EXPIRED");
            order.setOrderStatus("EXPIRED");
            if (order.getStatusHistory() == null) {
                order.setStatusHistory(new java.util.ArrayList<>());
            }
            order.getStatusHistory().add(new OrderStatusHistory(order, "EXPIRED", "Unpaid order reservation expired"));
            orderRepository.save(order);
            outboxService.enqueue(new OrderEvent(order.getOrderId(), OrderEvent.Status.FAILED, null));
            expired++;
        }
        return expired;
    }

    private void releaseReservation(Order order) {
        if (order.getOrderItems() == null) return;
        for (OrderItem item : order.getOrderItems()) {
            if (item.getSku() == null) continue;
            inventoryRepository.findBySku(item.getSku()).stream().findFirst().ifPresent(inventory -> {
                int quantity = Math.max(0, item.getQuantity());
                inventory.setStockAvailable(inventory.getStockAvailable() + quantity);
                inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - quantity));
                inventoryRepository.save(inventory);
            });
        }
    }
}
