package com.bidding.model;

import com.bidding.enums.UserRole;
import java.io.Serializable;

/**
 * Represents a registered user in the bidding platform.
 * Implements Serializable so it can be stored using Java object serialization.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;       // Wrapper class usage
    private String username;
    private String password;
    private UserRole role;        // BUYER or SELLER

    // Constructor
    public User(Integer userId, String username, String password, UserRole role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "', role=" + role + "}";
    }
}
