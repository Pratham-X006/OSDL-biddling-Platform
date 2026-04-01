package com.bidding.ui;

import java.util.List;

import com.bidding.enums.AuctionStatus;
import com.bidding.model.Bid;
import com.bidding.model.Product;
import com.bidding.model.User;
import com.bidding.service.AppContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Screen for buyers to place a bid on a specific product.
 * Uses async BidTask (multithreading) from BiddingService.
 */
public class PlaceBidScreen {

    private VBox root;
    private final Integer productId;

    public PlaceBidScreen(Integer productId) {
        this.productId = productId;
        buildUI();
    }

    private void buildUI() {
        User user = AppContext.getCurrentUser();
        Product product = MainApp.productService.findById(productId);

        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        HBox navBar = buildNavBar(user);

        VBox content = new VBox(24);
        content.setPadding(new Insets(30));
        VBox.setVgrow(content, Priority.ALWAYS);

        if (product == null) {
            Label errLabel = new Label("❌ Product not found.");
            errLabel.setTextFill(Color.web("#ff6b6b"));
            errLabel.setFont(Font.font("System", 18));
            content.getChildren().add(errLabel);
            root.getChildren().addAll(navBar, content);
            return;
        }

        // ── Product Info Card ──
        VBox productCard = new VBox(10);
        productCard.setPadding(new Insets(22));
        productCard.setMaxWidth(700);
        productCard.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 14px;"
        );

        Label productTitle = new Label("🏷  " + product.getName());
        productTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        productTitle.setTextFill(Color.WHITE);

        Label productDesc = new Label(
            product.getDescription().isEmpty() ? "No description provided." : product.getDescription()
        );
        productDesc.setTextFill(Color.web("#aaaaaa"));
        productDesc.setWrapText(true);

        HBox priceInfo = new HBox(30);
        priceInfo.setAlignment(Pos.CENTER_LEFT);

        VBox basePriceBox = buildInfoBox("Starting Price", "₹" + String.format("%.2f", product.getBasePrice()), "#888899");
        VBox highBidBox = buildInfoBox("Current Highest Bid", "₹" + String.format("%.2f", product.getHighestBid()), "#4ecca3");
        VBox bidderBox = buildInfoBox("Current Leader", product.getHighestBidder(), "#f7b731");

        boolean isOpen = product.getStatus() == AuctionStatus.OPEN;
        VBox statusBox = buildInfoBox("Status", isOpen ? "OPEN" : "CLOSED", isOpen ? "#4ecca3" : "#ff6b6b");

        priceInfo.getChildren().addAll(basePriceBox, highBidBox, bidderBox, statusBox);
        productCard.getChildren().addAll(productTitle, productDesc, priceInfo);

