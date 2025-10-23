# 👥 GymPro Team Setup Guide

## 🚀 Quick Start cho Team Members

### 1. Prerequisites
```bash
# Cài đặt Docker & Docker Compose
# Windows: Docker Desktop
# macOS: Docker Desktop
# Linux: docker + docker-compose

# Kiểm tra cài đặt
docker --version
docker-compose --version
```

### 2. Clone Project
```bash
git clone <repository-url>
cd GymPro
```

### 3. Configuration Setup
```bash
# Copy configuration template
cp config/application.properties config/local.properties

# Chỉnh sửa config/local.properties nếu cần (optional)
# Mặc định đã sẵn sàng để chạy
```

### 4. Start Database
```bash
# Khởi động database với Docker
docker-compose up -d

# Kiểm tra status
docker-compose ps

# Xem logs nếu cần
docker-compose logs mysql
```

### 5. Verify Database
```bash
# Database sẽ tự động tạo schema và data mẫu
# Truy cập Adminer: http://localhost:8080
# Server: mysql
# Username: gympro_user
# Password: gympro_password
# Database: gympro
```

### 6. Run Application
```bash
# Chạy JavaFX application
mvn javafx:run

# Hoặc build và chạy
mvn clean package
java -jar target/GymPro-1.0.0.jar
```

## 🔧 Development Workflow

### Daily Development
```bash
# 1. Pull latest changes
git pull origin main

# 2. Start database (if not running)
docker-compose up -d

# 3. Run application
mvn javafx:run

# 4. Make changes and test
# 5. Commit and push
git add .
git commit -m "feat: your changes"
git push origin your-branch
```

### Database Changes
```bash
# 1. Tạo migration file mới
# File: db/migration/V2__Your_Changes.sql

# 2. Test migration
docker-compose down
docker-compose up -d

# 3. Verify changes
# Check Adminer or run test queries
```

### Reset Database (if needed)
```bash
# Xóa database và tạo lại
docker-compose down -v
docker-compose up -d

# Database sẽ được tạo lại với schema mới nhất
```

## 📁 Project Structure

```
GymPro/
├── db/
│   ├── migration/          # Database schema migrations
│   │   └── V1__GymPro_Complete_Schema.sql
│   └── init/               # Database initialization scripts
│       └── 01-init-database.sql
├── src/
│   ├── main/
│   │   ├── java/           # Java source code
│   │   └── resources/      # Configuration files
│   └── test/               # Test code
├── docker-compose.yml      # Docker services
├── config/                 # Configuration files
│   ├── application.properties  # Template
│   └── local.properties        # Your config (git ignored)
├── .gitignore             # Git ignore rules
└── README.md              # Project documentation
```

## 🗄️ Database Management

### Access Database
- **Adminer**: http://localhost:8080
- **phpMyAdmin**: http://localhost:8081
- **Direct MySQL**: localhost:3306

### Default Credentials
- **Root**: root / root123
- **App User**: gympro_user / gympro_password
- **Database**: gympro

### Backup Database
```bash
# Backup
docker exec gympro_mysql mysqldump -u gympro_user -p gympro > backup.sql

# Restore
docker exec -i gympro_mysql mysql -u gympro_user -p gympro < backup.sql
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Check what's using port 3306
lsof -i :3306  # macOS/Linux
netstat -ano | findstr :3306  # Windows

# Stop conflicting service or change port in .env
MYSQL_PORT=3307
```

#### 2. Database Connection Failed
```bash
# Check if MySQL is running
docker-compose ps

# Check logs
docker-compose logs mysql

# Restart if needed
docker-compose restart mysql
```

#### 3. Permission Denied
```bash
# Make sure Docker has permission to access project directory
# On macOS/Linux:
sudo chown -R $USER:$USER .

# On Windows: Run Docker Desktop as Administrator
```

#### 4. Out of Disk Space
```bash
# Clean up Docker
docker system prune -a

# Remove unused volumes
docker volume prune
```

### Reset Everything
```bash
# Nuclear option - reset everything
docker-compose down -v
docker system prune -a
docker-compose up -d
```

## 📋 Team Guidelines

### Git Workflow
1. **Always pull before starting work**
2. **Create feature branches**: `git checkout -b feature/your-feature`
3. **Commit frequently**: `git commit -m "feat: add member management"`
4. **Push regularly**: `git push origin feature/your-feature`
5. **Create PR for review**

### Code Standards
- **Java**: Follow Java naming conventions
- **SQL**: Use UPPER_CASE for keywords
- **Comments**: Write meaningful comments
- **Testing**: Test your changes before committing

### Database Changes
- **Never modify production data directly**
- **Always use migrations for schema changes**
- **Test migrations on local first**
- **Document breaking changes**

### Configuration Files
- **Never commit config/local.properties**
- **Use config/application.properties as template**
- **Document new configuration properties**

## 🚀 Deployment

### Development
```bash
# Local development
docker-compose up -d
mvn javafx:run
```

### Production
```bash
# Build production JAR
mvn clean package -Pproduction

# Run with production config
java -jar target/GymPro-1.0.0.jar --spring.profiles.active=production
```

## 📞 Support

### Getting Help
1. **Check this guide first**
2. **Search existing issues**
3. **Ask in team chat**
4. **Create issue if needed**

### Useful Commands
```bash
# Check Docker status
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

---

**Happy Coding! 🎉**

Nếu gặp vấn đề, hãy check troubleshooting section hoặc hỏi team!
