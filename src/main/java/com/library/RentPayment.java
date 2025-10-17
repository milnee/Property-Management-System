package com.library;

import java.time.LocalDate;

public class RentPayment {
    private int id;
    private int tenantId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod; // CASH, BANK_TRANSFER, CHECK, OTHER
    private String notes;

    public RentPayment(int id, int tenantId, double amount, String paymentMethod) {
        this.id = id;
        this.tenantId = tenantId;
        this.amount = amount;
        this.paymentDate = LocalDate.now();
        this.paymentMethod = paymentMethod;
        this.notes = "";
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isLate() {
        // Consider a payment late if it's made after the 5th of the month
        return paymentDate.getDayOfMonth() > 5;
    }

    public String getFormattedAmount() {
        return String.format("£%.2f", amount);
    }

    public String getFormattedDate() {
        return paymentDate.toString();
    }
}
