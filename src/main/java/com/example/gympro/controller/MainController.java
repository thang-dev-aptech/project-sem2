package com.example.gympro.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.gympro.GymProApp;
import com.example.gympro.service.SessionManager;
import com.example.gympro.service.PermissionManager;
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
                "💳 Payment", "⏰ Expiring Members", "📈 Reports", "⚙️ Settings", "👤 Users"
        };
        String[] screenIds = {
                "dashboard", "members", "packages", "registration",
                "payment", "expiry", "reports", "settings", "users"
        };

        for (int i = 0; i < menuItems.length; i++) {
            final String screenId = screenIds[i];
            
            // Kiểm tra quyền truy cập cho từng menu
            boolean hasAccess = checkMenuAccess(screenId);
            if (!hasAccess) {
                continue; // Bỏ qua menu nếu không có quyền
            }
            
            Button btn = new Button(menuItems[i]);
            btn.getStyleClass().add("nav-button");
            btn.setOnAction(e -> {
                currentScreen = screenId;
                loadScreen(screenId);
                updateNavButtons(btn);
            });
            navMenu.getChildren().add(btn);
        }
    }
    
    /**
     * Kiểm tra quyền truy cập cho từng menu
     * ADMIN: truy cập tất cả (100%)
     * STAFF: chỉ truy cập Dashboard, Members (xem), Registration, Payment (40%)
     */
    private boolean checkMenuAccess(String screenId) {
        // ADMIN có quyền truy cập tất cả
        if (PermissionManager.isAdmin()) {
            return true;
        }
        
        // STAFF chỉ có quyền truy cập một số menu nhất định
        if (PermissionManager.isStaff()) {
            return switch (screenId) {
                case "dashboard" -> PermissionManager.canViewDashboard();
                case "members" -> PermissionManager.canViewMembers();
                case "registration" -> PermissionManager.canRegisterMembers();
                case "payment" -> PermissionManager.canManagePayments();
                default -> false; // Các menu khác staff không được truy cập
            };
        }
        
        return false; // Mặc định không có quyền
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
        // KHÔNG kiểm tra quyền nữa - đã kiểm tra khi tạo menu rồi
        // Nếu menu có hiển thị thì user đã có quyền truy cập
        
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
                case "expiry" -> "/com/example/gympro/fxml/expiry_view.fxml";

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
        // Clear session
        SessionManager.getInstance().endSession();
        
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
