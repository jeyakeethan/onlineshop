package com.example.onlineshop.report.service;

import com.example.onlineshop.report.dto.*;
import com.example.onlineshop.product.dto.ProductDTO;
import com.example.onlineshop.product.dto.SKUDTO;
import com.example.onlineshop.user.dto.UserDTO;
import com.example.onlineshop.product.entity.Inventory;
import com.example.onlineshop.order.entity.Order;
import com.example.onlineshop.product.entity.Product;
import com.example.onlineshop.user.entity.User;
import com.example.onlineshop.product.repository.InventoryRepository;
import com.example.onlineshop.product.repository.ProductRepository;
import com.example.onlineshop.order.repository.OrderRepository;
import com.example.onlineshop.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public String getSalesReport() {
        List<SalesReportDTO> salesReport = generateSalesReport();
        return formatAsJson(salesReport);
    }

    public String getUserStatistics() {
        List<UserDTO> userReport = generateUserActivityReport();
        return formatAsJson(userReport);
    }

    public String getProductStats() {
        List<ProductDTO> productReport = generateProductReport();
        return formatAsJson(productReport);
    }

    // Convert any report object to JSON String
    private String formatAsJson(Object data) {
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error formatting report as JSON", e);
        }
    }

    public List<ProductDTO> generateProductReport() {
        return productRepository.findAll().stream()
                .map(product -> {
                    // Fetch all SKUs for the given product
                    List<SKUDTO> skuDTOs = product.getSkus().stream()
                            .map(sku -> {
                                // Fetch all inventory records for the SKU
                                List<Inventory> inventories = inventoryRepository.findBySku(sku);

                                // Aggregate stock availability & pricing from inventory records
                                int totalStock = inventories.stream().mapToInt(Inventory::getStockAvailable).sum();
                                double avgSellingPrice = inventories.stream()
                                        .mapToDouble(Inventory::getSellingPrice)
                                        .average().orElse(0.0);

                                return new SKUDTO(sku);
                            })
                            .collect(Collectors.toList());

                    return new ProductDTO(product);
                })
                .collect(Collectors.toList());
    }


    public List<SalesReportDTO> generateSalesReport() {
        // Fetch all orders
        List<Order> orders = orderRepository.findAll();

        // Generate the sales report
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())  // Flatten order items from each order
                .map(orderItem -> {
                    // Get the details from order item
                    Long skuId = orderItem.getSku().getSkuId();  // SKU ID of the order item
                    Double priceAtSale = orderItem.getPriceAtPurchase();  // Price at sale from order item
                    Integer quantity = orderItem.getQuantity();  // Quantity from order item
                    Double totalPrice = quantity * priceAtSale;  // Calculate total price

                    // Create and return the SalesReportDTO for each order item
                    return new SalesReportDTO(
                            orderItem.getOrder().getOrderId(),
                            skuId,
                            quantity,
                            priceAtSale,
                            totalPrice,
                            orderItem.getOrder().getOrderDate()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<UserDTO> generateUserActivityReport() {
        // Fetch all users from the repository
        List<User> users = userRepository.findAll();

        // Convert to DTO and return the result
        return users.stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setEmail(user.getUserId());
                    userDTO.setFirstName(user.getFirstName());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setLastLoginTime(user.getLastLoginTime());
                    return userDTO;
                })
                .collect(Collectors.toList());
    }
}
