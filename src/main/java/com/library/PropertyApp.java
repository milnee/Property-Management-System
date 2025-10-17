package com.library;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

public class PropertyApp extends Application {

    private Label statusLabel;
    private UserManager userManager;
    private User currentUser;
    private Tenant currentTenant;
    private static final ObservableList<String> VALID_STATUSES = 
        FXCollections.observableArrayList("Rented", "Vacant");
    private static final ObservableList<String> HOUSE_TYPES = 
        FXCollections.observableArrayList("Detached", "Semi-Detached", "Terraced", "Apartment", "Bungalow", "Cottage");
    private DatabaseManager dbManager;
    private ObservableList<Property> properties;

    // CSS Styles
    private static final String MAIN_BACKGROUND = "-fx-background-color: #f5f6fa;";
    private static final String HEADER_STYLE = """
        -fx-font-family: 'Segoe UI', Arial, sans-serif;
        -fx-font-size: 24px;
        -fx-font-weight: bold;
        -fx-text-fill: #2c3e50;
    """;
    private static final String INPUT_FIELD_STYLE = """
        -fx-background-color: white;
        -fx-border-color: #e1e1e1;
        -fx-border-radius: 4px;
        -fx-padding: 8px 12px;
        -fx-font-size: 14px;
        -fx-prompt-text-fill: #95a5a6;
    """;
    private static final String BUTTON_STYLE = """
        -fx-background-color: #3498db;
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-padding: 10 20;
        -fx-cursor: hand;
        -fx-background-radius: 4px;
        -fx-font-weight: bold;
    """;
    private static final String BUTTON_HOVER_STYLE = """
        -fx-background-color: #2980b9;
    """;
    private static final String TABLE_STYLE = """
        -fx-background-color: white;
        -fx-border-color: #e1e1e1;
        -fx-border-radius: 4px;
        -fx-padding: 0;
    """;
    private static final String LOGIN_BOX_STYLE = """
        -fx-background-color: white;
        -fx-background-radius: 8px;
        -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 2);
        -fx-padding: 40px;
        -fx-alignment: center;
        -fx-spacing: 15px;
        -fx-min-width: 400px;
    """;

    public static class Property {
        private String propertyId;
        private String ownerName;
        private String address;
        private double monthlyRent;
        private double monthlyMortgage;
        private String status;
        private int bedrooms;
        private int livingRooms;
        private int kitchens;
        private String houseType;
        private int bathrooms;
        private String description;

        public Property(String propertyId, String ownerName, String address,
                        double monthlyRent, double monthlyMortgage, String status) {
            this.propertyId = propertyId;
            this.ownerName = ownerName;
            this.address = address;
            this.monthlyRent = monthlyRent;
            this.monthlyMortgage = monthlyMortgage;
            this.status = status;
            this.bedrooms = 0;
            this.livingRooms = 0;
            this.kitchens = 0;
            this.houseType = "";
            this.bathrooms = 0;
            this.description = "";
        }

        // Basic getters and setters
        public String getPropertyId() { return propertyId; }
        public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
        
        public String getOwnerName() { return ownerName; }
        public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public double getMonthlyRent() { return monthlyRent; }
        public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }
        
