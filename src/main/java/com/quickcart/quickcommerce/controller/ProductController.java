package com.quickcart.quickcommerce.controller;

import com.quickcart.quickcommerce.model.Product;
import com.quickcart.quickcommerce.service.ProductService;
import com.quickcart.quickcommerce.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/products") // Keeping /api/products for now, but consider /api/ai for AI-specific endpoints
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private GeminiService geminiService;

    // GET all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // GET product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST new product or PUT update existing product
    @PostMapping
    public ResponseEntity<Product> createOrUpdateProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    // DELETE product by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // AI-based product recommendations using Gemini
    @GetMapping("/ai-recommendations")
    public Mono<List<Product>> getAIRecommendations(@RequestParam String query) {
        return geminiService.getProductRecommendations(query);
    }

    // AI-based Chatbot for support
    @GetMapping("/ai-chatbot")
    public Mono<String> getChatbotResponse(@RequestParam String message) {
        return geminiService.getChatbotResponse(message);
    }

    // AI-based Intelligent Product Search
    @GetMapping("/ai-search")
    public Mono<List<Product>> intelligentSearch(@RequestParam String query) {
        return geminiService.intelligentProductSearch(query);
    }
}