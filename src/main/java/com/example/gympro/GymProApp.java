package com.example.gympro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.gympro.utils.DatabaseConnection;
import com.example.gympro.service.AuthService;
import com.example.gympro.service.SessionManager;
import com.example.gympro.service.AuthService.AuthResult;
import java.io.IOException;

public class GymProApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database connection before loading UI
        try {
            DatabaseConnection.getInstance();
            System.out.println("✓ Database connection initialized");
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            // Continue anyway - show error in UI
        }
        
        // AUTO LOGIN với tài khoản admin (CHỈ DÙNG CHO DEV/TEST)
        try {
            AuthService authService = new AuthService();
            AuthResult result = authService.authenticate("admin", "admin123");
            if (result.success) {
                SessionManager.getInstance().startSession(result.user, result.roles);
                System.out.println("✓ Auto-login successful as ADMIN");
            } else {
                System.err.println("✗ Auto-login failed: " + result.message);
            }
        } catch (Exception e) {
            System.err.println("✗ Auto-login error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Load màn hình chính (main.fxml)
        FXMLLoader fxmlLoader = new FXMLLoader(GymProApp.class.getResource("/com/example/gympro/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
        stage.setTitle("GymPro - Gym Management System");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
