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
import java.io.IOException;
import com.example.gympro.viewModel.*;
import com.example.gympro.authorization.*;

public class MainController {
    @FXML
    private VBox navMenu;
    @FXML
    private StackPane contentArea;
    @FXML
    private Button logoutBtn;

    private String currentScreen = "dashboard";

    private static MainController instance;

    @FXML
    private void initialize() {
        instance = this;
        createNavButtons();
        loadScreen("dashboard");

        if (contentArea.getScene() != null) {
            contentArea.getScene().getRoot().setUserData(this);
        }
        
        // In thông tin phân quyền ra console để debug
        printPermissionInfo();
    }
    
    private void printPermissionInfo() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║         PHÂN QUYỀN - MAIN SCREEN         ║");
        System.out.println("╚════════════════════════════════════════════╝");
        
        SessionManager session = SessionManager.getInstance();
        if (session.getCurrentUser() != null) {
            String username = session.getCurrentUser().getUsername();
            String fullName = session.getCurrentUser().getFullName();
            
            if (PermissionManager.isAdmin()) {
                System.out.println("👑 User: " + fullName + " (" + username + ")");
                System.out.println("✅ Vai trò: ADMIN/OWNER - 100% quyền");
                System.out.println("✅ Thấy TẤT CẢ menu: Dashboard, Members, Packages, Registration, Payment, Expiring, Reports, Settings, Users");
            } else if (PermissionManager.isStaff()) {
                System.out.println("👤 User: " + fullName + " (" + username + ")");
                System.out.println("⚠️ Vai trò: STAFF - 40% quyền");
                System.out.println("✅ Thấy menu: Dashboard, Members, Packages, Registration, Payment, Expiring");
                System.out.println("❌ KHÔNG thấy: Reports, Settings, Users");
            }
            
            System.out.println("\nTổng số quyền: " + PermissionManager.getCurrentUserPermissions().size() + " quyền");
        }
        
        System.out.println("════════════════════════════════════════════\n");
    }

    public static MainController getInstance() {
        return instance;
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
            Button btn = new Button(menuItems[i]);
            btn.getStyleClass().add("nav-button");
            final String screenId = screenIds[i];
            btn.setOnAction(e -> {
                currentScreen = screenId;
                loadScreen(screenId);
                updateNavButtons(btn);
            });
            
            // Staff sẽ KHÔNG THẤY các menu: Reports, Settings, Users
            if (screenIds[i].equals("reports")) {
                UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_REPORTS);
            }
            if (screenIds[i].equals("settings")) {
                UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_SETTINGS);
            }
            if (screenIds[i].equals("users")) {
                UIAccessControl.hideIfNoPermission(btn, Permission.VIEW_USERS);
            }
            
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

    public void navigateToRegistration(ExpiringMember expiringMember) {
        try {
            String fxmlFile = "/com/example/gympro/fxml/registration.fxml";
            FXMLLoader fxmlLoader = new FXMLLoader(GymProApp.class.getResource(fxmlFile));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(fxmlLoader.load());

            RegistrationController controller = fxmlLoader.getController();
            if (controller != null && expiringMember != null) {
                controller.fillFormForRenewal(expiringMember);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
