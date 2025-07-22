package com.quickcart.quickcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders") // Maps this class to a MongoDB collection named "orders"
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Order {

    @Id // Marks this field as the primary identifier for MongoDB
    private String id;
    private String userId; // Reference to the User who placed the order
    private List<OrderItem> items; // List of products in the order
    private double totalAmount;
    private String deliveryAddress;
    private String status; // e.g., "PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"
    private LocalDateTime orderDate; // Timestamp of when the order was placed
    private LocalDateTime deliveryExpectedAt; // Estimated delivery time

    // Nested class to represent an item within an order
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productId; // ID of the product
        private String productName; // Name of the product at the time of order
        private double quantity; // Quantity of the product (e.g., 1.0 for 1 piece, 0.5 for 500g)
        private double pricePerUnit; // Price of the product at the time of order
        private double subtotal; // quantity * pricePerUnit
    }
}