package com.quickcart.quickcommerce.controller;

import com.quickcart.quickcommerce.model.Order;
import com.quickcart.quickcommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST Controller
@RequestMapping("/api/orders") // Base URL path for all endpoints in this controller
public class OrderController {

    @Autowired // Injects an instance of OrderService
    private OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<Order> placeNewOrder(@RequestBody Order order) {
        try {
            Order placedOrder = orderService.placeOrder(order);
            return new ResponseEntity<>(placedOrder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle cases where a product in the order is not found
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Get all orders (primarily for admin/testing)
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id)
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get orders by User ID (e.g., for a user's order history)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // Update an existing order (e.g., status update by admin/delivery partner)
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable String id, @RequestBody Order order) {
        // Ensure the ID in the path matches for the update
        order.setId(id);
        return orderService.updateOrder(id, order)
                .map(updatedOrder -> new ResponseEntity<>(updatedOrder, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Delete an order by ID (e.g., if cancelled by admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}