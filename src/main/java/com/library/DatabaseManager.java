package com.library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.io.File;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private String dbPath;

    private DatabaseManager() {
        // Default to admin database
        this.dbPath = getDatabasePath("db/property_management_admin.db");
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private static String getDatabasePath(String relativePath) {
        try {
            // Get the directory where the JAR is located
            String jarPath = DatabaseManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            File jarDir = jarFile.getParentFile();
            
            // Create Database folder next to the JAR file
            File databaseDir = new File(jarDir, "Database");
            databaseDir.mkdirs(); // Create Database folder if it doesn't exist
            
            // Extract just the filename from the relative path (e.g., "db/property_management_admin.db" -> "property_management_admin.db")
            String fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1);
            File dbFile = new File(databaseDir, fileName);
            
            return dbFile.getAbsolutePath();
        } catch (Exception e) {
            // Fallback to current directory
            return relativePath;
        }
    }

    public static void createNewDatabase(String dbPath) {
        String resolvedPath = getDatabasePath(dbPath);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + resolvedPath)) {
            // Create all tables in the new database
            DatabaseManager tempManager = new DatabaseManager();
            tempManager.connection = conn;
            tempManager.createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDatabase(String dbPath) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            this.dbPath = dbPath;
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            // Create a new connection and tables (if they don't exist)
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Property expenses table
            String expensesSql = """
                CREATE TABLE IF NOT EXISTS property_expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    property_id TEXT,
                    description TEXT,
                    amount REAL,
                    expense_date TEXT,
                    category TEXT,
                    notes TEXT,
                    FOREIGN KEY (property_id) REFERENCES properties(property_id)
                )
            """;
            stmt.execute(expensesSql);

            // Rent payment dates table
            String rentDatesSql = """
                CREATE TABLE IF NOT EXISTS rent_payment_dates (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    property_id TEXT,
                    payment_date TEXT,
                    amount REAL,
                    status TEXT,
                    notes TEXT,
                    FOREIGN KEY (property_id) REFERENCES properties(property_id)
                )
            """;
            stmt.execute(rentDatesSql);

            // Properties table
            String propertiesSql = """
                CREATE TABLE IF NOT EXISTS properties (
                    property_id TEXT PRIMARY KEY,
                    owner_name TEXT,
                    address TEXT,
                    monthly_rent REAL,
                    monthly_mortgage REAL,
                    status TEXT,
                    bedrooms INTEGER,
                    living_rooms INTEGER,
                    kitchens INTEGER,
                    house_type TEXT,
                    bathrooms INTEGER,
                    description TEXT
                )
            """;
            stmt.execute(propertiesSql);

            // Tenants table
            String tenantsSql = """
                CREATE TABLE IF NOT EXISTS tenants (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT,
                    phone TEXT,
                    property_id TEXT,
                    lease_start_date TEXT,
                    lease_end_date TEXT,
                    deposit_amount REAL,
                    documents TEXT,
                    payment_history TEXT,
                    FOREIGN KEY (property_id) REFERENCES properties(property_id)
                )
            """;
            stmt.execute(tenantsSql);

            // Maintenance requests table
            String maintenanceSql = """
                CREATE TABLE IF NOT EXISTS maintenance_requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    property_id TEXT,
                    description TEXT,
                    status TEXT,
                    priority TEXT,
                    reported_date TEXT,
                    completed_date TEXT,
                    cost REAL,
                    notes TEXT,
                    FOREIGN KEY (property_id) REFERENCES properties(property_id)
                )
            """;
            stmt.execute(maintenanceSql);

            // Rent payments table
            String paymentsSql = """
                CREATE TABLE IF NOT EXISTS rent_payments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tenant_id INTEGER,
                    amount REAL,
                    payment_date TEXT,
                    payment_method TEXT,
                    notes TEXT,
                    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
                )
            """;
            stmt.execute(paymentsSql);

            // Property photos table
            String photosSql = """
                CREATE TABLE IF NOT EXISTS property_photos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    property_id TEXT,
                    photo_path TEXT,
                    description TEXT,
                    upload_date TEXT,
                    FOREIGN KEY (property_id) REFERENCES properties(property_id)
                )
            """;
            stmt.execute(photosSql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveProperty(PropertyApp.Property property) {
        // Don't save properties without an ID
        if (property.getPropertyId().isEmpty()) {
            return;
        }

        String sql = """
            INSERT OR REPLACE INTO properties 
            (property_id, owner_name, address, monthly_rent, monthly_mortgage, status,
             bedrooms, living_rooms, kitchens, house_type, bathrooms, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, property.getPropertyId());
            pstmt.setString(2, property.getOwnerName());
            pstmt.setString(3, property.getAddress());
            pstmt.setDouble(4, property.getMonthlyRent());
            pstmt.setDouble(5, property.getMonthlyMortgage());
            pstmt.setString(6, property.getStatus());
            pstmt.setInt(7, property.getBedrooms());
            pstmt.setInt(8, property.getLivingRooms());
            pstmt.setInt(9, property.getKitchens());
            pstmt.setString(10, property.getHouseType());
            pstmt.setInt(11, property.getBathrooms());
            pstmt.setString(12, property.getDescription());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteProperty(String propertyId) {
        String sql = "DELETE FROM properties WHERE property_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            pstmt.executeUpdate();
            reorderPropertyIds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reorderPropertyIds() {
        List<PropertyApp.Property> properties = loadProperties();
        // Clear the table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM properties");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Reinsert with new IDs
        for (int i = 0; i < properties.size(); i++) {
            PropertyApp.Property property = properties.get(i);
            PropertyApp.Property updatedProperty = new PropertyApp.Property(
                String.format("P%03d", i + 1),
                property.getOwnerName(),
                property.getAddress(),
                property.getMonthlyRent(),
                property.getMonthlyMortgage(),
                property.getStatus()
            );
            saveProperty(updatedProperty);
        }
    }

    public List<PropertyApp.Property> loadProperties() {
        List<PropertyApp.Property> properties = new ArrayList<>();
        String sql = "SELECT * FROM properties ORDER BY property_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PropertyApp.Property property = new PropertyApp.Property(
                    rs.getString("property_id"),
                    rs.getString("owner_name"),
                    rs.getString("address"),
                    rs.getDouble("monthly_rent"),
                    rs.getDouble("monthly_mortgage"),
                    rs.getString("status")
                );
                property.setBedrooms(rs.getInt("bedrooms"));
                property.setLivingRooms(rs.getInt("living_rooms"));
                property.setKitchens(rs.getInt("kitchens"));
                property.setHouseType(rs.getString("house_type"));
                property.setBathrooms(rs.getInt("bathrooms"));
                property.setDescription(rs.getString("description"));
                properties.add(property);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return properties;
    }

    // Tenant management methods
    public void saveTenant(Tenant tenant) {
        String sql = """
            INSERT OR REPLACE INTO tenants 
            (id, name, email, phone, property_id, lease_start_date, lease_end_date, 
             deposit_amount, documents, payment_history)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tenant.getId());
            pstmt.setString(2, tenant.getName());
            pstmt.setString(3, tenant.getEmail());
            pstmt.setString(4, tenant.getPhone());
            pstmt.setString(5, tenant.getPropertyId());
            pstmt.setString(6, tenant.getLeaseStartDate().toString());
            pstmt.setString(7, tenant.getLeaseEndDate().toString());
            pstmt.setDouble(8, tenant.getDepositAmount());
            pstmt.setString(9, tenant.getDocuments());
            pstmt.setString(10, tenant.getPaymentHistory());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Tenant> getTenantsForProperty(String propertyId) {
        List<Tenant> tenants = new ArrayList<>();
        String sql = "SELECT * FROM tenants WHERE property_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Tenant tenant = new Tenant(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("property_id"),
                    LocalDate.parse(rs.getString("lease_start_date")),
                    LocalDate.parse(rs.getString("lease_end_date")),
                    rs.getDouble("deposit_amount")
                );
                tenant.setDocuments(rs.getString("documents"));
                tenant.setPaymentHistory(rs.getString("payment_history"));
                tenants.add(tenant);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tenants;
    }

    // Maintenance request methods
    public void saveMaintenanceRequest(MaintenanceRequest request) {
        String sql = """
            INSERT OR REPLACE INTO maintenance_requests 
            (id, property_id, description, status, priority, reported_date, 
             completed_date, cost, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, request.getId());
            pstmt.setString(2, request.getPropertyId());
            pstmt.setString(3, request.getDescription());
            pstmt.setString(4, request.getStatus());
            pstmt.setString(5, request.getPriority());
            pstmt.setString(6, request.getReportedDate().toString());
            pstmt.setString(7, request.getCompletedDate() != null ? 
                request.getCompletedDate().toString() : null);
            pstmt.setDouble(8, request.getCost());
            pstmt.setString(9, request.getNotes());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MaintenanceRequest> getMaintenanceRequests(String propertyId) {
        List<MaintenanceRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_requests WHERE property_id = ? ORDER BY reported_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceRequest request = new MaintenanceRequest(
                    rs.getInt("id"),
                    rs.getString("property_id"),
                    rs.getString("description"),
                    rs.getString("priority")
                );
                request.setStatus(rs.getString("status"));
                request.setReportedDate(LocalDate.parse(rs.getString("reported_date")));
                String completedDate = rs.getString("completed_date");
                if (completedDate != null) {
                    request.setCompletedDate(LocalDate.parse(completedDate));
                }
                request.setCost(rs.getDouble("cost"));
                request.setNotes(rs.getString("notes"));
                requests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    // Rent payment methods
    public void saveRentPayment(RentPayment payment) {
        String sql = """
            INSERT OR REPLACE INTO rent_payments 
            (id, tenant_id, amount, payment_date, payment_method, notes)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getId());
            pstmt.setInt(2, payment.getTenantId());
            pstmt.setDouble(3, payment.getAmount());
            pstmt.setString(4, payment.getPaymentDate().toString());
            pstmt.setString(5, payment.getPaymentMethod());
            pstmt.setString(6, payment.getNotes());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<RentPayment> getRentPayments(int tenantId) {
        List<RentPayment> payments = new ArrayList<>();
        String sql = "SELECT * FROM rent_payments WHERE tenant_id = ? ORDER BY payment_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tenantId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RentPayment payment = new RentPayment(
                    rs.getInt("id"),
                    rs.getInt("tenant_id"),
                    rs.getDouble("amount"),
                    rs.getString("payment_method")
                );
                payment.setPaymentDate(LocalDate.parse(rs.getString("payment_date")));
                payment.setNotes(rs.getString("notes"));
                payments.add(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payments;
    }

    // Property photos methods
    public void savePropertyPhoto(String propertyId, String photoPath, String description) {
        String sql = """
            INSERT INTO property_photos 
            (property_id, photo_path, description, upload_date)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            pstmt.setString(2, photoPath);
            pstmt.setString(3, description);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getPropertyPhotos(String propertyId) {
        List<String> photos = new ArrayList<>();
        String sql = "SELECT photo_path FROM property_photos WHERE property_id = ? ORDER BY upload_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                photos.add(rs.getString("photo_path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return photos;
    }

    // Database backup and restore
    public void backupDatabase(String backupPath) {
        try {
            // Close the current connection to ensure all changes are saved
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            // Copy the database file
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(dbPath),
                java.nio.file.Paths.get(backupPath),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            // Reopen the connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreDatabase(String backupPath) {
        try {
            // Close the current connection
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            
            // Copy the backup file to the main database
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(backupPath),
                java.nio.file.Paths.get(dbPath),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            // Reopen the connection
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveExpense(PropertyExpense expense) {
        String sql = """
            INSERT OR REPLACE INTO property_expenses 
            (id, property_id, description, amount, expense_date, category, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, expense.getId());
            pstmt.setString(2, expense.getPropertyId());
            pstmt.setString(3, expense.getDescription());
            pstmt.setDouble(4, expense.getAmount());
            pstmt.setString(5, expense.getDate().toString());
            pstmt.setString(6, expense.getCategory());
            pstmt.setString(7, expense.getNotes());
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PropertyExpense> getPropertyExpenses(String propertyId) {
        List<PropertyExpense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM property_expenses WHERE property_id = ? ORDER BY expense_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PropertyExpense expense = new PropertyExpense(
                    rs.getInt("id"),
                    rs.getString("property_id"),
                    rs.getString("description"),
                    rs.getDouble("amount"),
                    LocalDate.parse(rs.getString("expense_date")),
                    rs.getString("category")
                );
                expense.setNotes(rs.getString("notes"));
                expenses.add(expense);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return expenses;
    }

    public void saveRentPaymentDate(String propertyId, LocalDate paymentDate, double amount, String status) {
        String sql = """
            INSERT INTO rent_payment_dates 
            (property_id, payment_date, amount, status)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            pstmt.setString(2, paymentDate.toString());
            pstmt.setDouble(3, amount);
            pstmt.setString(4, status);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<LocalDate> getRentPaymentDates(String propertyId) {
        List<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT payment_date FROM rent_payment_dates WHERE property_id = ? ORDER BY payment_date DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                dates.add(LocalDate.parse(rs.getString("payment_date")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dates;
    }

    public double getTotalExpenses(String propertyId) {
        String sql = "SELECT SUM(amount) as total FROM property_expenses WHERE property_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, propertyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}