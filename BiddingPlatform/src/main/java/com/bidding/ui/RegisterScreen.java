package com.bidding.ui;

import com.bidding.enums.UserRole;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Register screen — allows new users to create an account as Buyer or Seller.
 */
public class RegisterScreen {

    private VBox root;

    public RegisterScreen() {
        buildUI();
    }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        // Nav Bar
        HBox navBar = buildNavBar();

        // Form Card
        VBox card = new VBox(16);
        card.setMaxWidth(440);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 16px;"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📝  Create Account");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Join the bidding community");
        subtitleLabel.setFont(Font.font("System", 13));
        subtitleLabel.setTextFill(Color.web("#aaaaaa"));

        // Username
        Label usernameLabel = makeFieldLabel("Username");
        TextField usernameField = makeTextField("Choose a username");

        // Password
        Label passwordLabel = makeFieldLabel("Password");
        PasswordField passwordField = makePasswordField("At least 4 characters");

        // Confirm Password
        Label confirmLabel = makeFieldLabel("Confirm Password");
        PasswordField confirmField = makePasswordField("Re-enter your password");

        // Role selection
        Label roleLabel = makeFieldLabel("I want to:");
        ToggleGroup roleGroup = new ToggleGroup();

        HBox roleBox = new HBox(16);
        roleBox.setAlignment(Pos.CENTER_LEFT);

        RadioButton buyerBtn = new RadioButton("🛒  Bid & Buy");
        buyerBtn.setToggleGroup(roleGroup);
        buyerBtn.setSelected(true);
        buyerBtn.setTextFill(Color.WHITE);
        buyerBtn.setFont(Font.font("System", 14));
        buyerBtn.setStyle("-fx-cursor: hand;");

        RadioButton sellerBtn = new RadioButton("🏷  Sell Items");
        sellerBtn.setToggleGroup(roleGroup);
        sellerBtn.setTextFill(Color.WHITE);
        sellerBtn.setFont(Font.font("System", 14));
        sellerBtn.setStyle("-fx-cursor: hand;");

        roleBox.getChildren().addAll(buyerBtn, sellerBtn);

        // Error / success label
        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("System", 12));
        messageLabel.setWrapText(true);

        // Register button
        Button registerBtn = new Button("Create Account  →");
        registerBtn.setPrefWidth(360);
        registerBtn.setPrefHeight(44);
        registerBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        registerBtn.setTextFill(Color.WHITE);
        styleActionButton(registerBtn, "#e94560");

        // Login link
        HBox loginRow = new HBox(6);
        loginRow.setAlignment(Pos.CENTER);
        Label hasAccountLabel = new Label("Already have an account?");
        hasAccountLabel.setTextFill(Color.web("#aaaaaa"));
        Button loginLink = new Button("Login here");
        loginLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #e94560; -fx-cursor: hand;");
        loginLink.setFont(Font.font("System", FontWeight.BOLD, 13));
        loginRow.getChildren().addAll(hasAccountLabel, loginLink);

        // ── Action Handlers ──
        registerBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirm = confirmField.getText();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("⚠ Please fill in all fields.");
                return;
            }

            if (!password.equals(confirm)) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("❌ Passwords do not match.");
                return;
            }

            UserRole selectedRole = (buyerBtn.isSelected()) ? UserRole.BUYER : UserRole.SELLER;
            String error = MainApp.userService.register(username, password, selectedRole);

            if (error != null) {
                messageLabel.setTextFill(Color.web("#ff6b6b"));
                messageLabel.setText("❌ " + error);
            } else {
                messageLabel.setTextFill(Color.web("#4ecca3"));
                messageLabel.setText("✅ Account created! Redirecting to login...");
                // Auto-navigate to login after a brief moment
                javafx.animation.PauseTransition pause =
                    new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.2));
                pause.setOnFinished(ev -> MainApp.showLoginScreen());
                pause.play();
            }
        });

        loginLink.setOnAction(e -> MainApp.showLoginScreen());

        card.getChildren().addAll(
            titleLabel, subtitleLabel,
            new Separator(),
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            confirmLabel, confirmField,
            roleLabel, roleBox,
            messageLabel,
            registerBtn,
            loginRow
        );

        VBox centerWrapper = new VBox(card);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(navBar, scrollPane);
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(14, 24, 14, 24));
        nav.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
        nav.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-cursor: hand;");
        backBtn.setFont(Font.font("System", 13));
        backBtn.setOnAction(e -> MainApp.showWelcomeScreen());

        Label appName = new Label("🔨 BidPlatform");
        appName.setFont(Font.font("System", FontWeight.BOLD, 16));
        appName.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        nav.getChildren().addAll(backBtn, spacer, appName);
        return nav;
    }

    private Label makeFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web("#cccccc"));
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        return lbl;
    }

    private TextField makeTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(360);
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

    private PasswordField makePasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefWidth(360);
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

    private void styleActionButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + color + ", -15%);" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8px; -fx-cursor: hand;"
        ));
    }

    public VBox getRoot() { return root; }
}
