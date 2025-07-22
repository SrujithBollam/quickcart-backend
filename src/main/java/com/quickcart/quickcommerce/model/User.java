package com.quickcart.quickcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users") // Maps this class to a MongoDB collection named "users"
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class User {

    @Id // Marks this field as the primary identifier for MongoDB
    private String id;
    private String username;
    private String password; // Store hashed passwords in a real application!
    private String email;
    private String address; // User's delivery address
    private String phoneNumber;
    private String role; // e.g., "USER", "ADMIN", "DELIVERY_PARTNER"
}