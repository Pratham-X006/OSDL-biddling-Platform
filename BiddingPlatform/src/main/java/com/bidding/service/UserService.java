package com.bidding.service;

import com.bidding.enums.UserRole;
import com.bidding.model.User;
import com.bidding.util.DataStore;
import com.bidding.util.FileManager;
import com.bidding.util.IdGenerator;

import java.util.List;

/**
 * Handles all user-related business logic:
 * registration, login, and user lookup.
 */
public class UserService {

    // Generic DataStore for Users
    private DataStore<User> userStore;

    public UserService() {
        userStore = new DataStore<>();
        // Load users from file into memory on startup
        List<User> savedUsers = FileManager.loadUsers();
        userStore.setAll(savedUsers);
    }

    /**
     * Register a new user.
     * Returns null on success, error message string on failure.
     */
    public String register(String username, String password, UserRole role) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            return "Username cannot be empty.";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 4) {
            return "Password must be at least 4 characters.";
        }

        // Check for duplicate username
        for (User u : userStore.getAll()) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) {
                return "Username already exists. Please choose another.";
            }
        }

        // Create and save the new user
        Integer newId = IdGenerator.nextUserId();
        User newUser = new User(newId, username.trim(), password, role);
        userStore.add(newUser);
        FileManager.saveUsers(userStore.getAll());

        return null; // null means success
    }

    /**
     * Login a user.
     * Returns the User object on success, null on failure.
     */
    public User login(String username, String password) {
        for (User u : userStore.getAll()) {
            if (u.getUsername().equalsIgnoreCase(username.trim()) &&
                u.getPassword().equals(password)) {
                return u;
            }
        }
        return null; // Login failed
    }

    /**
     * Find a user by their ID.
     */
    public User findById(Integer userId) {
        for (User u : userStore.getAll()) {
            if (u.getUserId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Get all registered users.
     */
    public List<User> getAllUsers() {
        return userStore.getAll();
    }
}
