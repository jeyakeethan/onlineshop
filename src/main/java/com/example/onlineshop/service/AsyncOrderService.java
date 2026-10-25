package com.example.onlineshop.service;

import com.example.onlineshop.dto.AddressDTO;
import com.example.onlineshop.dto.OrderEvent;
import com.example.onlineshop.dto.OrderRequestDTO;
import com.example.onlineshop.model.Address;
import com.example.onlineshop.model.Inventory;
import com.example.onlineshop.model.Order;
import com.example.onlineshop.model.OrderItem;
import com.example.onlineshop.model.User;
import com.example.onlineshop.repository.AddressRepository;
import com.example.onlineshop.repository.InventoryRepository;
import com.example.onlineshop.repository.OrderRepository;
import com.example.onlineshop.repository.UserRepository;
import com.example.onlineshop.util.AuthenticationHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AsyncOrderService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AuthenticationHelper authenticationHelper;
    private final OrderProducerService producer;

    @Value("${app.kafka.enabled:false}")
    private boolean kafkaEnabled;

    @Transactional
    public Map<String, Object> place(OrderRequestDTO request) {
        return place(request, null);
    }

    @Transactional
    public Map<String, Object> place(OrderRequestDTO request, String idempotencyKey) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        String userId = request.getUserId() == null || request.getUserId().isBlank()
                ? authenticationHelper.getCurrentUserId() : request.getUserId();
        String authenticatedUserId = authenticationHelper.getCurrentUserId();
        if (!Objects.equals(userId, authenticatedUserId)) {
            throw new org.springframework.security.access.AccessDeniedException("Order user does not match authenticated user");
        }
        if (idempotencyKey != null && idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency-Key must not be blank");
        }
        if (idempotencyKey != null) {
            var existing = orderRepository.findByUser_emailAndIdempotencyKey(userId, idempotencyKey);
            if (existing.isPresent()) {
                Order prior = existing.get();
                return Map.of("orderId", prior.getOrderId(), "status", prior.getOrderStatus(), "paymentIntentId", prior.getPaymentIntentId());
            }
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Map<Long, Inventory> inventories = inventoryRepository.findAllById(request.getItems().stream()
            .map(OrderRequestDTO.OrderItemRequestDTO::getInventoryId).toList()).stream()
                .collect(Collectors.toMap(Inventory::getInventoryId, Function.identity()));
        if (inventories.size() != request.getItems().size()) {
            throw new IllegalArgumentException("One or more products were not found");
        }

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderEvent.Status.PENDING.name());
        order.setUser(user);
        order.setIdempotencyKey(idempotencyKey);
        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;
        for (var item : request.getItems()) {
            Inventory inventory = inventories.get(item.getInventoryId());
            if (item.getQuantity() < 1 || inventory.getStockAvailable() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            OrderItem orderItem = new OrderItem(inventory.getSku(), item.getQuantity(), inventory.getSellingPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            total += inventory.getSellingPrice() * item.getQuantity();
            inventory.setStockAvailable(inventory.getStockAvailable() - item.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
        }
        order.setOrderItems(orderItems);
        order.setSubtotal(total);
        double shippingFee = "EXPRESS".equalsIgnoreCase(request.getShippingMethod()) ? 12.50 : 5.00;
        double tax = total * 0.08;
        double calculatedTotal = total + shippingFee + tax;
        if (request.getQuoteExpiresAt() != null && request.getQuoteExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Checkout quote has expired");
        }
        if (request.getQuotedTotal() != null && Math.abs(request.getQuotedTotal() - calculatedTotal) > 0.01) {
            throw new IllegalArgumentException("Checkout quote no longer matches the cart");
        }
        order.setShippingMethod(request.getShippingMethod() == null ? "STANDARD" : request.getShippingMethod().toUpperCase());
        order.setShippingFee(shippingFee);
        order.setTax(tax);
        order.setTotalPrice(calculatedTotal);
        AddressDTO shipping = request.getShippingAddress();
        if (shipping != null) {
            Address address = new Address();
            address.setStreet(shipping.getStreet());
            address.setCity(shipping.getCity());
            address.setState(shipping.getState());
            address.setCountry(shipping.getCountry());
            address.setPostalCode(shipping.getPostalCode());
            address.setUser(user);
            order.setShippingAddress(addressRepository.save(address));
        }
        order = orderRepository.save(order);
        OrderEvent event = new OrderEvent(order.getOrderId(), OrderEvent.Status.PENDING, request);
        if (!kafkaEnabled) {
            fulfillLocal(event);
            payLocal(event.getOrderId());
            order = find(event.getOrderId());
            return Map.of("orderId", order.getOrderId(), "status", order.getOrderStatus());
        }

        producer.publish(event);
        return Map.of("orderId", order.getOrderId(), "status", order.getOrderStatus());
    }

    public void fulfill(OrderEvent event) {
        fulfillInternal(event, true);
    }

    public void pay(Long orderId) {
        payInternal(orderId, true);
    }

    private void fulfillLocal(OrderEvent event) {
        fulfillInternal(event, false);
    }

    private void payLocal(Long orderId) {
        payInternal(orderId, false);
    }

    private void fulfillInternal(OrderEvent event, boolean withRandomFailure) {
        Order order = find(event.getOrderId());
        if (OrderEvent.Status.FULFILLED.name().equals(order.getOrderStatus())
                || OrderEvent.Status.PAID.name().equals(order.getOrderStatus())) return;
        if (withRandomFailure) {
            failRandomly("fulfillment");
        }
        for (var item : order.getOrderItems()) {
            Inventory inventory = inventoryRepository.findBySku(item.getSku()).stream().findFirst().orElse(null);
            if (inventory != null && inventory.getStockAvailable() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock during fulfillment");
            }
        }
        order.setOrderStatus(OrderEvent.Status.FULFILLED.name());
        orderRepository.save(order);
    }

    private void payInternal(Long orderId, boolean withRandomFailure) {
        Order order = find(orderId);
        if (OrderEvent.Status.PAID.name().equals(order.getOrderStatus())) return;
        if (withRandomFailure) {
            failRandomly("payment");
        }
        order.setOrderStatus(OrderEvent.Status.PAID.name());
        orderRepository.save(order);
    }

    public void fail(Long orderId) {
        Order order = find(orderId);
        order.setOrderStatus(OrderEvent.Status.FAILED.name());
        orderRepository.save(order);
    }

    private Order find(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    private void failRandomly(String step) {
        if (ThreadLocalRandom.current().nextInt(10) < 3) {
            throw new IllegalStateException("Transient " + step + " failure");
        }
    }
}
