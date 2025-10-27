package com.example.gympro;

import java.sql.Connection;
import java.sql.SQLException;

import com.example.gympro.utils.DatabaseConnection;

public class MainApp {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Kết nối MySQL Docker thành công!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
