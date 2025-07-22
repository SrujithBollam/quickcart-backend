package com.quickcart.quickcommerce.service;

import com.quickcart.quickcommerce.model.Product;
import com.quickcart.quickcommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class ProductService {

    @Autowired // Injects an instance of ProductRepository
    private ProductRepository productRepository;

    // Method to get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Method to get a product by its ID
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    // Method to add a new product or update an existing one
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Method to delete a product by its ID
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    // You can add more complex business logic here, e.g.,
    // public List<Product> getProductsByCategory(String category) {
    //     return productRepository.findByCategory(category); // Requires a custom method in ProductRepository
    // }
}