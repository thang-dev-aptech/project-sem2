package com.example.gympro.service;

/**
 * AuthorizationService - Check user access permissions
 * 
 * Permission rules:
 * - OWNER: Full access (CRUD all)
 * - STAFF: View and create only, cannot delete/edit some sensitive modules
 */
public class AuthorizationService {
    
    private final SessionManager sessionManager;
    
    public AuthorizationService() {
        this.sessionManager = SessionManager.getInstance();
    }
    
    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String roleName) {
        return sessionManager.hasRole(roleName);
    }
    
    /**
     * Check if user is OWNER
     */
    public boolean isOwner() {
        return hasRole("OWNER");
    }
    
    /**
     * Check if user is STAFF
     */
    public boolean isStaff() {
        return hasRole("STAFF");
    }
    
    /**
     * Check if user has view permission (all roles have this)
     */
    public boolean canView() {
        return sessionManager.getCurrentUser() != null;
    }
    
    /**
     * Check if user has create permission
     * OWNER: Yes, STAFF: Yes (except some special modules)
     */
    public boolean canCreate() {
        return canView();
    }
    
    /**
     * Check if user has edit permission
     * OWNER: Yes, STAFF: Yes (except some special modules)
     */
    public boolean canEdit() {
        return canView();
    }
    
    /**
     * Check if user has delete permission
     * OWNER: Yes, STAFF: No
     */
    public boolean canDelete() {
        return isOwner();
    }
    
    /**
     * Check if user can manage packages
     * OWNER: Full access, STAFF: View only
     */
    public boolean canManagePackages() {
        return isOwner();
    }
    
    /**
     * Check if user can manage system settings
     * Only OWNER has this permission
     */
    public boolean canManageSettings() {
        return isOwner();
    }
    
    /**
     * Check if user can view reports
     * OWNER: Yes, STAFF: Yes
     */
    public boolean canViewReports() {
        return canView();
    }
    
    /**
     * Check if user can export reports/Excel
     * OWNER: Yes, STAFF: Yes
     */
    public boolean canExportReports() {
        return canView();
    }
    
    /**
     * Check if user can apply discount
     * OWNER: Yes, STAFF: Yes (but may be limited by %)
     */
    public boolean canApplyDiscount() {
        return canView();
    }
    
    /**
     * Show error message when user doesn't have permission
     */
    public void showAccessDeniedAlert() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.WARNING
        );
        alert.setTitle("Access Denied");
        alert.setHeaderText(null);
        alert.setContentText("You do not have permission to perform this action. Please contact administrator.");
        alert.showAndWait();
    }
}

