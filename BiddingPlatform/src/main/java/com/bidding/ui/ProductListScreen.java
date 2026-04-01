package com.bidding.ui;

import java.util.List;

import com.bidding.enums.AuctionStatus;
import com.bidding.enums.UserRole;
import com.bidding.model.Product;
import com.bidding.model.User;
import com.bidding.service.AppContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Displays all products/auctions.
 * Buyers see "Place Bid" button on open auctions.
 * Sellers see "Close Auction" button on their own listings.
 */
public class ProductListScreen {

    private VBox root;

    public ProductListScreen() {
        buildUI();
    }

    private void buildUI() {
        User user = AppContext.getCurrentUser();

        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        HBox navBar = buildNavBar(user);

        // Content area
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        VBox.setVgrow(content, Priority.ALWAYS);

        // Header row
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(
            user.getRole() == UserRole.SELLER ? "📋  My Product Listings" : "🔍  Browse Auctions"
        );
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white;" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        );
        refreshBtn.setOnAction(e -> MainApp.showProductListScreen());

        header.getChildren().addAll(titleLabel, spacer, refreshBtn);

        // Products container (scrollable)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox productsList = new VBox(14);
        productsList.setPadding(new Insets(4, 0, 4, 0));

        // Load products based on role
        List<Product> products;
        if (user.getRole() == UserRole.SELLER) {
            products = MainApp.productService.getProductsBySeller(user.getUserId());
        } else {
            products = MainApp.productService.getAllProducts();
        }

        if (products.isEmpty()) {
            Label emptyLabel = new Label(
                user.getRole() == UserRole.SELLER
                    ? "You haven't listed any products yet.\nClick 'Add Product' to get started!"
                    : "No auctions available right now.\nCheck back soon!"
            );
            emptyLabel.setTextFill(Color.web("#aaaaaa"));
            emptyLabel.setFont(Font.font("System", 15));
            emptyLabel.setWrapText(true);
            emptyLabel.setStyle("-fx-text-alignment: center;");
            VBox emptyBox = new VBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            productsList.getChildren().add(emptyBox);
        } else {
            for (Product product : products) {
                productsList.getChildren().add(buildProductCard(product, user));
            }
        }

        scrollPane.setContent(productsList);
        content.getChildren().addAll(header, scrollPane);

        ScrollPane pageScroll = new ScrollPane(content);
        pageScroll.setFitToWidth(true);
        pageScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(pageScroll, Priority.ALWAYS);

        root.getChildren().addAll(navBar, pageScroll);
    }

