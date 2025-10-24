package com.example.gympro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.gympro.GymProApp;
import java.io.IOException;

public class MainController {
    @FXML
    private VBox navMenu;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button logoutBtn;

    private String currentScreen = "dashboard";

    @FXML
    private void initialize() {
        createNavButtons();
        loadScreen("dashboard");
    }

    private void createNavButtons() {
        String[] menuItems = {
            "📊 Dashboard", "🧍 Members", "💪 Packages", "📅 Registration",
            "💳 Payment", "📈 Reports", "⚙️ Settings", "👤 Users"
        };
        String[] screenIds = {
            "dashboard", "members", "packages", "registration",
            "payment", "reports", "settings", "users"
        };

        for (int i = 0; i < menuItems.length; i++) {
            Button btn = new Button(menuItems[i]);
            btn.getStyleClass().add("nav-button");
            final String screenId = screenIds[i];
            btn.setOnAction(e -> {
                currentScreen = screenId;
                loadScreen(screenId);
                updateNavButtons(btn);
            });
            navMenu.getChildren().add(btn);
        }
    }

    private void updateNavButtons(Button selectedBtn) {
        for (var btn : navMenu.getChildren()) {
            if (btn instanceof Button) {
                ((Button) btn).getStyleClass().remove("selected");
            }
        }
        selectedBtn.getStyleClass().add("selected");
    }

    private void loadScreen(String screenId) {
        try {
            String fxmlFile = switch (screenId) {
                case "dashboard" -> "/com/example/gympro/fxml/dashboard.fxml";
                case "members" -> "/com/example/gympro/fxml/members.fxml";
                case "packages" -> "/com/example/gympro/fxml/packages.fxml";
                case "registration" -> "/com/example/gympro/fxml/registration.fxml";
                case "payment" -> "/com/example/gympro/fxml/payment.fxml";
                case "reports" -> "/com/example/gympro/fxml/reports.fxml";
                case "settings" -> "/com/example/gympro/fxml/settings.fxml";
                case "users" -> "/com/example/gympro/fxml/user-management.fxml";
                default -> "/com/example/gympro/fxml/dashboard.fxml";
            };

            FXMLLoader fxmlLoader = new FXMLLoader(GymProApp.class.getResource(fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(GymProApp.class.getResource("/com/example/gympro/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
