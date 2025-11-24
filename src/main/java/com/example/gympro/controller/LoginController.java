package com.example.gympro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.gympro.GymProApp;
import com.example.gympro.service.AuthService;
import com.example.gympro.service.SessionManager;
import com.example.gympro.service.AuthService.AuthResult;
import javafx.scene.control.Alert;
import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            AuthService authService = new AuthService();
            AuthResult result = authService.authenticate(username, password);
            if (!result.success) {
                showError(result.message);
                return;
            }

            SessionManager.getInstance().startSession(result.user, result.roles);

            FXMLLoader fxmlLoader = new FXMLLoader(GymProApp.class.getResource("/com/example/gympro/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e.getCause() instanceof java.sql.SQLException) {
                showError("Database connection error: " + e.getCause().getMessage() + 
                         "\nPlease check:\n- Is MySQL running?\n- Is application.properties configured correctly?");
            } else {
                showError("Error: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot load main screen: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Unknown error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login failed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