        public double getMonthlyMortgage() { return monthlyMortgage; }
        public void setMonthlyMortgage(double monthlyMortgage) { this.monthlyMortgage = monthlyMortgage; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getMonthlyProfit() { 
            return status.equals("Vacant") ? 0 : monthlyRent - monthlyMortgage;
        }

        // Additional property details getters and setters
        public int getBedrooms() { return bedrooms; }
        public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

        public int getLivingRooms() { return livingRooms; }
        public void setLivingRooms(int livingRooms) { this.livingRooms = livingRooms; }

        public int getKitchens() { return kitchens; }
        public void setKitchens(int kitchens) { this.kitchens = kitchens; }

        public String getHouseType() { return houseType; }
        public void setHouseType(String houseType) { this.houseType = houseType; }

        public int getBathrooms() { return bathrooms; }
        public void setBathrooms(int bathrooms) { this.bathrooms = bathrooms; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    @Override
    public void init() {
        userManager = UserManager.getInstance();
        dbManager = DatabaseManager.getInstance();
        properties = FXCollections.observableArrayList();
    }

    @Override
    public void stop() {
        if (dbManager != null) {
            dbManager.closeConnection();
        }
        if (userManager != null) {
            userManager.closeConnection();
        }
    }

    private void savePropertyToDatabase(Property property) {
        dbManager.saveProperty(property);
        showSavedStatus();
    }

    private void showPropertyDetails(Property property) {
        Dialog<Property> dialog = new Dialog<>();
        dialog.setTitle("Property Details");
        dialog.setHeaderText("View/Edit Property Details");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save");
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid for property details
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        // Create tabs for different sections
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Prevent tab closing
        
        // Property Details Tab
        Tab detailsTab = new Tab("Property Details");

        // Rent Payments Tab
        Tab rentTab = new Tab("Rent Payments");
        VBox rentBox = new VBox(10);
        rentBox.setPadding(new Insets(20, 20, 10, 10));
        
        // Date picker for new payment
        DatePicker paymentDatePicker = new DatePicker();
        paymentDatePicker.setPromptText("Select payment date");
        
        Button addPaymentButton = new Button("Add Payment Date");
        addPaymentButton.setStyle(BUTTON_STYLE);
        
        // Payment dates list
        ListView<String> paymentsList = new ListView<>();
        List<LocalDate> paymentDates = dbManager.getRentPaymentDates(property.getPropertyId());
        paymentDates.forEach(date -> 
            paymentsList.getItems().add(date.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))));
        
        addPaymentButton.setOnAction(e -> {
            LocalDate selectedDate = paymentDatePicker.getValue();
            if (selectedDate != null) {
                dbManager.saveRentPaymentDate(property.getPropertyId(), selectedDate, 
                    property.getMonthlyRent(), "PAID");
                paymentsList.getItems().clear();
                List<LocalDate> updatedDates = dbManager.getRentPaymentDates(property.getPropertyId());
                updatedDates.forEach(date -> 
                    paymentsList.getItems().add(date.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))));
                paymentDatePicker.setValue(null);
                showSavedStatus("Rent payment date saved successfully");
            } else {
                showAlert("Error", "Please select a payment date");
            }
        });
        
        rentBox.getChildren().addAll(
            new Label("Add New Payment Date:"),
            paymentDatePicker,
            addPaymentButton,
            new Label("Payment History:"),
            paymentsList
        );
        rentTab.setContent(rentBox);

        // Expenses Tab
        Tab expensesTab = new Tab("Expenses");
        VBox expensesBox = new VBox(10);
        expensesBox.setPadding(new Insets(20, 20, 10, 10));
        
        // Expense input fields
        TextField expenseDescField = new TextField();
        expenseDescField.setPromptText("Description");
        
        TextField expenseAmountField = new TextField();
        expenseAmountField.setPromptText("Amount");
        
        ComboBox<String> expenseCategoryBox = new ComboBox<>();
        expenseCategoryBox.getItems().addAll(
            "REPAIR", "MAINTENANCE", "UTILITIES", "INSURANCE", "TAX", "OTHER"
        );
        expenseCategoryBox.setPromptText("Category");
        
        DatePicker expenseDatePicker = new DatePicker();
        expenseDatePicker.setPromptText("Date");
        
        Button addExpenseButton = new Button("Add Expense");
        addExpenseButton.setStyle(BUTTON_STYLE);
        
        // Expenses list
        TableView<PropertyExpense> expensesTable = new TableView<>();
        
        TableColumn<PropertyExpense, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDate()));
        
        TableColumn<PropertyExpense, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDescription()));
        
        TableColumn<PropertyExpense, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getCategory()));
        
        TableColumn<PropertyExpense, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedAmount()));
        
        expensesTable.getColumns().addAll(dateCol, descCol, categoryCol, amountCol);
        
        // Load existing expenses
        List<PropertyExpense> expenses = dbManager.getPropertyExpenses(property.getPropertyId());
        expensesTable.setItems(FXCollections.observableArrayList(expenses));
        
        // Total expenses label
        Label totalExpensesLabel = new Label(String.format("Total Expenses: £%.2f", 
            dbManager.getTotalExpenses(property.getPropertyId())));
        totalExpensesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        addExpenseButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(expenseAmountField.getText());
                String description = expenseDescField.getText();
                String category = expenseCategoryBox.getValue();
                LocalDate date = expenseDatePicker.getValue();
                
                if (description.isEmpty() || category == null || date == null) {
                    showAlert("Error", "Please fill in all fields");
                    return;
                }
                
                PropertyExpense expense = new PropertyExpense(
                    0, property.getPropertyId(), description, amount, date, category
                );
                dbManager.saveExpense(expense);
                
                // Refresh expense list from database
                List<PropertyExpense> updatedExpenses = dbManager.getPropertyExpenses(property.getPropertyId());
                expensesTable.setItems(FXCollections.observableArrayList(updatedExpenses));
                
                // Update total expenses
                totalExpensesLabel.setText(String.format("Total Expenses: £%.2f", 
                    dbManager.getTotalExpenses(property.getPropertyId())));
                
                showSavedStatus("Expense saved successfully");
                
                // Clear inputs
                expenseDescField.clear();
                expenseAmountField.clear();
                expenseCategoryBox.setValue(null);
                expenseDatePicker.setValue(null);
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid amount");
            }
        });
        
        expensesBox.getChildren().addAll(
            new Label("Add New Expense:"),
            expenseDescField,
            expenseAmountField,
            expenseCategoryBox,
            expenseDatePicker,
            addExpenseButton,
            new Label("Expense History:"),
            expensesTable,
            totalExpensesLabel
        );
        expensesTab.setContent(expensesBox);

        // Communication Tab
        Tab communicationTab = new Tab("Communication");
        VBox communicationBox = new VBox(10);
        communicationBox.setPadding(new Insets(20, 20, 10, 10));

        // Tenant Email Management Section
        VBox tenantEmailBox = new VBox(5);
        tenantEmailBox.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-border-color: #e1e1e1; -fx-border-radius: 4px;");
        
        Label emailTitle = new Label("Tenant Email Management");
        emailTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Email input section
        GridPane emailGrid = new GridPane();
        emailGrid.setHgap(10);
        emailGrid.setVgap(10);
        emailGrid.setPadding(new Insets(10, 0, 10, 0));

        TextField tenantNameField = new TextField();
        tenantNameField.setPromptText("Tenant Name");
        
        TextField tenantEmailField = new TextField();
        tenantEmailField.setPromptText("Tenant Email");

        emailGrid.add(new Label("Tenant Name:"), 0, 0);
        emailGrid.add(tenantNameField, 1, 0);
        emailGrid.add(new Label("Email:"), 0, 1);
        emailGrid.add(tenantEmailField, 1, 1);

        // Tenant email list
        ListView<String> emailList = new ListView<>();
        emailList.setPrefHeight(150);

        // Load existing tenant emails
        List<Tenant> tenants = dbManager.getTenantsForProperty(property.getPropertyId());
        for (Tenant tenant : tenants) {
            if (tenant.getEmail() != null && !tenant.getEmail().isEmpty()) {
                emailList.getItems().add(String.format("%s (%s)", tenant.getName(), tenant.getEmail()));
            }
        }

        // Add and Remove buttons
        HBox emailButtonBox = new HBox(10);
        emailButtonBox.setAlignment(Pos.CENTER_LEFT);

        Button addEmailButton = new Button("Add Email");
        addEmailButton.setStyle(BUTTON_STYLE);

        Button removeEmailButton = new Button("Remove Email");
        removeEmailButton.setStyle(BUTTON_STYLE.replace("#3498db", "#e74c3c")); // Red button

            // Configure Email button
            Button configureEmailButton = new Button("Configure Email Settings");
            configureEmailButton.setStyle(BUTTON_STYLE);
            
            addEmailButton.setOnAction(e -> {
            String name = tenantNameField.getText().trim();
            String email = tenantEmailField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty()) {
                showAlert("Error", "Please enter both name and email");
                return;
            }

            // Create or update tenant
            Tenant tenant = tenants.isEmpty() ? new Tenant(0, name, email, "", property.getPropertyId(),
                                                         LocalDate.now(), LocalDate.now().plusYears(1), 0) 
                                            : tenants.get(0);
            tenant.setName(name);
            tenant.setEmail(email);
            tenant.setEmailNotifications(true);
            
            dbManager.saveTenant(tenant);
            emailList.getItems().add(String.format("%s (%s)", name, email));
            
            // Clear fields
            tenantNameField.clear();
            tenantEmailField.clear();
            
            showSavedStatus("Tenant email added successfully");
        });

        removeEmailButton.setOnAction(e -> {
            String selected = emailList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                emailList.getItems().remove(selected);
                // Extract name from "Name (email)" format
                String name = selected.substring(0, selected.indexOf(" ("));
                
                // Update tenant
                for (Tenant tenant : tenants) {
                    if (tenant.getName().equals(name)) {
                        tenant.setEmail("");
                        tenant.setEmailNotifications(false);
                        dbManager.saveTenant(tenant);
                        break;
                    }
                }
                
                showSavedStatus("Tenant email removed successfully");
            }
        });

        configureEmailButton.setOnAction(e -> {
            if (CommunicationManager.getInstance().configureEmailSettings()) {
                showSavedStatus("Email settings saved successfully");
            }
        });

        emailButtonBox.getChildren().addAll(addEmailButton, removeEmailButton, configureEmailButton);

        tenantEmailBox.getChildren().addAll(
            emailTitle,
            emailGrid,
            new Label("Current Tenant Emails:"),
            emailList,
            emailButtonBox
        );

        // Get current tenant
        currentTenant = tenants.isEmpty() ? null : tenants.get(0);

        // Always show tenant email management section
        communicationBox.getChildren().add(tenantEmailBox);

        if (currentTenant != null) {
            // Communication Preferences Section
            VBox preferencesBox = new VBox(5);
            preferencesBox.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-border-color: #e1e1e1; -fx-border-radius: 4px;");
            
            Label preferencesTitle = new Label("Communication Preferences");
            preferencesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            CheckBox emailNotifications = new CheckBox("Email Notifications");
            emailNotifications.setSelected(currentTenant.getEmailNotifications());
            
            CheckBox smsNotifications = new CheckBox("SMS Notifications");
            smsNotifications.setSelected(currentTenant.getSmsNotifications());

            // Contact Information
            GridPane contactGrid = new GridPane();
            contactGrid.setHgap(10);
            contactGrid.setVgap(10);
            contactGrid.setPadding(new Insets(10, 0, 10, 0));

            TextField emailField = new TextField(currentTenant.getEmail());
            emailField.setPromptText("Email Address");
            
            TextField phoneField = new TextField(currentTenant.getPhone());
            phoneField.setPromptText("Phone Number");

            contactGrid.add(new Label("Email:"), 0, 0);
            contactGrid.add(emailField, 1, 0);
            contactGrid.add(new Label("Phone:"), 0, 1);
            contactGrid.add(phoneField, 1, 1);

            preferencesBox.getChildren().addAll(
                preferencesTitle,
                emailNotifications,
                smsNotifications,
                new Separator(),
                contactGrid
            );

            // Communication History Section
            VBox historyBox = new VBox(5);
            historyBox.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-border-color: #e1e1e1; -fx-border-radius: 4px;");
            
            Label historyTitle = new Label("Communication History");
            historyTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            ListView<String> historyList = new ListView<>();
            historyList.setPrefHeight(200);
            if (currentTenant.getLastContactDate() != null) {
                historyList.getItems().add(String.format("Last Contact: %s (%s)",
                    currentTenant.getLastContactDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                    currentTenant.getLastContactType()));
            }

            // Quick Actions Section
            HBox actionsBox = new HBox(10);
            actionsBox.setAlignment(Pos.CENTER_LEFT);

            Button sendReminderButton = new Button("Send Rent Reminder");
            sendReminderButton.setStyle(BUTTON_STYLE);
            sendReminderButton.setOnAction(e -> {
                CommunicationManager.getInstance().sendRentReminder(currentTenant, property);
                currentTenant.updateLastContact("EMAIL");
                dbManager.saveTenant(currentTenant);
                historyList.getItems().add(0, String.format("Sent rent reminder email (%s)",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy"))));
                showSavedStatus("Rent reminder sent successfully");
            });

            Button savePreferencesButton = new Button("Save Preferences");
            savePreferencesButton.setStyle(BUTTON_STYLE);
            savePreferencesButton.setOnAction(e -> {
                currentTenant.setEmailNotifications(emailNotifications.isSelected());
                currentTenant.setSmsNotifications(smsNotifications.isSelected());
                currentTenant.setEmail(emailField.getText());
                currentTenant.setPhone(phoneField.getText());
                dbManager.saveTenant(currentTenant);
                showSavedStatus("Communication preferences saved successfully");
            });

            actionsBox.getChildren().addAll(sendReminderButton, savePreferencesButton);

            historyBox.getChildren().addAll(historyTitle, historyList);

            communicationBox.getChildren().addAll(preferencesBox, historyBox, actionsBox);
        } else {
            Label noTenantLabel = new Label("No tenant currently assigned to this property");
            noTenantLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            communicationBox.getChildren().add(noTenantLabel);
        }

        communicationTab.setContent(communicationBox);

        // Add all tabs
        tabPane.getTabs().addAll(detailsTab, rentTab, expensesTab, communicationTab);
        
        // Create the form grid for property details

        // Spinners for numeric values
        Spinner<Integer> bedrooms = new Spinner<>(0, 10, property.getBedrooms());
        Spinner<Integer> livingRooms = new Spinner<>(0, 5, property.getLivingRooms());
        Spinner<Integer> kitchens = new Spinner<>(0, 3, property.getKitchens());
        Spinner<Integer> bathrooms = new Spinner<>(0, 5, property.getBathrooms());

        // ComboBox for house type
        ComboBox<String> houseType = new ComboBox<>(HOUSE_TYPES);
        houseType.setValue(property.getHouseType().isEmpty() ? "Detached" : property.getHouseType());

        // Description text area
        TextArea description = new TextArea(property.getDescription());
        description.setPrefRowCount(3);
        description.setWrapText(true);

        // Add fields to grid
        grid.add(new Label("Bedrooms:"), 0, 0);
        grid.add(bedrooms, 1, 0);
        grid.add(new Label("Living Rooms:"), 0, 1);
        grid.add(livingRooms, 1, 1);
        grid.add(new Label("Kitchens:"), 0, 2);
        grid.add(kitchens, 1, 2);
        grid.add(new Label("Bathrooms:"), 0, 3);
        grid.add(bathrooms, 1, 3);
        grid.add(new Label("House Type:"), 0, 4);
        grid.add(houseType, 1, 4);
        grid.add(new Label("Description:"), 0, 5);
        grid.add(description, 1, 5);
        
        detailsTab.setContent(grid);

        dialog.getDialogPane().setContent(tabPane);

        // Convert the result to property when button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                property.setBedrooms(bedrooms.getValue());
                property.setLivingRooms(livingRooms.getValue());
                property.setKitchens(kitchens.getValue());
                property.setBathrooms(bathrooms.getValue());
                property.setHouseType(houseType.getValue());
                property.setDescription(description.getText());
                return property;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedProperty -> {
            dbManager.saveProperty(updatedProperty);
            showSavedStatus("Property details updated successfully");
        });
    }

    private Scene createLoginScene(Stage stage) {
        VBox loginBox = new VBox(20); // Increased spacing
        loginBox.setStyle(LOGIN_BOX_STYLE);

        // Logo
        try {
            ImageView logoView = new ImageView(new Image(new FileInputStream("logo.png")));
            logoView.setFitHeight(100);
            logoView.setFitWidth(100);
            logoView.setPreserveRatio(true);
            loginBox.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }

        // Title
        Label titleLabel = new Label("Property Management");
        titleLabel.setStyle(HEADER_STYLE);

        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setStyle(INPUT_FIELD_STYLE);

        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle(INPUT_FIELD_STYLE);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setStyle(BUTTON_STYLE);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(BUTTON_STYLE + BUTTON_HOVER_STYLE));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(BUTTON_STYLE));

        // Error message
        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);
        errorLabel.setStyle("-fx-font-size: 13px;");

        // Create signup button
        Button signupButton = new Button("Sign Up");
        signupButton.setStyle(BUTTON_STYLE);
        signupButton.setOnMouseEntered(e -> signupButton.setStyle(BUTTON_STYLE + BUTTON_HOVER_STYLE));
        signupButton.setOnMouseExited(e -> signupButton.setStyle(BUTTON_STYLE));

        // Create login handler
        Runnable loginHandler = () -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            User user = userManager.authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                dbManager.setDatabase(user.getDbPath());
                properties.setAll(dbManager.loadProperties());
                stage.setScene(createMainScene());
                stage.setMaximized(true);
            } else {
                errorLabel.setText("Invalid username or password");
                passwordField.clear();
            }
        };

        // Create signup handler
        signupButton.setOnAction(e -> {
            Dialog<User> dialog = new Dialog<>();
            dialog.setTitle("Sign Up");
            dialog.setHeaderText("Create a new account");

            // Set the button types
            ButtonType signupButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(signupButtonType, ButtonType.CANCEL);

            // Create the signup form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField newUsername = new TextField();
            newUsername.setPromptText("Username");
            PasswordField newPassword = new PasswordField();
            newPassword.setPromptText("Password");
            TextField newEmail = new TextField();
            newEmail.setPromptText("Email");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(newUsername, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(newPassword, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(newEmail, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Enable/Disable signup button depending on whether a username was entered
            Node dialogSignupButton = dialog.getDialogPane().lookupButton(signupButtonType);
            dialogSignupButton.setDisable(true);

            // Do validation
            newUsername.textProperty().addListener((observable, oldValue, newValue) -> {
                dialogSignupButton.setDisable(newValue.trim().isEmpty());
            });

            // Convert the result to a User object when the signup button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == signupButtonType) {
                    String username = newUsername.getText().trim();
                    String password = newPassword.getText();
                    String email = newEmail.getText().trim();

                    if (userManager.usernameExists(username)) {
                        errorLabel.setText("Username already exists");
                        return null;
                    }

                    if (userManager.registerUser(username, password, email, "user")) {
                        return userManager.authenticateUser(username, password);
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(user -> {
                if (user != null) {
                    currentUser = user;
                    dbManager.setDatabase(user.getDbPath());
                    properties.clear();
                    stage.setScene(createMainScene());
                    stage.setMaximized(true);
                }
            });
        });

        // Set action for login button
        loginButton.setOnAction(e -> loginHandler.run());

        // Handle Enter key in username field
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                passwordField.requestFocus();
            }
        });

        // Handle Enter key in password field
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                loginHandler.run();
            }
        });

        // Create button container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, signupButton);

        loginBox.getChildren().addAll(titleLabel, usernameField, passwordField, buttonBox, errorLabel);

        // Main container
        VBox root = new VBox(loginBox);
        root.setStyle("-fx-background-color: #f5f6fa;");
        root.setAlignment(Pos.CENTER);

        return new Scene(root, 800, 600);
    }

    private Scene createMainScene() {
        // Create main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle(MAIN_BACKGROUND);
        
        // Create tab pane for different views
        TabPane mainTabs = new TabPane();
        mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(mainTabs, Priority.ALWAYS);

        // Properties List Tab
        Tab propertiesTab = new Tab("Properties");
        VBox propertiesView = new VBox(10);
        propertiesView.setPadding(new Insets(10));

        TableView<Property> table = new TableView<>();
        table.setStyle(TABLE_STYLE);
        table.setEditable(true);

        // Add row styling based on property status
        table.setRowFactory(tv -> {
            TableRow<Property> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("");
                } else {
                    if (newItem.getStatus().equals("Vacant")) {
                        row.setStyle("-fx-background-color: #ffebee;"); // Light red for vacant
                    } else {
                        row.setStyle("-fx-background-color: #e8f5e9;"); // Light green for rented
                    }
                }
            });
            return row;
        });

        String columnStyle = "-fx-alignment: CENTER-LEFT; -fx-padding: 0 10px;";

        // Property ID Column
        TableColumn<Property, String> idCol = new TableColumn<>("Property ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPropertyId()));
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(event -> {
            Property property = event.getRowValue();
            String newId = event.getNewValue().trim();
            
            // Check if ID is empty
            if (newId.isEmpty()) {
                table.refresh(); // Revert the change
                showSavedStatus("Property ID cannot be empty");
                return;
            }
            
            // Check if ID is already in use by another property
            boolean idExists = properties.stream()
                .filter(p -> p != property) // Exclude current property
                .anyMatch(p -> p.getPropertyId().equals(newId));
                
            if (idExists) {
                table.refresh(); // Revert the change
                showSavedStatus("Property ID already exists");
                return;
            }
            
            // ID is valid, save it
            property.setPropertyId(newId);
            savePropertyToDatabase(property);
            table.refresh();
        });
        idCol.setStyle(columnStyle);

        // Owner Column
        TableColumn<Property, String> ownerCol = new TableColumn<>("Owner");
        ownerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOwnerName()));
        ownerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ownerCol.setOnEditCommit(event -> {
            event.getRowValue().setOwnerName(event.getNewValue());
            savePropertyToDatabase(event.getRowValue());
        });
        ownerCol.setStyle(columnStyle);

        // Address Column
        TableColumn<Property, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        addressCol.setCellFactory(TextFieldTableCell.forTableColumn());
        addressCol.setOnEditCommit(event -> {
            event.getRowValue().setAddress(event.getNewValue());
            savePropertyToDatabase(event.getRowValue());
        });
        addressCol.setStyle(columnStyle);

        // Rent Column
        TableColumn<Property, Double> rentCol = new TableColumn<>("Rent");
        rentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMonthlyRent()).asObject());
        rentCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        rentCol.setOnEditCommit(event -> {
            event.getRowValue().setMonthlyRent(event.getNewValue());
            savePropertyToDatabase(event.getRowValue());
            table.refresh(); // Refresh to update profit calculation
            // Trigger the total profit update
            table.getItems().set(event.getTablePosition().getRow(), event.getRowValue());
        });
        rentCol.setStyle(columnStyle + "-fx-alignment: CENTER-RIGHT;");

        // Mortgage Column
        TableColumn<Property, Double> mortgageCol = new TableColumn<>("Mortgage");
        mortgageCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMonthlyMortgage()).asObject());
        mortgageCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        mortgageCol.setOnEditCommit(event -> {
            event.getRowValue().setMonthlyMortgage(event.getNewValue());
            savePropertyToDatabase(event.getRowValue());
            table.refresh(); // Refresh to update profit calculation
            // Trigger the total profit update
            table.getItems().set(event.getTablePosition().getRow(), event.getRowValue());
        });
        mortgageCol.setStyle(columnStyle + "-fx-alignment: CENTER-RIGHT;");

        // Status Column
        TableColumn<Property, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setCellFactory(ComboBoxTableCell.forTableColumn(VALID_STATUSES));
        statusCol.setOnEditCommit(event -> {
            Property property = event.getRowValue();
            property.setStatus(event.getNewValue());
            if (event.getNewValue().equals("Vacant")) {
                property.setMonthlyRent(0.0);
            }
            savePropertyToDatabase(property);
            table.refresh(); // Refresh to update rent and profit display
            // Trigger the total profit update
            table.getItems().set(event.getTablePosition().getRow(), event.getRowValue());
        });
        statusCol.setStyle(columnStyle);

        // Profit Column (read-only)
        TableColumn<Property, Number> profitCol = new TableColumn<>("Profit");
        profitCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getMonthlyProfit()));
        profitCol.setStyle(columnStyle + "-fx-alignment: CENTER-RIGHT;");

        // Actions Column
        TableColumn<Property, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(5);
            private final Button viewButton = new Button("View");
            private final Button deleteButton = new Button("Delete");
            {
                viewButton.setStyle("""
                    -fx-background-color: #3498db;
                    -fx-text-fill: white;
                    -fx-cursor: hand;
                    -fx-padding: 5 10;
                    -fx-font-size: 12px;
                """);
                deleteButton.setStyle("""
                    -fx-background-color: #e74c3c;
                    -fx-text-fill: white;
                    -fx-cursor: hand;
                    -fx-padding: 5 10;
                    -fx-font-size: 12px;
                """);
                container.getChildren().addAll(viewButton, deleteButton);
                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Property property = getTableView().getItems().get(getIndex());
                    
                    viewButton.setOnAction(event -> showPropertyDetails(property));
                    
                    deleteButton.setOnAction(event -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Confirm Deletion");
                        alert.setHeaderText("Delete Property");
                        alert.setContentText("Are you sure you want to delete property " + property.getPropertyId() + "?");
                        
                        alert.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                dbManager.deleteProperty(property.getPropertyId());
                                properties.remove(property);
                                showSavedStatus("Property deleted successfully");
                            }
                        });
                    });
                    
                    setGraphic(container);
                }
            }
        });
        actionsCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(idCol, ownerCol, addressCol, rentCol, mortgageCol, statusCol, profitCol, actionsCol);
        table.setItems(properties);

        // Add Property Button
        Button addButton = new Button("Add New Property");
        addButton.setStyle("""
            -fx-background-color: #27ae60;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 8 16;
            -fx-cursor: hand;
        """);
        addButton.setOnMouseEntered(e -> addButton.setStyle(addButton.getStyle() + "-fx-background-color: #219a52;"));
        addButton.setOnMouseExited(e -> addButton.setStyle(addButton.getStyle().replace("-fx-background-color: #219a52;", "-fx-background-color: #27ae60;")));
        
        addButton.setOnAction(e -> {
            // For vacant properties, rent is always 0
            Property newProperty = new Property("", "", "", 0.0, 0.0, "Vacant");
            properties.add(newProperty);
            table.edit(properties.size() - 1, idCol);
        });

        // Status label for save confirmation
        statusLabel = new Label("");
        statusLabel.setTextFill(Color.web("#27ae60"));
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Instructions label
        Label instructionsLabel = new Label("Double-click any cell to edit • Press Enter to save changes • Click View for more details");
        instructionsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        // Logo, title, and logout container
        HBox titleContainer = new HBox(20);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        // Create right-side container for logout
        HBox rightContainer = new HBox(10);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);

        // Create logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("""
            -fx-background-color: #e74c3c;
            -fx-text-fill: white;
            -fx-font-size: 14px;
            -fx-padding: 8 16;
            -fx-cursor: hand;
            -fx-background-radius: 4px;
        """);
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle(logoutButton.getStyle() + "-fx-background-color: #c0392b;"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle(logoutButton.getStyle().replace("-fx-background-color: #c0392b;", "-fx-background-color: #e74c3c;")));
        
        // Add logout handler
        logoutButton.setOnAction(e -> {
            // Clear current user data
            currentUser = null;
            properties.clear();
            
            // Switch back to login scene
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(createLoginScene(stage));
            stage.setMaximized(false);
            stage.centerOnScreen();
        });

        rightContainer.getChildren().add(logoutButton);

        // Logo
        try {
            ImageView logoView = new ImageView(new Image(new FileInputStream("logo.png")));
            logoView.setFitHeight(50);
            logoView.setFitWidth(50);
            logoView.setPreserveRatio(true);
            titleContainer.getChildren().add(logoView);
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }

        // Title and summary cards
        VBox titleAndSummary = new VBox(10);
        
        Label titleLabel = new Label("Property Management Dashboard");
        titleLabel.setStyle(HEADER_STYLE);

        // Summary cards
        HBox summaryCards = new HBox(20);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        String cardStyle = """
            -fx-background-color: white;
            -fx-padding: 5px 10px;
            -fx-background-radius: 5px;
            -fx-border-color: #e1e1e1;
            -fx-border-radius: 5px;
            -fx-min-width: 120px;
            -fx-max-width: 120px;
            -fx-alignment: center;
        """;

        // Total Properties Card
        VBox propertiesCard = new VBox(5);
        propertiesCard.setStyle(cardStyle);
        Label propCount = new Label(String.valueOf(properties.size()));
        propCount.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label propLabel = new Label("Total Properties");
        propLabel.setStyle("-fx-font-size: 12px;");
        propertiesCard.getChildren().addAll(propCount, propLabel);

        // Occupancy Rate Card
        VBox occupancyCard = new VBox(5);
        occupancyCard.setStyle(cardStyle);
        long rentedCount = properties.stream()
            .filter(p -> p.getStatus().equals("Rented"))
            .count();
        double occupancyRate = properties.isEmpty() ? 0 : 
            (double) rentedCount / properties.size() * 100;
        Label occRate = new Label(String.format("%.1f%%", occupancyRate));
        occRate.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label occLabel = new Label("Occupancy Rate");
        occLabel.setStyle("-fx-font-size: 12px;");
        occupancyCard.getChildren().addAll(occRate, occLabel);

        // Monthly Income Card
        VBox incomeCard = new VBox(5);
        incomeCard.setStyle(cardStyle);
        double totalIncome = properties.stream()
            .mapToDouble(Property::getMonthlyProfit)
            .sum();
        Label incomeAmount = new Label(String.format("£%.2f", totalIncome));
        incomeAmount.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label incomeLabel = new Label("Monthly Income");
        incomeLabel.setStyle("-fx-font-size: 12px;");
        incomeCard.getChildren().addAll(incomeAmount, incomeLabel);

        summaryCards.getChildren().addAll(propertiesCard, occupancyCard, incomeCard);
        
        titleAndSummary.getChildren().addAll(titleLabel, summaryCards);
        titleContainer.getChildren().addAll(titleAndSummary, rightContainer);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search properties...");
        searchField.setStyle("""
            -fx-pref-width: 300px;
            """ + INPUT_FIELD_STYLE);

        // Filter controls
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All Statuses", "Rented Only", "Vacant Only");
        statusFilter.setValue("All Statuses");
        statusFilter.setStyle(INPUT_FIELD_STYLE);

        // Sort controls
        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.getItems().addAll("Sort by ID", "Sort by Rent (High to Low)", "Sort by Profit (High to Low)");
        sortBy.setValue("Sort by ID");
        sortBy.setStyle(INPUT_FIELD_STYLE);

        // Export and Communication buttons
        Button exportPdfButton = new Button("Export PDF");
        Button exportExcelButton = new Button("Export Excel");
        Button bulkEmailButton = new Button("Email All Tenants");
        
        String exportButtonStyle = """
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-font-size: 12px;
            -fx-padding: 5 10;
            -fx-cursor: hand;
        """;
        
        exportPdfButton.setStyle(exportButtonStyle);
        exportExcelButton.setStyle(exportButtonStyle);
        bulkEmailButton.setStyle(exportButtonStyle);
        
        bulkEmailButton.setOnAction(e -> {
            // Create bulk email dialog
            Dialog<Map<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Send Bulk Email");
            dialog.setHeaderText("Send email to all tenants");

            // Set the button types
            ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

            // Create the email form grid
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Template selection
            ComboBox<CommunicationManager.EmailTemplate> templateBox = new ComboBox<>();
            templateBox.getItems().addAll(CommunicationManager.EmailTemplate.values());
            templateBox.setPromptText("Select Template");
            templateBox.setMaxWidth(Double.MAX_VALUE);

            TextField subjectField = new TextField();
            subjectField.setPromptText("Email Subject");

            TextArea contentArea = new TextArea();
            contentArea.setPromptText("Custom Message");
            contentArea.setPrefRowCount(5);
            contentArea.setWrapText(true);

            grid.add(new Label("Template:"), 0, 0);
            grid.add(templateBox, 1, 0);
            grid.add(new Label("Subject:"), 0, 1);
            grid.add(subjectField, 1, 1);
            grid.add(new Label("Message:"), 0, 2);
            grid.add(contentArea, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Enable/Disable send button depending on whether a template was selected
            Node sendButton = dialog.getDialogPane().lookupButton(sendButtonType);
            sendButton.setDisable(true);

            templateBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    sendButton.setDisable(false);
                    subjectField.setText(newValue.getDisplayName());
                }
            });

            // Convert the result when the send button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == sendButtonType) {
                    Map<String, String> result = new HashMap<>();
                    result.put("template", templateBox.getValue().name());
                    result.put("subject", subjectField.getText());
                    result.put("message", contentArea.getText());
                    return result;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(result -> {
                // Get all tenants with email notifications enabled
                List<Tenant> allTenants = new ArrayList<>();
                for (Property property : properties) {
                    List<Tenant> propertyTenants = dbManager.getTenantsForProperty(property.getPropertyId());
                    allTenants.addAll(propertyTenants);
                }

                // Prepare email parameters
                Map<String, String> params = new HashMap<>();
                params.put("ANNOUNCEMENT_TEXT", result.get("message"));
                params.put("EMERGENCY_DETAILS", result.get("message"));
                params.put("POLICY_CHANGES", result.get("message"));
                params.put("HOLIDAY_DETAILS", result.get("message"));

                // Send bulk email
                CommunicationManager.getInstance().sendBulkEmail(
                    allTenants,
                    result.get("subject"),
                    result.get("template"),
                    params
                );

                showSavedStatus("Bulk email sent successfully");
            });
        });

        exportPdfButton.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            java.io.File file = fileChooser.showSaveDialog(table.getScene().getWindow());
            
            if (file != null) {
                ReportGenerator.generatePdfReport(table.getItems(), file.getAbsolutePath());
                showSavedStatus("PDF report generated successfully");
            }
        });
        
        exportExcelButton.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Excel Report");
            fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            java.io.File file = fileChooser.showSaveDialog(table.getScene().getWindow());
            
            if (file != null) {
                ReportGenerator.exportToExcel(table.getItems(), file.getAbsolutePath());
                showSavedStatus("Excel report generated successfully");
            }
        });

        // Search and filter container
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.getChildren().addAll(
            searchField,
            new Label("Status:"),
            statusFilter,
            new Label("Sort:"),
            sortBy,
            new javafx.scene.control.Separator(javafx.geometry.Orientation.VERTICAL),
            exportPdfButton,
            exportExcelButton,
            bulkEmailButton
        );

        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                table.setItems(properties);
            } else {
                ObservableList<Property> filteredList = properties.filtered(property ->
                    property.getPropertyId().toLowerCase().contains(newValue.toLowerCase()) ||
                    property.getOwnerName().toLowerCase().contains(newValue.toLowerCase()) ||
                    property.getAddress().toLowerCase().contains(newValue.toLowerCase()) ||
                    property.getStatus().toLowerCase().contains(newValue.toLowerCase())
                );
                table.setItems(filteredList);
            }
        });

        // Add status filter functionality
        statusFilter.setOnAction(e -> {
            String filter = statusFilter.getValue();
            ObservableList<Property> currentItems = searchField.getText().isEmpty() ? properties : table.getItems();
            
            if (filter.equals("All Statuses")) {
                table.setItems(currentItems);
            } else {
                String status = filter.equals("Rented Only") ? "Rented" : "Vacant";
                ObservableList<Property> filteredList = currentItems.filtered(property ->
                    property.getStatus().equals(status)
                );
                table.setItems(filteredList);
            }
        });

        // Add sorting functionality
        sortBy.setOnAction(e -> {
            String sort = sortBy.getValue();
            ObservableList<Property> currentItems = table.getItems();
            
            switch (sort) {
                case "Sort by ID":
                    FXCollections.sort(currentItems, (p1, p2) -> 
                        p1.getPropertyId().compareTo(p2.getPropertyId()));
                    break;
                case "Sort by Rent (High to Low)":
                    FXCollections.sort(currentItems, (p1, p2) -> 
                        Double.compare(p2.getMonthlyRent(), p1.getMonthlyRent()));
                    break;
                case "Sort by Profit (High to Low)":
                    FXCollections.sort(currentItems, (p1, p2) -> 
                        Double.compare(p2.getMonthlyProfit(), p1.getMonthlyProfit()));
                    break;
            }
        });

        // Header container
        VBox headerContainer = new VBox(10);
        headerContainer.getChildren().addAll(titleContainer, instructionsLabel, searchContainer, addButton);
        headerContainer.setPadding(new Insets(0, 0, 20, 0));

        // Total profit and mortgage container
        HBox totalsContainer = new HBox();
        totalsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15px; -fx-border-color: #e1e1e1; -fx-border-radius: 4px;");
        
        // Create a spacer to push profit to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Total mortgage label
        Label totalMortgageLabel = new Label();
        totalMortgageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

        // Total profit label
        Label totalProfitLabel = new Label();
        totalProfitLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        // Initial calculations
        double initialTotalMortgage = properties.stream()
            .mapToDouble(Property::getMonthlyMortgage)
            .sum();
        double initialTotalProfit = properties.stream()
            .mapToDouble(Property::getMonthlyProfit)
            .sum();
        
        totalMortgageLabel.setText(String.format("Total Monthly Mortgage: £%.2f", initialTotalMortgage));
        totalProfitLabel.setText(String.format("Total Monthly Profit: £%.2f", initialTotalProfit));
        
        // Update totals and summary cards whenever the table items change
        table.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Property> c) -> {
            double totalProfit = table.getItems().stream()
                .mapToDouble(Property::getMonthlyProfit)
                .sum();
            double totalMortgage = table.getItems().stream()
                .mapToDouble(Property::getMonthlyMortgage)
                .sum();
                
            totalProfitLabel.setText(String.format("Total Monthly Profit: £%.2f", totalProfit));
            totalMortgageLabel.setText(String.format("Total Monthly Mortgage: £%.2f", totalMortgage));
            
            // Update summary cards
            propCount.setText(String.valueOf(table.getItems().size()));
            long currentRentedCount = table.getItems().stream()
                .filter(p -> p.getStatus().equals("Rented"))
                .count();
            double currentOccupancyRate = table.getItems().isEmpty() ? 0 : 
                (double) currentRentedCount / table.getItems().size() * 100;
            occRate.setText(String.format("%.1f%%", currentOccupancyRate));
            incomeAmount.setText(String.format("£%.2f", totalProfit));
        });
        
        totalsContainer.getChildren().addAll(totalMortgageLabel, spacer, totalProfitLabel);

        // Set up properties tab content
        propertiesView.getChildren().addAll(headerContainer, table, totalsContainer, statusLabel);
        VBox.setVgrow(table, Priority.ALWAYS);
        propertiesTab.setContent(propertiesView);

        // Calendar Tab
        Tab calendarTab = new Tab("Calendar");
        VBox calendarView = createCalendarView();
        calendarTab.setContent(calendarView);

        // Add all tabs
        mainTabs.getTabs().addAll(propertiesTab, calendarTab);

        // Main container
        mainLayout.getChildren().addAll(mainTabs);
        mainLayout.setStyle(MAIN_BACKGROUND);

        return new Scene(mainLayout);
    }

    private void showSavedStatus(String message) {
        statusLabel.setText(message);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                javafx.application.Platform.runLater(() -> statusLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private VBox createCalendarView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: white;");

        // Calendar header
        Label title = new Label("Payment Calendar");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Calendar grid
        GridPane calendar = new GridPane();
        calendar.setHgap(10);
        calendar.setVgap(10);
        calendar.setPadding(new Insets(20));
        calendar.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20px; -fx-border-radius: 5px;");

        // Add days of week header
        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label day = new Label(daysOfWeek[i]);
            day.setStyle("-fx-font-weight: bold;");
            calendar.add(day, i, 0);
        }

        // Get current month's dates
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        int monthLength = today.lengthOfMonth();
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1;

        // Add dates to calendar
        for (int i = 1; i <= monthLength; i++) {
            LocalDate date = LocalDate.of(today.getYear(), today.getMonth(), i);
            VBox dateBox = new VBox(5);
            dateBox.setStyle("""
                -fx-background-color: white;
                -fx-padding: 10px;
                -fx-border-color: #e1e1e1;
                -fx-border-radius: 5px;
                -fx-min-width: 100px;
                -fx-min-height: 80px;
            """);

            Label dateLabel = new Label(String.valueOf(i));
            dateBox.getChildren().add(dateLabel);

            // Add payment indicators for each property
            for (Property property : properties) {
                List<LocalDate> payments = dbManager.getRentPaymentDates(property.getPropertyId());
                if (payments.contains(date)) {
                    Label paymentLabel = new Label("• " + property.getPropertyId());
                    paymentLabel.setStyle("-fx-text-fill: #27ae60;");
                    dateBox.getChildren().add(paymentLabel);
                }
            }

            calendar.add(dateBox, (firstDayOfWeek + i - 1) % 7, (firstDayOfWeek + i - 1) / 7 + 1);
        }

        container.getChildren().addAll(title, calendar);
        return container;
    }

    private void showSavedStatus() {
        showSavedStatus("Changes saved");
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Property Management System");
        stage.setScene(createLoginScene(stage));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}