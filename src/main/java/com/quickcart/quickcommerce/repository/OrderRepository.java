package com.quickcart.quickcommerce.repository;

import com.quickcart.quickcommerce.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marks this interface as a Spring Data Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    // MongoRepository provides CRUD operations for the Order model

    // Custom method to find orders by a specific user ID
    List<Order> findByUserId(String userId);

    // You might add other custom query methods here, e.g.,
    // List<Order> findByStatus(String status);
    // List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}