package com.library;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public class CommunicationManager {
    private static CommunicationManager instance;
    private final Properties emailProperties;
    private final Map<String, String> templateCache;
    private final EmailSettings emailSettings;

    private CommunicationManager() {
        emailSettings = new EmailSettings();
        
        emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth", "true");
        emailProperties.put("mail.smtp.starttls.enable", "true");
        emailProperties.put("mail.smtp.host", "smtp-relay.sendinblue.com");
        emailProperties.put("mail.smtp.port", "587");
        emailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        templateCache = new HashMap<>();
        loadTemplates();
    }

    public static CommunicationManager getInstance() {
        if (instance == null) {
            instance = new CommunicationManager();
        }
        return instance;
    }

    public enum EmailTemplate {
        RENT_REMINDER("Rent Payment Reminder"),
        MAINTENANCE_UPDATE("Maintenance Update"),
        INSPECTION_NOTICE("Property Inspection Notice"),
        MONTHLY_REPORT("Monthly Property Report"),
        GENERAL_ANNOUNCEMENT("General Announcement"),
        LEASE_RENEWAL("Lease Renewal Notice"),
        MAINTENANCE_SCHEDULE("Maintenance Schedule"),
        HOLIDAY_NOTICE("Holiday Notice"),
        POLICY_UPDATE("Policy Update"),
        EMERGENCY_NOTICE("Emergency Notice");

        private final String displayName;

        EmailTemplate(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private void loadTemplates() {
        // General Announcement Template
        templateCache.put("GENERAL_ANNOUNCEMENT", """
            Dear {TENANT_NAME},

            {ANNOUNCEMENT_TEXT}

            If you have any questions or concerns, please don't hesitate to contact us.

            Best regards,
            Property Management Team
        """);

        // Lease Renewal Template
        templateCache.put("LEASE_RENEWAL", """
            Dear {TENANT_NAME},

            Your lease for {PROPERTY_ADDRESS} is due to expire on {LEASE_END_DATE}. We would like to offer you the opportunity to renew your lease.

            Current Terms:
            - Monthly Rent: £{CURRENT_RENT}
            - Lease Period: {LEASE_PERIOD}

            Please let us know your decision regarding lease renewal by {RESPONSE_DEADLINE}.

            Best regards,
            Property Management Team
        """);

        // Maintenance Schedule Template
        templateCache.put("MAINTENANCE_SCHEDULE", """
            Dear {TENANT_NAME},

            We have scheduled the following maintenance work at {PROPERTY_ADDRESS}:

            Work Details:
            - Type: {MAINTENANCE_TYPE}
            - Date: {SCHEDULED_DATE}
            - Time: {SCHEDULED_TIME}
            - Duration: {ESTIMATED_DURATION}

            Please ensure the property is accessible during this time.

            Best regards,
            Property Management Team
        """);

        // Holiday Notice Template
        templateCache.put("HOLIDAY_NOTICE", """
            Dear {TENANT_NAME},

            This is to inform you about our holiday schedule:

            {HOLIDAY_DETAILS}

            For emergencies during this period, please contact: {EMERGENCY_CONTACT}

            Best regards,
            Property Management Team
        """);

        // Policy Update Template
        templateCache.put("POLICY_UPDATE", """
            Dear {TENANT_NAME},

            We are writing to inform you about important updates to our policies:

            {POLICY_CHANGES}

            These changes will take effect from {EFFECTIVE_DATE}.

            Best regards,
            Property Management Team
        """);

        // Emergency Notice Template
        templateCache.put("EMERGENCY_NOTICE", """
            Dear {TENANT_NAME},

            IMPORTANT NOTICE:

            {EMERGENCY_DETAILS}

            Action Required:
            {ACTION_REQUIRED}

            Emergency Contact: {EMERGENCY_CONTACT}

            Best regards,
            Property Management Team
        """);

        // Rent Payment Reminder Template
        templateCache.put("RENT_REMINDER", """
            Dear {TENANT_NAME},

            This is a friendly reminder that your rent payment of £{RENT_AMOUNT} for the property at {PROPERTY_ADDRESS} is due on {DUE_DATE}.

            Payment Details:
            - Amount Due: £{RENT_AMOUNT}
            - Due Date: {DUE_DATE}
            - Property: {PROPERTY_ADDRESS}

            Please ensure timely payment to avoid any late fees.

            If you have already made the payment, please disregard this notice.

            Best regards,
            {OWNER_NAME}
            Property Management System
            """);

        // Maintenance Request Update Template
        templateCache.put("MAINTENANCE_UPDATE", """
            Dear {TENANT_NAME},

            This is an update regarding your maintenance request for {PROPERTY_ADDRESS}:

            Request Details:
            - Issue: {MAINTENANCE_ISSUE}
            - Status: {STATUS}
            - Scheduled Date: {SCHEDULED_DATE}
            - Contractor: {CONTRACTOR_NAME}

            {ADDITIONAL_NOTES}

            If you have any questions, please don't hesitate to contact us.

            Best regards,
            Property Management Team
            """);

        // Property Inspection Notice Template
        templateCache.put("INSPECTION_NOTICE", """
            Dear {TENANT_NAME},

            This is to notify you that a routine property inspection has been scheduled for your property at {PROPERTY_ADDRESS}.

            Inspection Details:
            - Date: {INSPECTION_DATE}
            - Time: {INSPECTION_TIME}
            - Duration: Approximately 30 minutes

            Please ensure the property is accessible at the scheduled time. If this timing doesn't work for you, please contact us to reschedule.

            Best regards,
            Property Management Team
            """);

        // Monthly Report Template
        templateCache.put("MONTHLY_REPORT", """
            Dear {OWNER_NAME},

            Here is your monthly property report for {MONTH_YEAR}:

            Property: {PROPERTY_ADDRESS}
            
            Financial Summary:
            - Rent Collected: £{RENT_COLLECTED}
            - Maintenance Costs: £{MAINTENANCE_COSTS}
            - Net Income: £{NET_INCOME}

            Occupancy Status: {OCCUPANCY_STATUS}
            Current Tenant: {TENANT_NAME}

            Maintenance Summary:
            {MAINTENANCE_SUMMARY}

            Upcoming Events:
            {UPCOMING_EVENTS}

            Please review the attached detailed report for more information.

            Best regards,
            Property Management Team
            """);
    }

    public void sendBulkEmail(List<Tenant> tenants, String subject, String templateName, Map<String, String> parameters) {
        for (Tenant tenant : tenants) {
            if (tenant.getEmailNotifications() && tenant.getEmail() != null && !tenant.getEmail().isEmpty()) {
                // Add tenant-specific parameters
                Map<String, String> tenantParams = new HashMap<>(parameters);
                tenantParams.put("TENANT_NAME", tenant.getName());
                sendEmail(tenant.getEmail(), subject, templateName, tenantParams);
                tenant.updateLastContact("EMAIL");
            }
        }
    }

    public boolean configureEmailSettings() {
        return emailSettings.showSettingsDialog();
    }

    public boolean hasValidSettings() {
        return emailSettings.hasValidSettings();
    }

    public void sendEmail(String to, String subject, String templateName, Map<String, String> parameters) {
        if (!hasValidSettings()) {
            throw new IllegalStateException("Email settings not configured. Please configure email settings first.");
        }

        try {
            Email from = new Email(emailSettings.getEmail());
            Email toEmail = new Email(to);
            
            // Get template and replace parameters
            String contentText = templateCache.get(templateName);
            if (contentText != null) {
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    contentText = contentText.replace("{" + entry.getKey() + "}", entry.getValue());
                }
            }
            
            Content content = new Content("text/plain", contentText);
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(emailSettings.getPassword());
            Request request = new Request();
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 400) {
                throw new IllegalStateException("Failed to send email: " + response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to send email: " + e.getMessage());
        }
    }

    public void sendRentReminder(Tenant tenant, PropertyApp.Property property) {
        Map<String, String> params = new HashMap<>();
        params.put("TENANT_NAME", tenant.getName());
        params.put("RENT_AMOUNT", String.format("%.2f", property.getMonthlyRent()));
        params.put("PROPERTY_ADDRESS", property.getAddress());
        params.put("DUE_DATE", tenant.getNextRentDue().format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
        params.put("OWNER_NAME", property.getOwnerName());

        sendEmail(tenant.getEmail(), 
                 "Rent Payment Reminder - " + property.getAddress(),
                 "RENT_REMINDER", 
                 params);
    }

    public void sendMaintenanceUpdate(Tenant tenant, PropertyApp.Property property, 
                                    MaintenanceRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("TENANT_NAME", tenant.getName());
        params.put("PROPERTY_ADDRESS", property.getAddress());
        params.put("MAINTENANCE_ISSUE", request.getDescription());
        params.put("STATUS", request.getStatus());
        params.put("SCHEDULED_DATE", request.getScheduledDate() != null ? 
                  request.getScheduledDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy")) : "To be scheduled");
        params.put("CONTRACTOR_NAME", request.getContractorName());
        params.put("ADDITIONAL_NOTES", request.getNotes());

        sendEmail(tenant.getEmail(),
                 "Maintenance Update - " + property.getAddress(),
                 "MAINTENANCE_UPDATE",
                 params);
    }

    public void sendInspectionNotice(Tenant tenant, PropertyApp.Property property, 
                                   LocalDate inspectionDate, String inspectionTime) {
        Map<String, String> params = new HashMap<>();
        params.put("TENANT_NAME", tenant.getName());
        params.put("PROPERTY_ADDRESS", property.getAddress());
        params.put("INSPECTION_DATE", inspectionDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
        params.put("INSPECTION_TIME", inspectionTime);

        sendEmail(tenant.getEmail(),
                 "Property Inspection Notice - " + property.getAddress(),
                 "INSPECTION_NOTICE",
                 params);
    }

    public void sendMonthlyReport(String ownerEmail, PropertyApp.Property property,
                                double rentCollected, double maintenanceCosts,
                                String maintenanceSummary, String upcomingEvents) {
        Map<String, String> params = new HashMap<>();
        params.put("OWNER_NAME", property.getOwnerName());
        params.put("MONTH_YEAR", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        params.put("PROPERTY_ADDRESS", property.getAddress());
        params.put("RENT_COLLECTED", String.format("%.2f", rentCollected));
        params.put("MAINTENANCE_COSTS", String.format("%.2f", maintenanceCosts));
        params.put("NET_INCOME", String.format("%.2f", rentCollected - maintenanceCosts));
        params.put("OCCUPANCY_STATUS", property.getStatus());
        params.put("MAINTENANCE_SUMMARY", maintenanceSummary);
        params.put("UPCOMING_EVENTS", upcomingEvents);

        sendEmail(ownerEmail,
                 "Monthly Property Report - " + property.getAddress() + " - " + 
                 LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                 "MONTHLY_REPORT",
                 params);
    }
}
