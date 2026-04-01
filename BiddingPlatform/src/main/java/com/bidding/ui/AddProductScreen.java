package com.bidding.ui;

import com.bidding.model.User;
import com.bidding.service.AppContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Screen for sellers to add a new product/auction listing.
 */
public class AddProductScreen {

    private VBox root;

    public AddProductScreen() {
        buildUI();
    }

    private void buildUI() {
        User user = AppContext.getCurrentUser();

        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        // Nav Bar
        HBox navBar = buildNavBar(user);

        // Form Card
        VBox card = new VBox(16);
        card.setMaxWidth(480);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 16px;"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📦  List a New Product");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Fill in the details to start your auction");
        subtitleLabel.setTextFill(Color.web("#aaaaaa"));

        // Product Name
        Label nameLabel = makeLabel("Product Name *");
        TextField nameField = makeTextField("e.g. Sony WH-1000XM5 Headphones");

        // Description
        Label descLabel = makeLabel("Description");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe your product (condition, features, etc.)");
        descArea.setPrefRowCount(3);
        descArea.setWrapText(true);
        descArea.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-caret-color: white;" +
            "-fx-prompt-text-fill: #777799;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #444466;" +
            "-fx-border-radius: 8px;" +
            "-fx-control-inner-background: transparent;"
        );

        // Base Price
        Label priceLabel = makeLabel("Starting Bid Price (₹) *");
        TextField priceField = makeTextField("e.g. 500.00");

        // Message label
        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("System", 13));
        messageLabel.setWrapText(true);

        // Buttons
        HBox btnRow = new HBox(12);

        Button submitBtn = new Button("🚀  List Product");
        submitBtn.setPrefWidth(200);
        submitBtn.setPrefHeight(44);
        submitBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        submitBtn.setTextFill(Color.WHITE);
        styleBtn(submitBtn, "#e94560");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setPrefHeight(44);
        cancelBtn.setFont(Font.font("System", 13));
        cancelBtn.setTextFill(Color.web("#aaaaaa"));
        cancelBtn.setStyle(
            "-fx-background-color: rgba(255,255,255,0.08);" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> MainApp.showDashboard());

        btnRow.getChildren().addAll(submitBtn, cancelBtn);

        // ── Submit Handler ──
        submitBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String desc = descArea.getText().trim();
            String priceText = priceField.getText().trim();

            if (name.isEmpty() || priceText.isEmpty()) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("⚠ Product name and price are required.");
                return;
            }

            Double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("❌ Please enter a valid price (e.g. 500.00).");
                return;
            }

            String error = MainApp.productService.addProduct(name, desc, price, user.getUserId());
            if (error != null) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("❌ " + error);
            } else {
                messageLabel.setTextFill(Color.web("#4ecca3"));
                messageLabel.setText("✅ Product listed successfully! Returning to dashboard...");
                javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.2));
                pause.setOnFinished(ev -> MainApp.showDashboard());
                pause.play();
            }
        });

        card.getChildren().addAll(
            titleLabel, subtitleLabel,
            new Separator(),
            nameLabel, nameField,
            descLabel, descArea,
            priceLabel, priceField,
            messageLabel,
            btnRow
        );

        VBox centerWrapper = new VBox(card);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(30));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(navBar, scrollPane);
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

        Label appName = new Label("🔨 BidPlatform");
        appName.setFont(Font.font("System", FontWeight.BOLD, 16));
        appName.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white;" +
                           "-fx-background-radius: 6px; -fx-cursor: hand;");
        logoutBtn.setFont(Font.font("System", FontWeight.BOLD, 12));
        logoutBtn.setOnAction(e -> MainApp.logout());

        nav.getChildren().addAll(backBtn, spacer, appName,
            new Label("  "), logoutBtn);
        return nav;
    }

    private Label makeLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web("#cccccc"));
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        return lbl;
    }

    private TextField makeTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(400);
        field.setPrefHeight(40);
        field.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-text-fill: white;" +
            "-fx-caret-color: white;" +
            "-fx-prompt-text-fill: #777799;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: #444466;" +
            "-fx-border-radius: 8px;" +
            "-fx-padding: 8 12 8 12;"
        );
        return field;
    }

    private void styleBtn(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + color + ",-15%);" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        ));
    }

    public VBox getRoot() { return root; }
}
