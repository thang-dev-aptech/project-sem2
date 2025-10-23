#!/bin/bash

# GymPro Team Setup Script
# This script helps team members set up the project quickly

set -e  # Exit on any error

echo "🚀 GymPro Team Setup Script"
echo "=========================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed
check_docker() {
    print_status "Checking Docker installation..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        echo "Visit: https://docs.docker.com/get-docker/"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        echo "Visit: https://docs.docker.com/compose/install/"
        exit 1
    fi
    
    print_success "Docker and Docker Compose are installed"
}

# Check if Java is installed
check_java() {
    print_status "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 21+ first."
        echo "Visit: https://adoptium.net/"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        print_error "Java 21+ is required. Current version: $JAVA_VERSION"
        exit 1
    fi
    
    print_success "Java $JAVA_VERSION is installed"
}

# Check if Maven is installed
check_maven() {
    print_status "Checking Maven installation..."
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven first."
        echo "Visit: https://maven.apache.org/install.html"
        exit 1
    fi
    
    print_success "Maven is installed"
}

# Setup configuration files
setup_config() {
    print_status "Setting up configuration files..."
    
    # Create config directory if it doesn't exist
    mkdir -p config
    
    # Copy template config if local config doesn't exist
    if [ ! -f config/local.properties ]; then
        if [ -f config/application.properties ]; then
            cp config/application.properties config/local.properties
            print_success "Created config/local.properties from template"
        else
            print_warning "No config template found, using defaults"
        fi
    else
        print_warning "config/local.properties already exists, skipping..."
    fi
}

# Start Docker services
start_docker() {
    print_status "Starting Docker services..."
    
    # Stop any existing containers
    docker-compose down 2>/dev/null || true
    
    # Start services
    docker-compose up -d
    
    # Wait for MySQL to be ready
    print_status "Waiting for MySQL to be ready..."
    timeout=60
    while [ $timeout -gt 0 ]; do
        if docker exec gympro_mysql mysqladmin ping -h localhost --silent; then
            print_success "MySQL is ready"
            break
        fi
        sleep 2
        timeout=$((timeout - 2))
    done
    
    if [ $timeout -le 0 ]; then
        print_error "MySQL failed to start within 60 seconds"
        docker-compose logs mysql
        exit 1
    fi
}

# Verify database setup
verify_database() {
    print_status "Verifying database setup..."
    
    # Check if database exists and has tables
    TABLE_COUNT=$(docker exec gympro_mysql mysql -u gympro_user -pgympro_password -e "USE gympro; SHOW TABLES;" 2>/dev/null | wc -l)
    
    if [ $TABLE_COUNT -gt 10 ]; then
        print_success "Database is properly initialized with $((TABLE_COUNT-1)) tables"
    else
        print_error "Database setup failed. Only $((TABLE_COUNT-1)) tables found"
        docker-compose logs mysql
        exit 1
    fi
}

# Build application
build_app() {
    print_status "Building application..."
    
    if mvn clean compile; then
        print_success "Application built successfully"
    else
        print_error "Application build failed"
        exit 1
    fi
}

# Show final instructions
show_instructions() {
    echo ""
    echo "🎉 Setup Complete!"
    echo "=================="
    echo ""
    echo "Database Services:"
    echo "  - MySQL: localhost:3306"
    echo "  - Adminer: http://localhost:8080"
    echo "  - phpMyAdmin: http://localhost:8081"
    echo ""
    echo "Database Credentials:"
    echo "  - Username: gympro_user"
    echo "  - Password: gympro_password"
    echo "  - Database: gympro"
    echo ""
    echo "Next Steps:"
    echo "  1. Run the application: mvn javafx:run"
    echo "  2. Or build JAR: mvn clean package"
    echo "  3. Check TEAM_SETUP_GUIDE.md for more details"
    echo ""
    echo "Configuration:"
    echo "  - Main config: src/main/resources/application.properties"
    echo "  - Local config: config/local.properties (create from template)"
    echo "  - Dev config: src/main/resources/application-dev.properties"
    echo "  - Prod config: src/main/resources/application-prod.properties"
    echo ""
    echo "Useful Commands:"
    echo "  - Stop services: docker-compose down"
    echo "  - Start services: docker-compose up -d"
    echo "  - View logs: docker-compose logs -f"
    echo "  - Reset database: docker-compose down -v && docker-compose up -d"
    echo ""
}

# Main execution
main() {
    echo "Starting setup process..."
    echo ""
    
    check_docker
    check_java
    check_maven
    setup_config
    start_docker
    verify_database
    build_app
    show_instructions
}

# Run main function
main "$@"
