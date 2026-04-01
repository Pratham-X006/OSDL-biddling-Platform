package com.bidding.ui;

import com.bidding.service.AppContext;
import com.bidding.service.BiddingService;
import com.bidding.service.ProductService;
import com.bidding.service.UserService;
import com.bidding.util.FileManager;
import com.bidding.util.IdGenerator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Bidding Platform JavaFX application.
 * Initializes all services and launches the Welcome screen.
 */
public class MainApp extends Application {

    // Shared service instances (passed between screens)
    public static UserService userService;
    public static ProductService productService;
    public static BiddingService biddingService;
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Initialize data directory and ID counters
        FileManager.initDataDirectory();
        IdGenerator.loadCounters();

        // Initialize services
        userService = new UserService();
        productService = new ProductService();
        biddingService = new BiddingService(productService);

        // Launch welcome screen
        stage.setTitle("🔨 Online Bidding Platform");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(false);

        showWelcomeScreen();
        stage.show();
    }

    /**
     * Navigate to the Welcome screen.
     */
    public static void showWelcomeScreen() {
        WelcomeScreen screen = new WelcomeScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Login screen.
     */
    public static void showLoginScreen() {
        LoginScreen screen = new LoginScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Register screen.
     */
    public static void showRegisterScreen() {
        RegisterScreen screen = new RegisterScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Dashboard.
     */
    public static void showDashboard() {
        DashboardScreen screen = new DashboardScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Add Product screen.
     */
    public static void showAddProductScreen() {
        AddProductScreen screen = new AddProductScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Product List screen.
     */
    public static void showProductListScreen() {
        ProductListScreen screen = new ProductListScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Place Bid screen for a specific product.
     */
    public static void showPlaceBidScreen(Integer productId) {
        PlaceBidScreen screen = new PlaceBidScreen(productId);
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Navigate to Auction Log screen.
     */
    public static void showAuctionLogScreen() {
        AuctionLogScreen screen = new AuctionLogScreen();
        primaryStage.setScene(new Scene(screen.getRoot(), 800, 600));
    }

    /**
     * Logout and return to welcome screen.
     */
    public static void logout() {
        AppContext.logout();
        showWelcomeScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
