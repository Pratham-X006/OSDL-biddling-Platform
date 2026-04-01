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
 *
 * Color scheme:
 *   Primary   Deep Blue  #1E3A8A
 *   Secondary Green      #10B981
 *   Accent    Red        #EF4444
 *   CTA       Amber      #F59E0B
 *   Background Light Gray #F3F4F6
 *   Text      Dark       #111827
 */
public class LoginScreen {

    // ── Design tokens ────────────────────────────────────────────────────────
    private static final String C_PRIMARY    = "#1E3A8A";   // Deep Blue
    private static final String C_SECONDARY  = "#10B981";   // Green
    private static final String C_ACCENT     = "#EF4444";   // Red  (alerts / errors)
    private static final String C_CTA        = "#F59E0B";   // Amber (primary action)
    private static final String C_CTA_HOVER  = "#D97706";   // Amber darker
    private static final String C_BG         = "#F3F4F6";   // Light Gray
    private static final String C_TEXT       = "#111827";   // Dark
    private static final String C_TEXT_MUTED = "#6B7280";   // Gray-500
    private static final String C_CARD_BG    = "#FFFFFF";
    private static final String C_BORDER     = "#D1D5DB";   // Gray-300
    private static final String C_INPUT_FOCUS = "#1E3A8A";  // Primary on focus

    private VBox root;

    public LoginScreen() {
        buildUI();
    }

    private void buildUI() {
        root = new VBox(0);
        // Light gray page background
        root.setStyle("-fx-background-color: " + C_BG + ";");

        // ── Top Nav Bar ──
        HBox navBar = buildNavBar();

        // ── Center Form Card ──
        VBox card = new VBox(16);
        card.setMaxWidth(440);
        card.setPadding(new Insets(40, 44, 40, 44));
        card.setStyle(
            "-fx-background-color: " + C_CARD_BG + ";" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 12px;" +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 16, 0, 0, 4);"
        );
        card.setAlignment(Pos.CENTER_LEFT);

        // ── Card header accent bar ──
        Region accentBar = new Region();
        accentBar.setPrefHeight(4);
        accentBar.setPrefWidth(440);
        accentBar.setStyle(
            "-fx-background-color: linear-gradient(to right, " + C_PRIMARY + ", " + C_SECONDARY + ");" +
            "-fx-background-radius: 12px 12px 0 0;"
        );
        accentBar.setTranslateY(-40); // pulled to top of card padding
        // We'll add it outside via a wrapper instead

        // Title block
        Label titleLabel = new Label("Welcome back");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web(C_TEXT));

        Label subtitleLabel = new Label("Sign in to your BidPlatform account");
        subtitleLabel.setFont(Font.font("System", 13));
        subtitleLabel.setTextFill(Color.web(C_TEXT_MUTED));

        // ── Username ──
        Label usernameLabel = makeFieldLabel("Username");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        styleTextField(usernameField);

        // ── Password ──
        Label passwordLabel = makeFieldLabel("Password");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        styleTextField(passwordField);

