# 🏋️ GymPro - Gym Management System

**Version:** 1.0.0  
**Technology Stack:** JavaFX 21 + MySQL 8.0 + Maven  
**Architecture:** MVVM + DAO Pattern

---

## 🎯 Overview

**GymPro** is a desktop gym management application developed with JavaFX and MySQL. The application helps digitize gym operations.

### Key Features
- ✅ Member management, packages, registration/renewal
- ✅ Payment and invoice management
- ✅ Revenue reports and statistics (Excel export)
- ✅ User management and authorization
- ✅ System configuration

---

## 💻 System Requirements

- **JDK:** 21+
- **Maven:** 3.6+
- **Docker:** (for running MySQL)
- **OS:** Windows, macOS, Linux

---

## 🚀 Getting Started

### Step 1: Extract the project
Extract the `GymPro.zip` file and open terminal in the `GymPro` directory

### Step 2: Start MySQL using Docker
```bash
docker-compose up -d
```
Wait 10-15 seconds for MySQL to fully start.

### Step 3: Run Flyway Migration to create Database and demo data
```bash
mvn flyway:migrate
```

### Step 4: Run the application

**Option 1: Run with launcher script (Recommended)**

**macOS/Linux:**
```bash
./run.sh
```

**Windows:**
```cmd
run.bat
```

**Option 2: Run with Maven**
```bash
mvn clean compile
mvn javafx:run
```

**Option 3: Build Native Application (Optional)**

Build a native application (.dmg on macOS, .exe on Windows):

**macOS/Linux:**
```bash
./build-app.sh
```

**Windows:**
```cmd
build-app.bat
```

The native application will be created in `target/dist/` directory:
- **macOS:** `GymPro-1.0.0.dmg` (double-click to install, then drag GymPro.app to Applications)
- **Windows:** `GymPro.exe`
- **Linux:** `GymPro.deb` or `GymPro.rpm`

**Test the native app:**
```bash
./test-app.sh
```

**Note:** JavaFX applications cannot run directly from JAR file. Use the launcher script, Maven command, or build native application.

### Step 5: Login
- **Username:** `admin`
- **Password:** `admin123`

---

## ⚙️ Configuration (if needed)

If MySQL is not running on `localhost:3306`, edit the file:
`src/main/resources/application.properties`

```properties
db.url=jdbc:mysql://localhost:3306/gympro?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=root123
```

---

## 🛠️ Troubleshooting

### MySQL connection error
- Check Docker container: `docker ps`
- If not found, run again: `docker-compose up -d`
- Wait additional 10-15 seconds

### Flyway migration error
- Check MySQL is running: `docker ps`
- Reset database if needed:
  ```bash
  mysql -u root -proot123 -e "DROP DATABASE IF EXISTS gympro; CREATE DATABASE gympro;"
  mvn flyway:migrate
  ```

### JavaFX runtime error
- **Do not run JAR directly** with `java -jar`
- Use launcher script: `./run.sh` (macOS/Linux) or `run.bat` (Windows)
- Or use Maven: `mvn javafx:run`

---

## 📂 Project Structure

```
GymPro/
├── src/main/java/com/example/gympro/
│   ├── controller/          # JavaFX Controllers
│   ├── domain/              # Domain Entities
│   ├── repository/          # Data Access Layer
│   ├── service/             # Business Logic
│   ├── mapper/              # Domain ↔ ViewModel Mappers
│   ├── viewModel/           # View Models
│   └── utils/               # Utilities
├── src/main/resources/
│   ├── application.properties
│   └── com/example/gympro/
│       ├── fxml/            # JavaFX FXML layouts
│       └── css/             # Stylesheets
├── db/migration/            # Flyway migrations
│   ├── V1__GymPro_Complete_Schema.sql
│   └── V2__Seed_Data.sql
├── docker-compose.yml       # Docker MySQL setup
├── run.sh                   # Launcher script (macOS/Linux)
├── run.bat                  # Launcher script (Windows)
├── build-app.sh             # Build native app script (macOS/Linux)
├── build-app.bat            # Build native app script (Windows)
└── pom.xml                  # Maven dependencies
```

---

## 🏗️ Architecture

### MVVM + DAO Pattern
```
View (FXML) → Controller (ViewModel) → Service → Repository → MySQL
```

---

## 📚 Documentation

Detailed documentation available in the `docs/` folder (if available)

---

## 📝 Changelog

### Version 1.0.0 (2025-01-15)
- ✅ Core features completed
- ✅ Excel Export
- ✅ User Management & Authorization
- ✅ Settings & Configuration

---

## 📄 License

Proprietary - Internal Use Only

---

**Last Updated:** 2025-01-15
