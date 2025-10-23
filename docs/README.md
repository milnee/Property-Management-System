# 🏠 Property Management System

A comprehensive **JavaFX-based Property Management System**, created with the assistance of **AI tools** to demonstrate full-stack application design and development.  

This project is designed for **property owners and managers** who need an easy way to manage their properties, tenants, rent payments, maintenance requests, and communication — all in one place.

---

## ⚙️ Overview

This system allows users to:
- Manage multiple properties  
- Track tenants and lease details  
- Monitor rent payments and expenses  
- Handle maintenance requests efficiently  
- Send automated email notifications  
- Generate financial and operational reports  

While the project was developed using **AI-assisted coding** (for structure, boilerplate code, and UI design), it demonstrates how AI can be integrated into the **software development workflow** to accelerate prototyping and learning.

---

## 🧠 Features

- **Property Management** – Add, edit, and track property details  
- **Tenant Management** – Maintain tenant information and lease periods  
- **Rent Payment Tracking** – Log payments and generate rent reports  
- **Maintenance Requests** – Submit and track maintenance issues  
- **Email Notifications** – Automatically send updates and reminders  
- **Report Generation** – Export data as PDF or Excel reports  
- **User Management** – Multi-user access with role-based permissions  
- **Built-in Database** – Uses local **SQLite** for storage; **no external setup required**  
- **Runnable JAR Output** – Easily generate a `.jar` file that can be distributed to end-users  

---

## 🗂️ Project Structure

```text
Property-Management-System/
├── db/                          # Embedded SQLite database files
│   ├── property_management_admin.db
│   ├── users.db
│   └── ...
├── resources/                   # Images, configuration, and assets
│   ├── images/
│   └── config/email_settings.properties
├── src/main/java/com/library/    # Main source code
├── scripts/                      # Run scripts for both OS types
│   ├── run.sh                   # For macOS/Linux
│   └── run.bat                  # For Windows
├── docs/                         # Documentation
├── target/                       # Maven build output
└── pom.xml                       # Maven configuration
