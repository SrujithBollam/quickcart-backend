package com.quickcart.quickcommerce.service;

import com.quickcart.quickcommerce.model.User;
import com.quickcart.quickcommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class UserService {

    @Autowired // Injects an instance of UserRepository
    private UserRepository userRepository;

    // Method to get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Method to get a user by ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // Method to save a new user or update an existing one
    public User saveUser(User user) {
        // In a real app, you'd hash the password here before saving!
        return userRepository.save(user);
    }

    // Method to delete a user by ID
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    // Example: Method to find a user by username (requires custom method in repository)
    // public Optional<User> findByUsername(String username) {
    //     return userRepository.findByUsername(username);
    // }
}