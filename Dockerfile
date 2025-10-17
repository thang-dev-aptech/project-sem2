# Sử dụng base image có sẵn Java
FROM openjdk:21-jdk-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file jar từ máy host vào container
COPY target/GymPro.jar .

# Thiết lập timezone và encoding
ENV TZ=Asia/Ho_Chi_Minh
ENV LANG=C.UTF-8

# Biến môi trường kết nối MySQL
ENV DB_HOST=mysql
ENV DB_PORT=3306
ENV DB_NAME=gympro
ENV DB_USER=gympro_app
ENV DB_PASS=gympro123

# Lệnh chạy ứng dụng Java
CMD ["java", "-jar", "GymPro.jar"]