        // ── Bid Form ──
        VBox bidCard = new VBox(14);
        bidCard.setPadding(new Insets(22));
        bidCard.setMaxWidth(700);
        bidCard.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 14px;"
        );

        Label bidTitle = new Label("💰  Place Your Bid");
        bidTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        bidTitle.setTextFill(Color.WHITE);

        Label minBidHint = new Label(
            "Your bid must be greater than ₹" + String.format("%.2f", product.getHighestBid())
        );
        minBidHint.setTextFill(Color.web("#aaaaaa"));
        minBidHint.setFont(Font.font("System", 13));

        HBox inputRow = new HBox(12);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        Label rupeeLabel = new Label("₹");
        rupeeLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        rupeeLabel.setTextFill(Color.web("#4ecca3"));

        TextField bidField = new TextField();
        bidField.setPromptText("Enter your bid amount");
        bidField.setPrefWidth(260);
        bidField.setPrefHeight(44);
        bidField.setFont(Font.font("System", FontWeight.BOLD, 16));
        bidField.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-caret-color: white;" +
            "-fx-prompt-text-fill: #777799;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #4ecca3;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 8 12 8 12;"
        );

        Button placeBidBtn = new Button("🔨  Place Bid");
        placeBidBtn.setPrefWidth(150);
        placeBidBtn.setPrefHeight(44);
        placeBidBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        placeBidBtn.setTextFill(Color.WHITE);
        placeBidBtn.setStyle(
            "-fx-background-color: #e94560; -fx-background-radius: 8px; -fx-cursor: hand;"
        );

        inputRow.getChildren().addAll(rupeeLabel, bidField, placeBidBtn);

        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("System", 13));
        messageLabel.setWrapText(true);

        // Disable bidding if auction is closed or user is the seller
        if (!isOpen) {
            bidField.setDisable(true);
            placeBidBtn.setDisable(true);
            messageLabel.setTextFill(Color.web("#ff6b6b"));
            messageLabel.setText("⛔ This auction is closed. No more bids accepted.");
        }

        // ── Bid Action ──
        placeBidBtn.setOnAction(e -> {
            String amountText = bidField.getText().trim();
            if (amountText.isEmpty()) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("⚠ Please enter a bid amount.");
                return;
            }

            Double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("❌ Please enter a valid number.");
                return;
            }

            // Disable button while processing
            placeBidBtn.setDisable(true);
            placeBidBtn.setText("Processing...");
            messageLabel.setTextFill(Color.web("#f7b731"));
            messageLabel.setText("⏳ Placing bid...");

            // Place bid asynchronously (uses BidTask thread)
            MainApp.biddingService.placeBidAsync(productId, user, amount, result -> {
                placeBidBtn.setDisable(false);
                placeBidBtn.setText("🔨  Place Bid");

                if (result == null) {
                    // Success — reload screen to show updated price
                    messageLabel.setTextFill(Color.web("#4ecca3"));
                    messageLabel.setText("✅ Bid placed successfully!");
                    javafx.animation.PauseTransition pause =
                        new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                    pause.setOnFinished(ev -> MainApp.showPlaceBidScreen(productId));
                    pause.play();
                } else {
                    messageLabel.setTextFill(Color.web("#ff6b6b"));
                    messageLabel.setText("❌ " + result);
                }
            });
        });

        bidCard.getChildren().addAll(bidTitle, minBidHint, inputRow, messageLabel);

        // ── Bid History ──
        VBox historyCard = buildBidHistoryCard(product);

        content.getChildren().addAll(productCard, bidCard, historyCard);
        root.getChildren().addAll(navBar, new ScrollPane(content) {{
            setFitToWidth(true);
            setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            VBox.setVgrow(this, Priority.ALWAYS);
        }});
    }

    private VBox buildBidHistoryCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22));
        card.setMaxWidth(700);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.05);" +
            "-fx-background-radius: 14px;"
        );

        Label histTitle = new Label("📜  Bid History");
        histTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        histTitle.setTextFill(Color.WHITE);

        List<Bid> bids = MainApp.biddingService.getBidsForProduct(product.getProductId());

        if (bids.isEmpty()) {
            Label noBids = new Label("No bids placed yet. Be the first!");
            noBids.setTextFill(Color.web("#777799"));
            card.getChildren().addAll(histTitle, noBids);
        } else {
            card.getChildren().add(histTitle);
            // Show most recent first
            for (int i = bids.size() - 1; i >= 0; i--) {
                Bid b = bids.get(i);
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(6, 0, 6, 0));
                row.setStyle("-fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 1 0;");

                Label rank = new Label("#" + (bids.size() - i));
                rank.setFont(Font.font("System", FontWeight.BOLD, 12));
                rank.setTextFill(Color.web("#555577"));
                rank.setMinWidth(30);

                Label bidder = new Label(b.getBidderUsername());
                bidder.setFont(Font.font("System", FontWeight.BOLD, 13));
                bidder.setTextFill(Color.WHITE);
                bidder.setMinWidth(120);

                Label amount = new Label("₹" + String.format("%.2f", b.getAmount()));
                amount.setFont(Font.font("System", FontWeight.BOLD, 14));
                amount.setTextFill(Color.web("#4ecca3"));
                amount.setMinWidth(100);

                Label time = new Label(b.getTimestamp());
                time.setFont(Font.font("System", 11));
                time.setTextFill(Color.web("#666688"));

                row.getChildren().addAll(rank, bidder, amount, time);
                card.getChildren().add(row);
            }
        }
        return card;
    }

    private VBox buildInfoBox(String label, String value, String valueColor) {
        VBox box = new VBox(4);
        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", 11));
        lbl.setTextFill(Color.web("#888899"));
        Label val = new Label(value);
        val.setFont(Font.font("System", FontWeight.BOLD, 15));
        val.setTextFill(Color.web(valueColor));
        box.getChildren().addAll(lbl, val);
        return box;
    }

    private HBox buildNavBar(User user) {
        HBox nav = new HBox();
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setStyle("-fx-background-color: rgba(0,0,0,0.35);");
        nav.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Auctions");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-cursor: hand;");
        backBtn.setFont(Font.font("System", 13));
        backBtn.setOnAction(e -> MainApp.showProductListScreen());

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
