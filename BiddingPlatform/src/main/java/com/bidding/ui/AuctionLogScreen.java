package com.bidding.ui;

import java.util.List;

import com.bidding.enums.AuctionStatus;
import com.bidding.enums.UserRole;
import com.bidding.model.Bid;
import com.bidding.model.Product;
import com.bidding.model.User;
import com.bidding.service.AppContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
 * Auction Log screen — shows all auctions with their bid history.
 * Sellers see all their auctions and winners.
 * Buyers see all auctions they've participated in.
 */
public class AuctionLogScreen {

    private VBox root;

    public AuctionLogScreen() {
        buildUI();
    }

    private void buildUI() {
        User user = AppContext.getCurrentUser();

        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        HBox navBar = buildNavBar(user);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        VBox.setVgrow(content, Priority.ALWAYS);

        // Header
        Label titleLabel = new Label("📊  Auction Log");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label(
            user.getRole() == UserRole.SELLER
                ? "Overview of all your auctions and their bidding activity"
                : "Your bidding history and auction outcomes"
        );
        subtitleLabel.setTextFill(Color.web("#aaaaaa"));

        // Summary stats bar
        HBox statsBar = buildSummaryStats(user);

        // Table header
        HBox tableHeader = new HBox();
        tableHeader.setPadding(new Insets(8, 16, 8, 16));
        tableHeader.setStyle("-fx-background-color: rgba(233,69,96,0.2); -fx-background-radius: 8px;");

        Label[] headers = {
            makeHeaderLabel("Product", 180),
            makeHeaderLabel("Base Price", 110),
            makeHeaderLabel("Highest Bid", 110),
            makeHeaderLabel("Winner", 130),
            makeHeaderLabel("Status", 90),
            makeHeaderLabel("Total Bids", 80)
        };
        for (Label h : headers) tableHeader.getChildren().add(h);

        // Scrollable list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox logList = new VBox(8);

        List<Product> products;
        if (user.getRole() == UserRole.SELLER) {
            products = MainApp.productService.getProductsBySeller(user.getUserId());
        } else {
            // For buyers: show all products they bid on
            products = MainApp.productService.getAllProducts();
        }

        if (products.isEmpty()) {
            Label emptyLabel = new Label("No auction data available yet.");
            emptyLabel.setTextFill(Color.web("#aaaaaa"));
            emptyLabel.setFont(Font.font("System", 15));
            VBox emptyBox = new VBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            logList.getChildren().add(emptyBox);
        } else {
            for (Product product : products) {
                List<Bid> bids = MainApp.biddingService.getBidsForProduct(product.getProductId());

                // For buyers: only show products they bid on
                if (user.getRole() == UserRole.BUYER) {
                    boolean userBid = bids.stream()
                        .anyMatch(b -> b.getBidderId().equals(user.getUserId()));
                    if (!userBid) continue;
                }

                logList.getChildren().add(buildLogRow(product, bids, user));

                // Show expanded bid details for closed auctions
                if (product.getStatus() == AuctionStatus.CLOSED && !bids.isEmpty()) {
                    logList.getChildren().add(buildWinnerBanner(product));
                }
            }

            if (logList.getChildren().isEmpty()) {
                Label emptyLabel = new Label("You haven't placed any bids yet.");
                emptyLabel.setTextFill(Color.web("#aaaaaa"));
                emptyLabel.setFont(Font.font("System", 15));
                VBox emptyBox = new VBox(emptyLabel);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(60));
                logList.getChildren().add(emptyBox);
            }
        }

        scrollPane.setContent(logList);
        content.getChildren().addAll(titleLabel, subtitleLabel, statsBar, tableHeader, scrollPane);

        ScrollPane pageScroll = new ScrollPane(content);
        pageScroll.setFitToWidth(true);
        pageScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        pageScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pageScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(pageScroll, Priority.ALWAYS);

