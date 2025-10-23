#!/bin/bash

# GymPro Database Reset Script
# This script resets the database to a clean state

set -e  # Exit on any error

echo "🔄 GymPro Database Reset Script"
echo "==============================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Confirm reset
confirm_reset() {
    echo ""
    print_warning "This will completely reset the database and remove all data!"
    echo "Are you sure you want to continue? (y/N)"
    read -r response
    
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        print_status "Reset cancelled"
        exit 0
    fi
}

# Stop and remove containers
reset_containers() {
    print_status "Stopping and removing containers..."
    docker-compose down -v
    
    print_success "Containers stopped and volumes removed"
}

# Remove Docker volumes
clean_volumes() {
    print_status "Cleaning up Docker volumes..."
    
    # Remove gympro volumes
    docker volume ls -q | grep gympro | xargs -r docker volume rm
    
    print_success "Docker volumes cleaned"
}

# Start fresh containers
start_fresh() {
    print_status "Starting fresh containers..."
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

# Verify reset
verify_reset() {
    print_status "Verifying database reset..."
    
    # Check if database exists and has tables
    TABLE_COUNT=$(docker exec gympro_mysql mysql -u gympro_user -pgympro_password -e "USE gympro; SHOW TABLES;" 2>/dev/null | wc -l)
    
    if [ $TABLE_COUNT -gt 10 ]; then
        print_success "Database reset successful with $((TABLE_COUNT-1)) tables"
    else
        print_error "Database reset failed. Only $((TABLE_COUNT-1)) tables found"
        docker-compose logs mysql
        exit 1
    fi
}

# Show final status
show_status() {
    echo ""
    echo "🎉 Database Reset Complete!"
    echo "=========================="
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
    echo "The database has been reset to a clean state with:"
    echo "  - Fresh schema"
    echo "  - Default configuration"
    echo "  - Sample data (if any)"
    echo ""
}

# Main execution
main() {
    confirm_reset
    reset_containers
    clean_volumes
    start_fresh
    verify_reset
    show_status
}

# Run main function
main "$@"
