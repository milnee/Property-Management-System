package com.library;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

public class DashboardView extends VBox {
    private DatabaseManager dbManager;
    private List<PropertyApp.Property> properties;

    public DashboardView(DatabaseManager dbManager, List<PropertyApp.Property> properties) {
        this.dbManager = dbManager;
        this.properties = properties;
        
        setPadding(new Insets(10));
        setSpacing(10);
        
        createDashboard();
    }

    private void createDashboard() {
        // Summary Cards
        HBox summaryCards = createSummaryCards();
        getChildren().add(summaryCards);
    }

    private HBox createSummaryCards() {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER_LEFT);
        String cardStyle = """
            -fx-background-color: white;
            -fx-padding: 10px 15px;
            -fx-background-radius: 5px;
            -fx-border-color: #e1e1e1;
            -fx-border-radius: 5px;
            -fx-min-width: 150px;
            -fx-alignment: center;
        """;

        // Total Properties Card
        VBox propertiesCard = new VBox(10);
        propertiesCard.setStyle(cardStyle);
        Label propCount = new Label(String.valueOf(properties.size()));
        propCount.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label propLabel = new Label("Total Properties");
        propertiesCard.getChildren().addAll(propCount, propLabel);

        // Occupancy Rate Card
        VBox occupancyCard = new VBox(10);
        occupancyCard.setStyle(cardStyle);
        long rentedCount = properties.stream()
            .filter(p -> p.getStatus().equals("Rented"))
            .count();
        double occupancyRate = properties.isEmpty() ? 0 : 
            (double) rentedCount / properties.size() * 100;
        Label occRate = new Label(String.format("%.1f%%", occupancyRate));
        occRate.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label occLabel = new Label("Occupancy Rate");
        occupancyCard.getChildren().addAll(occRate, occLabel);

        // Monthly Income Card
        VBox incomeCard = new VBox(10);
        incomeCard.setStyle(cardStyle);
        double totalIncome = properties.stream()
            .mapToDouble(PropertyApp.Property::getMonthlyProfit)
            .sum();
        Label incomeAmount = new Label(String.format("£%.2f", totalIncome));
        incomeAmount.setFont(Font.font("System", FontWeight.BOLD, 18));
        Label incomeLabel = new Label("Monthly Income");
        incomeCard.getChildren().addAll(incomeAmount, incomeLabel);

        container.getChildren().addAll(propertiesCard, occupancyCard, incomeCard);
        return container;
    }
}