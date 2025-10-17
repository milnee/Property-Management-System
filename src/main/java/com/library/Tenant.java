package com.library;

import java.time.LocalDate;

public class Tenant {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String propertyId;
    private LocalDate leaseStartDate;
    private LocalDate leaseEndDate;
    private double depositAmount;
    private String documents; // Comma-separated list of document paths
    private String paymentHistory; // JSON string of payment history
    private String communicationPreferences; // JSON string of communication preferences
    private LocalDate lastContactDate;
    private String lastContactType; // EMAIL, SMS, PHONE
    private boolean emailNotifications;
    private boolean smsNotifications;

    public Tenant(int id, String name, String email, String phone, String propertyId,
                 LocalDate leaseStartDate, LocalDate leaseEndDate, double depositAmount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.propertyId = propertyId;
        this.leaseStartDate = leaseStartDate;
        this.leaseEndDate = leaseEndDate;
        this.depositAmount = depositAmount;
        this.documents = "";
        this.paymentHistory = "[]";
        this.communicationPreferences = "{}";
        this.lastContactDate = null;
        this.lastContactType = "";
        this.emailNotifications = true;
        this.smsNotifications = false;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public LocalDate getLeaseStartDate() { return leaseStartDate; }
    public void setLeaseStartDate(LocalDate leaseStartDate) { this.leaseStartDate = leaseStartDate; }

    public LocalDate getLeaseEndDate() { return leaseEndDate; }
    public void setLeaseEndDate(LocalDate leaseEndDate) { this.leaseEndDate = leaseEndDate; }

    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double depositAmount) { this.depositAmount = depositAmount; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }

    public String getPaymentHistory() { return paymentHistory; }
    public void setPaymentHistory(String paymentHistory) { this.paymentHistory = paymentHistory; }

    public void addDocument(String documentPath) {
        if (documents.isEmpty()) {
            documents = documentPath;
        } else {
            documents += "," + documentPath;
        }
    }

    public void removeDocument(String documentPath) {
        String[] docs = documents.split(",");
        StringBuilder newDocs = new StringBuilder();
        for (String doc : docs) {
            if (!doc.equals(documentPath)) {
                if (newDocs.length() > 0) {
                    newDocs.append(",");
                }
                newDocs.append(doc);
            }
        }
        documents = newDocs.toString();
    }

    public String[] getDocumentList() {
        return documents.isEmpty() ? new String[0] : documents.split(",");
    }

    // Communication-related methods
    public String getCommunicationPreferences() { return communicationPreferences; }
    public void setCommunicationPreferences(String preferences) { this.communicationPreferences = preferences; }

    public LocalDate getLastContactDate() { return lastContactDate; }
    public void setLastContactDate(LocalDate date) { this.lastContactDate = date; }

    public String getLastContactType() { return lastContactType; }
    public void setLastContactType(String type) { this.lastContactType = type; }

    public boolean getEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean enabled) { this.emailNotifications = enabled; }

    public boolean getSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(boolean enabled) { this.smsNotifications = enabled; }

    public LocalDate getNextRentDue() {
        LocalDate today = LocalDate.now();
        LocalDate nextDue = today.withDayOfMonth(1); // First of current month
        
        if (today.isAfter(nextDue)) {
            nextDue = nextDue.plusMonths(1); // First of next month
        }
        
        return nextDue;
    }

    public void updateLastContact(String type) {
        this.lastContactDate = LocalDate.now();
        this.lastContactType = type;
    }
}
