package com.bidding.ui;

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
import javafx.scene.text.TextAlignment;

/**
 * The first screen the user sees when launching the app.
 * Provides navigation to Login or Register.
 */
public class WelcomeScreen {

    private VBox root;

    public WelcomeScreen() {
        buildUI();
    }

    private void buildUI() {
        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(60));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        // App Icon / Logo area
        Label iconLabel = new Label("🔨");
        iconLabel.setFont(Font.font("System", 72));

        // Title
        Label titleLabel = new Label("Online Bidding Platform");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        // Subtitle
        Label subtitleLabel = new Label("Buy · Sell · Bid · Win");
        subtitleLabel.setFont(Font.font("System", 16));
        subtitleLabel.setTextFill(Color.web("#e94560"));

        // Separator line
        Region divider = new Region();
        divider.setPrefHeight(2);
        divider.setPrefWidth(300);
        divider.setStyle("-fx-background-color: #e94560;");

        // Description
        Label descLabel = new Label("Welcome to the ultimate auction experience.\nRegister as a Buyer or Seller and start today!");
        descLabel.setFont(Font.font("System", 14));
        descLabel.setTextFill(Color.web("#aaaaaa"));
        descLabel.setTextAlignment(TextAlignment.CENTER);
        descLabel.setWrapText(true);

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginBtn = createStyledButton("🔑  Login", "#e94560", "#c73652");
        Button registerBtn = createStyledButton("📝  Register", "#0f3460", "#1a4a80");
        registerBtn.setStyle(registerBtn.getStyle() + "-fx-border-color: #e94560; -fx-border-width: 2px; -fx-border-radius: 8px;");

        loginBtn.setOnAction(e -> MainApp.showLoginScreen());
        registerBtn.setOnAction(e -> MainApp.showRegisterScreen());

        buttonBox.getChildren().addAll(loginBtn, registerBtn);

        // Footer
        Label footerLabel = new Label("© 2025 BidPlatform | Academic Project");
        footerLabel.setFont(Font.font("System", 11));
        footerLabel.setTextFill(Color.web("#555577"));

        content.getChildren().addAll(
            iconLabel,
            titleLabel,
            subtitleLabel,
            divider,
            descLabel,
            buttonBox,
            footerLabel
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        root = new VBox(scrollPane);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    /**
     * Creates a consistently styled button.
     */
    private Button createStyledButton(String text, String bgColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setPrefHeight(45);
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        btn.setTextFill(Color.WHITE);
        btn.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        // Hover effects
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + hoverColor + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        return btn;
    }

    public VBox getRoot() {
        return root;
    }
}
