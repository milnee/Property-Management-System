package com.library;

import java.time.LocalDate;

public class MaintenanceRequest {
    private int id;
    private String propertyId;
    private String description;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private LocalDate reportedDate;
    private LocalDate completedDate;
    private double cost;
    private String notes;
    private LocalDate scheduledDate;
    private String contractorName;

    public MaintenanceRequest(int id, String propertyId, String description, String priority) {
        this.id = id;
        this.propertyId = propertyId;
        this.description = description;
        this.status = "PENDING";
        this.priority = priority;
        this.reportedDate = LocalDate.now();
        this.cost = 0.0;
        this.notes = "";
        this.scheduledDate = null;
        this.contractorName = "";
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public LocalDate getReportedDate() { return reportedDate; }
    public void setReportedDate(LocalDate reportedDate) { this.reportedDate = reportedDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate date) { this.scheduledDate = date; }

    public String getContractorName() { return contractorName; }
    public void setContractorName(String name) { this.contractorName = name; }

    public void complete(double finalCost) {
        this.status = "COMPLETED";
        this.completedDate = LocalDate.now();
        this.cost = finalCost;
    }

    public void cancel(String reason) {
        this.status = "CANCELLED";
        this.notes = reason;
    }

    public boolean isOverdue() {
        if (status.equals("COMPLETED") || status.equals("CANCELLED")) {
            return false;
        }
        
        int daysThreshold;
        switch (priority) {
            case "URGENT": daysThreshold = 1; break;
            case "HIGH": daysThreshold = 3; break;
            case "MEDIUM": daysThreshold = 7; break;
            default: daysThreshold = 14; break;
        }
        
        return LocalDate.now().isAfter(reportedDate.plusDays(daysThreshold));
    }
}
