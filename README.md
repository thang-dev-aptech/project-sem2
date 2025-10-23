# 🏋️‍♂️ GymPro - Gym Management System

A comprehensive desktop application for managing gym operations, built with JavaFX and MySQL.

## 🚀 Quick Start

### Prerequisites
- **Java 21+**
- **Maven 3.9+**
- **Docker & Docker Compose**

### One-Command Setup
```bash
# Clone repository
git clone <repository-url>
cd GymPro

# Run setup script (Linux/macOS)
./scripts/setup.sh

# Or manual setup (Windows)
# 1. Copy config/application.properties to config/local.properties
# 2. docker-compose up -d
# 3. mvn javafx:run
```

### Manual Setup
```bash
# 1. Configuration setup
cp config/application.properties config/local.properties

# 2. Start database
docker-compose up -d

# 3. Run application
mvn javafx:run
```

## 📊 Features

### For Staff (STAFF Role)
- 👥 **Member Management**: Add, edit, view members
- 📝 **Registration/Renewal**: Process membership subscriptions
- 💳 **Payment Processing**: Handle payments and invoices
- ⏰ **Expiration Alerts**: Track members nearing expiration
- 🚪 **Shift Management**: Open/close work shifts

### For Owners (OWNER Role)
- 📊 **Advanced Reports**: Revenue, membership, performance analytics
- 📦 **Plan Management**: Create and manage membership plans
- 👥 **Staff Management**: Manage user accounts and roles
- ⚙️ **System Configuration**: Customize settings and preferences
- 📋 **Audit Logs**: Track all system activities
- 💾 **Backup/Restore**: Database backup and recovery

## 🏗️ Architecture

### Technology Stack
- **Frontend**: JavaFX 21
- **Backend**: Java 21 + Maven
- **Database**: MySQL 8.0
- **Migration**: Flyway
- **Containerization**: Docker + Docker Compose

### Project Structure
```
GymPro/
├── db/
│   ├── migration/          # Database schema migrations
│   └── init/               # Database initialization
├── src/
│   ├── main/java/          # Java source code
│   └── main/resources/     # Configuration files
├── scripts/                # Setup and utility scripts
├── docker-compose.yml      # Docker services
└── env.example            # Environment template
```

## 🗄️ Database

### Access Database
- **Adminer**: http://localhost:8080
- **phpMyAdmin**: http://localhost:8081
- **Direct MySQL**: localhost:3306

### Default Credentials
- **Username**: gympro_user
- **Password**: gympro_password
- **Database**: gympro

## 🛠️ Development

### Daily Workflow
```bash
# 1. Pull latest changes
git pull origin main

# 2. Start database
docker-compose up -d

# 3. Run application
mvn javafx:run

# 4. Make changes and test
# 5. Commit and push
```

### Database Management
```bash
# Reset database
./scripts/reset-db.sh

# Backup database
docker exec gympro_mysql mysqldump -u gympro_user -p gympro > backup.sql

# Restore database
docker exec -i gympro_mysql mysql -u gympro_user -p gympro < backup.sql
```

### Building
```bash
# Compile
mvn clean compile

# Package
mvn clean package

# Run tests
mvn test
```

## 📱 UI Layout

The application features a comprehensive UI design with:

- **Login Screen**: Secure authentication
- **Staff Dashboard**: Member management, payments, shifts
- **Owner Dashboard**: Reports, configuration, audit logs
- **Responsive Design**: Works on different screen sizes
- **Professional UI**: Modern, intuitive interface

See `GYMPRO_UI_LAYOUT_COMPLETE.md` for detailed UI specifications.

## 🔧 Configuration

### Configuration Files
The application uses multiple configuration files for different environments:

#### Main Configuration
- **`src/main/resources/application.properties`**: Default configuration
- **`src/main/resources/application-dev.properties`**: Development settings
- **`src/main/resources/application-prod.properties`**: Production settings

#### Local Configuration
- **`config/application.properties`**: Template for local configuration
- **`config/local.properties`**: Your local settings (git ignored)

#### Database Configuration
- **`src/main/resources/database.properties`**: Database connection settings
- **`src/main/resources/flyway.conf`**: Flyway migration settings

### Configuration Priority
1. `config/local.properties` (highest priority)
2. `src/main/resources/application-{profile}.properties`
3. `src/main/resources/application.properties` (lowest priority)

## 🚨 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Check what's using port 3306
lsof -i :3306  # macOS/Linux
netstat -ano | findstr :3306  # Windows

# Change port in .env
MYSQL_PORT=3307
```

#### Database Connection Failed
```bash
# Check if MySQL is running
docker-compose ps

# Check logs
docker-compose logs mysql

# Restart if needed
docker-compose restart mysql
```

#### Application Won't Start
```bash
# Check Java version (need 21+)
java -version

# Check Maven
mvn -version

# Clean and rebuild
mvn clean compile
```

### Reset Everything
```bash
# Nuclear option
docker-compose down -v
docker system prune -a
./scripts/setup.sh
```

## 👥 Team Development

### Git Workflow
1. **Pull before starting**: `git pull origin main`
2. **Create feature branch**: `git checkout -b feature/your-feature`
3. **Commit frequently**: `git commit -m "feat: add member management"`
4. **Push regularly**: `git push origin feature/your-feature`
5. **Create PR for review**

### Code Standards
- Follow Java naming conventions
- Use meaningful comments
- Test changes before committing
- Use migrations for database changes

## 📚 Documentation

- **Team Setup Guide**: `TEAM_SETUP_GUIDE.md`
- **Database Setup**: `DATABASE_SETUP.md`
- **UI Layout**: `GYMPRO_UI_LAYOUT_COMPLETE.md`
- **Project Overview**: `README_PROJECT_OVERVIEW.md`

## 🚀 Deployment

### Development
```bash
docker-compose up -d
mvn javafx:run
```

### Production
```bash
mvn clean package -Pproduction
java -jar target/GymPro-1.0.0.jar
```

## 📞 Support

### Getting Help
1. Check this README
2. Check `TEAM_SETUP_GUIDE.md`
3. Search existing issues
4. Ask in team chat
5. Create issue if needed

### Useful Commands
```bash
# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Restart services
docker-compose restart

# Stop everything
docker-compose down

# Start everything
docker-compose up -d
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Team

- **Project Lead**: [Your Name]
- **Backend Developer**: [Name]
- **Frontend Developer**: [Name]
- **Database Designer**: [Name]
- **Tester**: [Name]

---

**Happy Coding! 🎉**

For detailed setup instructions, see `TEAM_SETUP_GUIDE.md`
