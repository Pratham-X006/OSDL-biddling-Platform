package com.bidding.ui;

import com.bidding.model.User;
import com.bidding.service.AppContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Login screen — allows existing users to log into the platform.
 */
public class LoginScreen {

    private VBox root;

    public LoginScreen() {
        buildUI();
    }

    private void buildUI() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e, #0f3460);");

        // ── Top Nav Bar ──
        HBox navBar = buildNavBar();

        // ── Center Form Card ──
        VBox card = new VBox(18);
        card.setMaxWidth(420);
        card.setPadding(new Insets(40));
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 16px;"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // Title
        Label titleLabel = new Label("🔑  Welcome Back");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 26));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Sign in to your account");
        subtitleLabel.setFont(Font.font("System", 13));
        subtitleLabel.setTextFill(Color.web("#aaaaaa"));

        // Username field
        Label usernameLabel = new Label("Username");
        usernameLabel.setTextFill(Color.web("#cccccc"));
        usernameLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        styleTextField(usernameField);

        // Password field
        Label passwordLabel = new Label("Password");
        passwordLabel.setTextFill(Color.web("#cccccc"));
        passwordLabel.setFont(Font.font("System", FontWeight.BOLD, 13));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        styleTextField(passwordField);

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.web("#ff6b6b"));
        errorLabel.setFont(Font.font("System", 12));
        errorLabel.setWrapText(true);

        // Login button
        Button loginBtn = new Button("Login  →");
        loginBtn.setPrefWidth(340);
        loginBtn.setPrefHeight(44);
        loginBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        loginBtn.setTextFill(Color.WHITE);
        styleActionButton(loginBtn, "#e94560");

        // Register link
        HBox registerRow = new HBox(6);
        registerRow.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setTextFill(Color.web("#aaaaaa"));
        Button registerLink = new Button("Register here");
        registerLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #e94560; -fx-cursor: hand;");
        registerLink.setFont(Font.font("System", FontWeight.BOLD, 13));
        registerRow.getChildren().addAll(noAccountLabel, registerLink);

        // ── Action Handlers ──
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠ Please fill in all fields.");
                return;
            }

            User user = MainApp.userService.login(username, password);
            if (user == null) {
                errorLabel.setText("❌ Invalid username or password.");
            } else {
                AppContext.setCurrentUser(user);
                MainApp.showDashboard();
            }
        });

        // Allow Enter key to submit
        passwordField.setOnAction(e -> loginBtn.fire());

        registerLink.setOnAction(e -> MainApp.showRegisterScreen());

        card.getChildren().addAll(
            titleLabel, subtitleLabel,
            new Separator(),
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            errorLabel,
            loginBtn,
            registerRow
        );

        // Wrap card in centering container
        VBox centerWrapper = new VBox(card);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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

    private void styleTextField(TextField field) {
        field.setPrefWidth(340);
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
    }

    private void styleActionButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: derive(" + color + ", -15%);" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        ));
    }

    public VBox getRoot() { return root; }
}