    /**
     * Builds a card for one product.
     */
    private HBox buildProductCard(Product product, User user) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(18, 22, 18, 22));
        card.setAlignment(Pos.CENTER_LEFT);

        boolean isOpen = product.getStatus() == AuctionStatus.OPEN;

        card.setStyle(
            "-fx-background-color: rgba(255,255,255," + (isOpen ? "0.08" : "0.04") + ");" +
            "-fx-background-radius: 12px;"
        );

        // Status indicator
        Label statusDot = new Label(isOpen ? "🟢" : "🔴");
        statusDot.setFont(Font.font("System", 18));

        // Product info
        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.WHITE);

        Label descLabel = new Label(
            product.getDescription().isEmpty() ? "No description" : product.getDescription()
        );
        descLabel.setFont(Font.font("System", 12));
        descLabel.setTextFill(Color.web("#aaaaaa"));
        descLabel.setWrapText(true);

        HBox priceRow = new HBox(16);
        Label basePriceLabel = new Label("Start: ₹" + String.format("%.2f", product.getBasePrice()));
        basePriceLabel.setFont(Font.font("System", 12));
        basePriceLabel.setTextFill(Color.web("#888899"));

        Label highBidLabel = new Label("Highest: ₹" + String.format("%.2f", product.getHighestBid()));
        highBidLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        highBidLabel.setTextFill(Color.web("#4ecca3"));

        Label bidderLabel = new Label("by " + product.getHighestBidder());
        bidderLabel.setFont(Font.font("System", 12));
        bidderLabel.setTextFill(Color.web("#aaaaaa"));

        priceRow.getChildren().addAll(basePriceLabel, highBidLabel, bidderLabel);

        Label statusLabel = new Label(isOpen ? "OPEN" : "CLOSED");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        statusLabel.setTextFill(isOpen ? Color.web("#4ecca3") : Color.web("#ff6b6b"));
        statusLabel.setStyle(
            "-fx-background-color: " + (isOpen ? "rgba(78,204,163,0.15)" : "rgba(255,107,107,0.15)") + ";" +
            "-fx-background-radius: 4px; -fx-padding: 2 8 2 8;"
        );

        info.getChildren().addAll(nameLabel, descLabel, priceRow, statusLabel);

        // Action buttons
        VBox btnBox = new VBox(8);
        btnBox.setAlignment(Pos.CENTER);

        if (user.getRole() == UserRole.BUYER && isOpen) {
            Button bidBtn = new Button("Place Bid");
            bidBtn.setPrefWidth(110);
            bidBtn.setPrefHeight(36);
            bidBtn.setFont(Font.font("System", FontWeight.BOLD, 13));
            bidBtn.setTextFill(Color.WHITE);
            bidBtn.setStyle(
                "-fx-background-color: #e94560; -fx-background-radius: 8px; -fx-cursor: hand;"
            );
            bidBtn.setOnAction(e -> MainApp.showPlaceBidScreen(product.getProductId()));
            btnBox.getChildren().add(bidBtn);

        } else if (user.getRole() == UserRole.SELLER
                   && product.getSellerId().equals(user.getUserId())
                   && isOpen) {
            Button closeBtn = new Button("Close Auction");
            closeBtn.setPrefWidth(120);
            closeBtn.setPrefHeight(36);
            closeBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
            closeBtn.setTextFill(Color.WHITE);
            closeBtn.setStyle(
                "-fx-background-color: #f7b731; -fx-background-radius: 8px; -fx-cursor: hand;"
            );
            closeBtn.setOnAction(e -> {
                String err = MainApp.productService.closeAuction(
                    product.getProductId(), user.getUserId()
                );
                if (err == null) {
                    MainApp.showProductListScreen(); // Refresh
                } else {
                    showAlert("Error", err);
                }
            });
            btnBox.getChildren().add(closeBtn);
        }

        // Always show view bids button
        Button viewBidsBtn = new Button("View Bids");
        viewBidsBtn.setPrefWidth(110);
        viewBidsBtn.setPrefHeight(32);
        viewBidsBtn.setFont(Font.font("System", 12));
        viewBidsBtn.setTextFill(Color.web("#aaaaaa"));
        viewBidsBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 8px; -fx-cursor: hand;"
        );
        viewBidsBtn.setOnAction(e -> showBidHistory(product));
        btnBox.getChildren().add(viewBidsBtn);

        card.getChildren().addAll(statusDot, info, btnBox);
        return card;
    }

    /**
     * Shows a popup with bid history for a product.
     */
    private void showBidHistory(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bid History — " + product.getName());
        alert.setHeaderText("Bids for: " + product.getName());

        var bids = MainApp.biddingService.getBidsForProduct(product.getProductId());
        if (bids.isEmpty()) {
            alert.setContentText("No bids placed yet.");
        } else {
            StringBuilder sb = new StringBuilder();
            bids.forEach(b -> sb.append(String.format(
                "• %s  →  ₹%.2f  (%s)%n",
                b.getBidderUsername(), b.getAmount(), b.getTimestamp()
            )));
            alert.setContentText(sb.toString());
        }
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private HBox buildNavBar(User user) {
        HBox nav = new HBox();
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        nav.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Dashboard");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-cursor: hand;");
        backBtn.setFont(Font.font("System", 13));
        backBtn.setOnAction(e -> MainApp.showDashboard());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label appName = new Label("🔨 BidPlatform");
        appName.setFont(Font.font("System", FontWeight.BOLD, 16));
        appName.setTextFill(Color.WHITE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;" +
                           "-fx-background-radius: 6px; -fx-cursor: hand;");
        logoutBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        logoutBtn.setOnAction(e -> MainApp.logout());

        nav.getChildren().addAll(backBtn, spacer, appName, new Label("  "), logoutBtn);
        return nav;
    }

    public VBox getRoot() { return root; }
}
