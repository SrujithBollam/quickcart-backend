package com.quickcart.quickcommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products") // Maps this class to a MongoDB collection named "products"
@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public class Product {

    @Id // Marks this field as the primary identifier for MongoDB
    private String id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String imageUrl; // URL for the product image
    private String category; // e.g., "Dairy", "Snacks", "Beverages"
    private String brand;
}