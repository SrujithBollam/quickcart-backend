package com.quickcart.quickcommerce.service;

import com.quickcart.quickcommerce.model.Order;
import com.quickcart.quickcommerce.model.Product;
import com.quickcart.quickcommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class OrderService {

    @Autowired // Injects an instance of OrderRepository
    private OrderRepository orderRepository;

    @Autowired // Injects an instance of ProductService to interact with products
    private ProductService productService; // We will use this to check product availability/stock

    // Method to get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Method to get an order by its ID
    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    // Method to get orders by a specific user ID
    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }

    // Method to place a new order
    public Order placeOrder(Order order) {
        // --- Basic Business Logic for Placing an Order ---
        // 1. Set order date
        order.setOrderDate(LocalDateTime.now());
        // 2. Set initial status
        order.setStatus("PENDING");
        // 3. Calculate total amount and potentially check product stock (more robust in a real app)
        double calculatedTotal = 0;
        for (Order.OrderItem item : order.getItems()) {
            Optional<Product> productOptional = productService.getProductById(item.getProductId());
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                // Ensure the price per unit in the order item reflects the current product price
                // Or you might have a different pricing strategy
                item.setPricePerUnit(product.getPrice());
                item.setSubtotal(item.getQuantity() * product.getPrice());
                calculatedTotal += item.getSubtotal();

                // !!! In a real app, you would also deduct stock from the Product here !!!
                // product.setStock(product.getStock() - item.getQuantity());
                // productService.saveProduct(product); // Save updated stock
            } else {
                // Handle case where product is not found (e.g., throw an exception)
                throw new IllegalArgumentException("Product with ID " + item.getProductId() + " not found.");
            }
        }
        order.setTotalAmount(calculatedTotal);

        // 4. Save the order
        return orderRepository.save(order);
    }

    // Method to update an existing order (e.g., status update by admin/delivery partner)
    public Optional<Order> updateOrder(String id, Order updatedOrder) {
        return orderRepository.findById(id).map(existingOrder -> {
            existingOrder.setDeliveryAddress(updatedOrder.getDeliveryAddress());
            existingOrder.setStatus(updatedOrder.getStatus());
            existingOrder.setDeliveryExpectedAt(updatedOrder.getDeliveryExpectedAt());
            // Add other fields to update as needed
            return orderRepository.save(existingOrder);
        });
    }

    // Method to delete an order (e.g., if cancelled)
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }
}