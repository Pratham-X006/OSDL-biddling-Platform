package com.bidding.ui;

import com.bidding.enums.UserRole;
import com.bidding.model.User;
import com.bidding.service.AppContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Dashboard screen — shown after login.
 * Shows different options based on whether the user is a BUYER or SELLER.
 */
public class DashboardScreen {

    private VBox root;

    public DashboardScreen() {
        buildUI();
    }

    private void buildUI() {
        User user = AppContext.getCurrentUser();

        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        // ── Top Nav Bar ──
        HBox navBar = buildNavBar(user);

        // ── Content ──
        VBox content = new VBox(32);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(40, 40, 40, 40));
        VBox.setVgrow(content, Priority.ALWAYS);

        // Welcome banner
        VBox welcomeBanner = new VBox(6);
        welcomeBanner.setAlignment(Pos.CENTER);

        Label greetLabel = new Label("Welcome back, " + user.getUsername() + "! 👋");
        greetLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        greetLabel.setTextFill(Color.WHITE);

        String roleDesc = user.getRole() == UserRole.SELLER
            ? "You are logged in as a  🏷  Seller"
            : "You are logged in as a  🛒  Buyer";
        Label roleLabel = new Label(roleDesc);
        roleLabel.setFont(Font.font("System", 15));
        roleLabel.setTextFill(Color.web("#e94560"));

        welcomeBanner.getChildren().addAll(greetLabel, roleLabel);

        // ── Dashboard Cards ──
        // Cards are shown based on role
        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(20);
        cardGrid.setVgap(20);
        cardGrid.setAlignment(Pos.CENTER);

        if (user.getRole() == UserRole.SELLER) {
            // SELLER options
            VBox card1 = buildDashCard("📦", "Add Product",
                "List a new item for auction", "#e94560",
                e -> MainApp.showAddProductScreen());

            VBox card2 = buildDashCard("📋", "My Products",
                "View and manage your listings", "#4ecca3",
                e -> MainApp.showProductListScreen());

            VBox card3 = buildDashCard("📊", "Auction Log",
                "See all bids and auction history", "#f7b731",
                e -> MainApp.showAuctionLogScreen());

            cardGrid.add(card1, 0, 0);
            cardGrid.add(card2, 1, 0);
            cardGrid.add(card3, 0, 1);

        } else {
            // BUYER options
            VBox card1 = buildDashCard("🔍", "Browse Auctions",
                "View all open products to bid on", "#e94560",
                e -> MainApp.showProductListScreen());

            VBox card2 = buildDashCard("📊", "Auction Log",
                "See your bids and auction history", "#f7b731",
                e -> MainApp.showAuctionLogScreen());

            cardGrid.add(card1, 0, 0);
            cardGrid.add(card2, 1, 0);
        }

        // Stats row (quick info)
        HBox statsRow = buildStatsRow(user);

        content.getChildren().addAll(welcomeBanner, statsRow, cardGrid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(navBar, scrollPane);
    }

    /**
     * Builds a clickable dashboard card.
     */
    private VBox buildDashCard(String icon, String title, String desc,
                                String accentColor, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(12);
        card.setPrefWidth(220);
        card.setPrefHeight(150);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 14px;" +
            "-fx-cursor: hand;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 32));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(accentColor));

        Label descLabel = new Label(desc);
        descLabel.setFont(Font.font("System", 12));
        descLabel.setTextFill(Color.web("#aaaaaa"));
        descLabel.setWrapText(true);

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);

        // Card click
        card.setOnMouseClicked(e -> {
            Button dummy = new Button();
            action.handle(new javafx.event.ActionEvent(dummy, null));
        });
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.13);" +
            "-fx-background-radius: 14px; -fx-cursor: hand;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 14px; -fx-cursor: hand;"
        ));

        return card;
    }

    /**
     * Builds a quick-stats bar showing total products and bids.
     */
    private HBox buildStatsRow(User user) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);

        int totalProducts = MainApp.productService.getAllProducts().size();
        int openAuctions = MainApp.productService.getOpenProducts().size();
        int totalBids = MainApp.biddingService.getAllBids().size();

        row.getChildren().addAll(
            buildStatChip("📦 Total Products", String.valueOf(totalProducts)),
            buildStatChip("🟢 Open Auctions", String.valueOf(openAuctions)),
            buildStatChip("🔨 Total Bids", String.valueOf(totalBids))
        );
        return row;
    }

    private VBox buildStatChip(String label, String value) {
        VBox chip = new VBox(4);
        chip.setPadding(new Insets(14, 24, 14, 24));
        chip.setAlignment(Pos.CENTER);
        chip.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 10px;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.web("#e94560"));

        Label textLabel = new Label(label);
        textLabel.setFont(Font.font("System", 12));
        textLabel.setTextFill(Color.web("#aaaaaa"));

        chip.getChildren().addAll(valueLabel, textLabel);
        return chip;
    }

    private HBox buildNavBar(User user) {
        HBox nav = new HBox();
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        nav.setAlignment(Pos.CENTER_LEFT);

        Label appName = new Label("🔨 BidPlatform");
        appName.setFont(Font.font("System", FontWeight.BOLD, 16));
        appName.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label userInfo = new Label("👤 " + user.getUsername() + "  |  " + user.getRole().name());
        userInfo.setTextFill(Color.web("#aaaaaa"));
        userInfo.setFont(Font.font("System", 13));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;" +
                           "-fx-background-radius: 6px; -fx-cursor: hand;");
        logoutBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        logoutBtn.setOnAction(e -> MainApp.logout());

        nav.getChildren().addAll(appName, spacer, userInfo,
            new Label("    "), logoutBtn);
        return nav;
    }

    public VBox getRoot() { return root; }
}
