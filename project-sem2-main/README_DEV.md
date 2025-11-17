# Khởi động database
docker-compose up -d

# Dừng database  
docker-compose down

# Xem logs
docker-compose logs

# Reset database (nếu cần)
docker-compose down -v && docker-compose up -d

Server: mysql
User: root
Pass: root123
db : gympro