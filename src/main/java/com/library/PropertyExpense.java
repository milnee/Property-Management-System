package com.library;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PropertyExpense {
    private int id;
    private String propertyId;
    private String description;
    private double amount;
    private LocalDate date;
    private String category; // REPAIR, MAINTENANCE, UTILITIES, INSURANCE, TAX, OTHER
    private String notes;

    public PropertyExpense(int id, String propertyId, String description, double amount, 
                         LocalDate date, String category) {
        this.id = id;
        this.propertyId = propertyId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.notes = "";
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("d MMMM yyyy"));
    }

    public String getFormattedAmount() {
        return String.format("£%.2f", amount);
    }
}
