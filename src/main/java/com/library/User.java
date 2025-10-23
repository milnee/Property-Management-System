package com.library;

import java.io.File;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String dbPath;

    public User(int id, String username, String password, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.dbPath = getDatabasePath(String.format("db/property_management_%s.db", username));
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { 
        this.username = username;
        this.dbPath = getDatabasePath(String.format("db/property_management_%s.db", username));
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDbPath() { return dbPath; }

    private static String getDatabasePath(String relativePath) {
        try {
            // Get the directory where the JAR is located
            String jarPath = User.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            File jarDir = jarFile.getParentFile();
            
            // Create Database folder next to the JAR file
            File databaseDir = new File(jarDir, "Database");
            databaseDir.mkdirs(); // Create Database folder if it doesn't exist
            
            // Extract just the filename from the relative path (e.g., "db/property_management_username.db" -> "property_management_username.db")
            String fileName = relativePath.substring(relativePath.lastIndexOf("/") + 1);
            File dbFile = new File(databaseDir, fileName);
            
            return dbFile.getAbsolutePath();
        } catch (Exception e) {
            // Fallback to current directory
            return relativePath;
        }
    }
}
