package com.quickcart.quickcommerce.controller;

import com.quickcart.quickcommerce.model.User;
import com.quickcart.quickcommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Marks this class as a REST Controller
@RequestMapping("/api/users") // Base URL path for all endpoints in this controller
public class UserController {

    @Autowired // Injects an instance of UserService
    private UserService userService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        // In a real application, you'd add password hashing and validation here
        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Get all users (for administrative purposes, or testing)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        // Ensure the ID in the path matches the ID in the request body, or set it
        user.setId(id);
        User updatedUser = userService.saveUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Delete a user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Simple login endpoint (without Spring Security yet)
    // In a real app, this would involve password checking and JWT generation
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginUser) {
        // For demonstration: simple check
        List<User> users = userService.getAllUsers();
        boolean isAuthenticated = users.stream()
                .anyMatch(u -> u.getUsername().equals(loginUser.getUsername()) &&
                        u.getPassword().equals(loginUser.getPassword())); // !!! DANGER: Plain text password comparison !!!

        if (isAuthenticated) {
            return new ResponseEntity<>("Login successful for user: " + loginUser.getUsername(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }
}