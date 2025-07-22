package com.quickcart.quickcommerce.repository;

import com.quickcart.quickcommerce.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this interface as a Spring Data Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    // MongoRepository provides CRUD operations (save, findById, findAll, delete, etc.)
    // You can add custom query methods here if needed, e.g., findByName(String name)
}