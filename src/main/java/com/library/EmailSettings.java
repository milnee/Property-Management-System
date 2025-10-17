package com.library;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class EmailSettings {
    private static final String SETTINGS_FILE = "email_settings.properties";
    private String email;
    private String apiKey; // SendGrid API key

    public EmailSettings() {
        // Initialize with default settings
        this.email = "singhmillen2005@gmail.com";
        this.apiKey = "YOUR_SENDGRID_API_KEY";
        loadSettings();
        
        // If no settings file exists, save the default settings
        if (!new java.io.File(SETTINGS_FILE).exists()) {
            saveSettings();
        }
    }

    private void loadSettings() {
        try {
            java.util.Properties props = new java.util.Properties();
            java.io.File file = new java.io.File(SETTINGS_FILE);
            if (file.exists()) {
                try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
                    props.load(in);
                    this.email = props.getProperty("email", "");
                    this.password = props.getProperty("password", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSettings() {
        try {
            java.util.Properties props = new java.util.Properties();
            props.setProperty("email", email);
            props.setProperty("password", password);
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(SETTINGS_FILE)) {
                props.store(out, "Email Settings");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save email settings");
        }
    }

    public boolean hasValidSettings() {
        return email != null && !email.isEmpty() && password != null && !password.isEmpty();
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public boolean showSettingsDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Email Settings");
        dialog.setHeaderText("Configure Email Settings\n\nEnter your email and password to send notifications");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField emailField = new TextField();
        emailField.setPromptText("Gmail Address");
        if (email != null) emailField.setText(email);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("App Password");
        if (password != null) passwordField.setText(password);

        // Help link for App Password
        Hyperlink helpLink = new Hyperlink("How to get an App Password?");
        helpLink.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(
                    new java.net.URI("https://support.google.com/accounts/answer/185833")
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        grid.add(new Label("Gmail Address:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("App Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(helpLink, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable save button depending on whether fields are filled
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || passwordField.getText().trim().isEmpty());
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || emailField.getText().trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                this.email = emailField.getText().trim();
                this.password = passwordField.getText().trim();
                saveSettings();
                return true;
            }
            return false;
        });

        Optional<Boolean> result = dialog.showAndWait();
        return result.orElse(false);
    }
}
