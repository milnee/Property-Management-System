# Property Management System

A comprehensive JavaFX-based property management system for managing properties, tenants, rent payments, and maintenance requests.

## Features

- **Property Management**: Add, edit, and manage property details
- **Tenant Management**: Track tenant information and lease details
- **Rent Payment Tracking**: Monitor rent payments and generate reports
- **Maintenance Requests**: Handle and track maintenance requests
- **Email Notifications**: Send automated emails to tenants and property managers
- **Report Generation**: Generate PDF and Excel reports
- **User Management**: Multi-user system with role-based access
- **Database Management**: SQLite database for data persistence

## Project Structure

```sss
Property-Management-System/
├── db/                          # Database files
│   ├── property_management_admin.db
│   ├── users.db
│   └── other database files...
├── resources/                   # Application resources
│   ├── images/                 # Images and logos
│   │   └── logo.png
│   └── config/                 # Configuration files
│       └── email_settings.properties
├── src/main/java/com/library/  # Source code
├── scripts/                    # Run scripts
│   ├── run.sh                 # macOS/Linux run script
│   └── run.bat                # Windows run script
├── docs/                       # Documentation
├── target/                     # Maven build output
└── pom.xml                     # Maven configuration
```

## Requirements

- Java 17 or later
- Maven 3.6 or later
- JavaFX 21.0.1

## Installation

1. Clone or download the project
2. Ensure Java 17+ and Maven are installed
3. Navigate to the project directory

## Running the Application

### macOS/Linux:
```bash
cd scripts
./run.sh
```

### Windows:
```cmd
cd scripts
run.bat
```

### Alternative (Maven):
```bash
mvn clean compile javafx:run
```

## Configuration

### Email Settings
Configure email settings in `resources/config/email_settings.properties`:
- Set up Gmail SMTP settings
- Configure SendGrid API key for email notifications

### Database
- Default admin database: `db/property_management_admin.db`
- User databases: `db/property_management_[username].db`
- Users database: `db/users.db`

## Features Overview

### Dashboard
- Property overview
- Recent activities
- Quick statistics

### Property Management
- Add/edit properties
- Property details and photos
- Property status tracking

### Tenant Management
- Tenant information
- Lease management
- Contact details

### Financial Management
- Rent payment tracking
- Expense management
- Financial reports

### Maintenance
- Request tracking
- Priority management
- Status updates

## Development

### Building
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Creating JAR
```bash
mvn clean package
```

## License

This project is for educational and personal use.

## Support

For issues or questions, please check the documentation or contact the development team.
