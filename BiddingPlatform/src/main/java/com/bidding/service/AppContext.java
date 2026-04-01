package com.bidding.service;

import com.bidding.model.User;

/**
 * Holds the global application state — currently logged-in user.
 * Acts as a simple session manager for the desktop app.
 */
public class AppContext {

    // Currently logged-in user (null if no one is logged in)
    private static User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