        // ── Error label ──
        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.web(C_ACCENT));
        errorLabel.setFont(Font.font("System", 12));
        errorLabel.setWrapText(true);
        errorLabel.setMinHeight(16);

        // ── CTA: Login button ──
        Button loginBtn = new Button("Sign in");
        loginBtn.setPrefWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(44);
        loginBtn.setFont(Font.font("System", FontWeight.BOLD, 14));
        loginBtn.setTextFill(Color.web(C_TEXT));
        styleActionButton(loginBtn, C_CTA, C_CTA_HOVER, C_TEXT);

        // ── Divider ──
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + C_BORDER + ";");

        // ── Register row ──
        HBox registerRow = new HBox(6);
        registerRow.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setTextFill(Color.web(C_TEXT_MUTED));
        noAccountLabel.setFont(Font.font("System", 13));

        Button registerLink = new Button("Create one");
        registerLink.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + C_PRIMARY + ";" +
            "-fx-cursor: hand;" +
            "-fx-underline: true;" +
            "-fx-padding: 0;"
        );
        registerLink.setFont(Font.font("System", FontWeight.BOLD, 13));
        registerRow.getChildren().addAll(noAccountLabel, registerLink);

        // ── Action handlers ──
        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("⚠  Please fill in all fields.");
                return;
            }

            User user = MainApp.userService.login(username, password);
            if (user == null) {
                errorLabel.setText("✕  Invalid username or password.");
                usernameField.setStyle(usernameField.getStyle() +
                    "-fx-border-color: " + C_ACCENT + ";");
                passwordField.setStyle(passwordField.getStyle() +
                    "-fx-border-color: " + C_ACCENT + ";");
            } else {
                AppContext.setCurrentUser(user);
                MainApp.showDashboard();
            }
        });

        // Clear error on typing
        usernameField.setOnKeyTyped(e -> {
            errorLabel.setText("");
            styleTextField(usernameField);
        });
        passwordField.setOnKeyTyped(e -> {
            errorLabel.setText("");
            styleTextField(passwordField);
        });

        // Enter key submits
        passwordField.setOnAction(e -> loginBtn.fire());

        registerLink.setOnAction(e -> MainApp.showRegisterScreen());

        card.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            sep,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            errorLabel,
            loginBtn,
            registerRow
        );

        // ── Card wrapper with top accent bar ──
        VBox cardWrapper = new VBox(0);
        cardWrapper.setMaxWidth(440);

        Region topBar = new Region();
        topBar.setPrefHeight(5);
        topBar.setPrefWidth(440);
        topBar.setStyle(
            "-fx-background-color: linear-gradient(to right, " + C_PRIMARY + ", " + C_SECONDARY + ");" +
            "-fx-background-radius: 12px 12px 0 0;"
        );

        // Adjust card to not repeat top radius since accent bar covers it
        card.setStyle(
            "-fx-background-color: " + C_CARD_BG + ";" +
            "-fx-background-radius: 0 0 12px 12px;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 0 0 12px 12px;" +
            "-fx-border-width: 0 1px 1px 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.09), 20, 0, 0, 6);"
        );

        cardWrapper.getChildren().addAll(topBar, card);

        // ── Center wrapper ──
        VBox centerWrapper = new VBox(cardWrapper);
        centerWrapper.setAlignment(Pos.CENTER);
        centerWrapper.setPadding(new Insets(48, 24, 48, 24));

        ScrollPane scrollPane = new ScrollPane(centerWrapper);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + C_BG + "; -fx-background-color: " + C_BG + ";");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(navBar, scrollPane);
    }

    private HBox buildNavBar() {
        HBox nav = new HBox();
        nav.setPadding(new Insets(0, 24, 0, 24));
        nav.setPrefHeight(56);
        nav.setStyle("-fx-background-color: " + C_PRIMARY + ";");
        nav.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: rgba(255,255,255,0.75);" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 12 6 0;"
        );
        backBtn.setFont(Font.font("System", 13));
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 12 6 0;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: rgba(255,255,255,0.75);" +
            "-fx-cursor: hand;" +
            "-fx-padding: 6 12 6 0;"
        ));
        backBtn.setOnAction(e -> MainApp.showWelcomeScreen());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label appName = new Label("BidPlatform");
        appName.setFont(Font.font("System", FontWeight.BOLD, 17));
        appName.setTextFill(Color.WHITE);

        // Green dot indicator — decorative brand mark
        Region dot = new Region();
        dot.setPrefSize(8, 8);
        dot.setStyle(
            "-fx-background-color: " + C_SECONDARY + ";" +
            "-fx-background-radius: 50%;"
        );

        HBox brand = new HBox(7, dot, appName);
        brand.setAlignment(Pos.CENTER);

        nav.getChildren().addAll(backBtn, spacer, brand);
        return nav;
    }

    private Label makeFieldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web(C_TEXT));
        lbl.setFont(Font.font("System", FontWeight.BOLD, 13));
        VBox.setMargin(lbl, new Insets(4, 0, 0, 0));
        return lbl;
    }

    private void styleTextField(TextField field) {
        field.setPrefWidth(Double.MAX_VALUE);
        field.setPrefHeight(42);
        field.setStyle(
            "-fx-background-color: " + C_BG + ";" +
            "-fx-text-fill: " + C_TEXT + ";" +
            "-fx-caret-color: " + C_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + C_TEXT_MUTED + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-border-color: " + C_BORDER + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-border-width: 1.5px;" +
            "-fx-padding: 0 12 0 12;" +
            "-fx-font-size: 14px;"
        );

        // Focus styling via hover approximation (JavaFX CSS focus works differently)
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                field.setStyle(
                    "-fx-background-color: " + C_CARD_BG + ";" +
                    "-fx-text-fill: " + C_TEXT + ";" +
                    "-fx-caret-color: " + C_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + C_TEXT_MUTED + ";" +
                    "-fx-background-radius: 8px;" +
                    "-fx-border-color: " + C_INPUT_FOCUS + ";" +
                    "-fx-border-radius: 8px;" +
                    "-fx-border-width: 2px;" +
                    "-fx-padding: 0 12 0 12;" +
                    "-fx-font-size: 14px;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: " + C_BG + ";" +
                    "-fx-text-fill: " + C_TEXT + ";" +
                    "-fx-caret-color: " + C_PRIMARY + ";" +
                    "-fx-prompt-text-fill: " + C_TEXT_MUTED + ";" +
                    "-fx-background-radius: 8px;" +
                    "-fx-border-color: " + C_BORDER + ";" +
                    "-fx-border-radius: 8px;" +
                    "-fx-border-width: 1.5px;" +
                    "-fx-padding: 0 12 0 12;" +
                    "-fx-font-size: 14px;"
                );
            }
        });
    }

    private void styleActionButton(Button btn, String bg, String bgHover, String textColor) {
        String base =
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;";
        String hover =
            "-fx-background-color: " + bgHover + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-border-width: 0;";

        btn.setStyle(base);
        btn.setTextFill(Color.web(textColor));
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnMousePressed(e -> btn.setStyle(hover + "-fx-scale-y: 0.97; -fx-scale-x: 0.99;"));
        btn.setOnMouseReleased(e -> btn.setStyle(hover));
    }

    public VBox getRoot() { return root; }
}