        root.getChildren().addAll(navBar, pageScroll);
    }

    private HBox buildLogRow(Product product, List<Bid> bids, User user) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 16, 12, 16));
        boolean isOpen = product.getStatus() == AuctionStatus.OPEN;
        row.setStyle(
            "-fx-background-color: rgba(255,255,255," + (isOpen ? "0.06" : "0.03") + ");" +
            "-fx-background-radius: 10px;"
        );

        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setMinWidth(180);
        nameLabel.setMaxWidth(180);
        nameLabel.setWrapText(false);

        // Base price
        Label basePriceLabel = new Label("₹" + String.format("%.2f", product.getBasePrice()));
        basePriceLabel.setTextFill(Color.web("#888899"));
        basePriceLabel.setMinWidth(110);

        // Highest bid
        Label highBidLabel = new Label("₹" + String.format("%.2f", product.getHighestBid()));
        highBidLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        highBidLabel.setTextFill(Color.web("#4ecca3"));
        highBidLabel.setMinWidth(110);

        // Winner
        String winnerText = product.getStatus() == AuctionStatus.CLOSED
            ? (product.getHighestBidder().equals("None") ? "No bids" : "🏆 " + product.getHighestBidder())
            : "—";
        Label winnerLabel = new Label(winnerText);
        winnerLabel.setTextFill(Color.web("#f7b731"));
        winnerLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        winnerLabel.setMinWidth(130);

        // Status badge
        Label statusLabel = new Label(isOpen ? "OPEN" : "CLOSED");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        statusLabel.setTextFill(isOpen ? Color.web("#4ecca3") : Color.web("#ff6b6b"));
        statusLabel.setStyle(
            "-fx-background-color: " + (isOpen ? "rgba(78,204,163,0.15)" : "rgba(255,107,107,0.15)") + ";" +
            "-fx-background-radius: 4px; -fx-padding: 3 8 3 8;"
        );
        HBox statusBox = new HBox(statusLabel);
        statusBox.setMinWidth(90);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        // Bid count
        Label bidCountLabel = new Label(String.valueOf(bids.size()));
        bidCountLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        bidCountLabel.setTextFill(Color.web("#aaaaaa"));
        bidCountLabel.setMinWidth(80);

        row.getChildren().addAll(nameLabel, basePriceLabel, highBidLabel,
                                 winnerLabel, statusBox, bidCountLabel);
        return row;
    }

    /**
     * Builds a winner announcement banner for closed auctions.
     */
    private HBox buildWinnerBanner(Product product) {
        HBox banner = new HBox(10);
        banner.setPadding(new Insets(10, 20, 10, 40));
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.setStyle(
            "-fx-background-color: rgba(247,183,49,0.1);" +
            "-fx-background-radius: 8px;"
        );

        if (product.getHighestBidder().equals("None")) {
            Label noWinner = new Label("⚠  No bids were placed — auction ended with no winner.");
            noWinner.setTextFill(Color.web("#888899"));
            noWinner.setFont(Font.font("System", 12));
            banner.getChildren().add(noWinner);
        } else {
            Label trophy = new Label("🏆");
            trophy.setFont(Font.font("System", 16));

            Label winnerText = new Label(
                "Winner: " + product.getHighestBidder() +
                "  |  Winning Bid: ₹" + String.format("%.2f", product.getHighestBid())
            );
            winnerText.setTextFill(Color.web("#f7b731"));
            winnerText.setFont(Font.font("System", FontWeight.BOLD, 13));

            banner.getChildren().addAll(trophy, winnerText);
        }
        return banner;
    }

    private HBox buildSummaryStats(User user) {
        HBox row = new HBox(16);

        List<Product> allProducts = user.getRole() == UserRole.SELLER
            ? MainApp.productService.getProductsBySeller(user.getUserId())
            : MainApp.productService.getAllProducts();

        long openCount = allProducts.stream()
            .filter(p -> p.getStatus() == AuctionStatus.OPEN).count();
        long closedCount = allProducts.stream()
            .filter(p -> p.getStatus() == AuctionStatus.CLOSED).count();

        int totalBids;
        if (user.getRole() == UserRole.BUYER) {
            totalBids = MainApp.biddingService.getBidsByUser(user.getUserId()).size();
        } else {
            totalBids = (int) allProducts.stream()
                .mapToLong(p -> MainApp.biddingService.getBidsForProduct(p.getProductId()).size())
                .sum();
        }

        row.getChildren().addAll(
            buildStatChip("Total Auctions", String.valueOf(allProducts.size()), "#e94560"),
            buildStatChip("Open", String.valueOf(openCount), "#4ecca3"),
            buildStatChip("Closed", String.valueOf(closedCount), "#ff6b6b"),
            buildStatChip(user.getRole() == UserRole.BUYER ? "My Bids" : "Total Bids",
                          String.valueOf(totalBids), "#f7b731")
        );
        return row;
    }

    private VBox buildStatChip(String label, String value, String color) {
        VBox chip = new VBox(4);
        chip.setPadding(new Insets(12, 20, 12, 20));
        chip.setAlignment(Pos.CENTER);
        chip.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10px;");

        Label valLabel = new Label(value);
        valLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        valLabel.setTextFill(Color.web(color));

        Label txtLabel = new Label(label);
        txtLabel.setFont(Font.font("System", 11));
        txtLabel.setTextFill(Color.web("#aaaaaa"));

        chip.getChildren().addAll(valLabel, txtLabel);
        return chip;
    }

    private Label makeHeaderLabel(String text, double minWidth) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web("#cccccc"));
        lbl.setMinWidth(minWidth);
        return lbl;
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
