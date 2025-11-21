package com.example.gympro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.gympro.utils.DatabaseConnection;
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

        FXMLLoader fxmlLoader = new FXMLLoader(GymProApp.class.getResource("/com/example/gympro/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("GymPro - Gym Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
