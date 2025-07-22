package com.quickcart.quickcommerce.repository;

import com.quickcart.quickcommerce.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this interface as a Spring Data Repository
public interface UserRepository extends MongoRepository<User, String> {
    // MongoRepository provides CRUD operations for the User model
    // You might add custom query methods here later, e.g., findByUsername(String username)
}