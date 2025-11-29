package com.example.gympro.controller.settings;

import com.example.gympro.controller.base.BaseController;
import com.example.gympro.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Properties;

/**
 * Controller for "System" tab in Settings
 */
public class SystemInfoTabController extends BaseController {
    
    @FXML private Label lblAppVersion;
    @FXML private Label lblDbVersion;
    @FXML private Label lblDbInfo;
    
    @FXML
    public void initialize() {
        loadSystemInfo();
    }
    
    private void loadSystemInfo() {
        // Load app version from application.properties
        try {
            Properties prop = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            if (input != null) {
                prop.load(input);
                String version = prop.getProperty("app.version", "1.0.0");
                lblAppVersion.setText(version);
            } else {
                lblAppVersion.setText("1.0.0");
            }
        } catch (Exception e) {
            lblAppVersion.setText("1.0.0");
        }
        
        // Load database info
        try {
            Connection conn = DatabaseConnection.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String dbVersion = metaData.getDatabaseProductVersion();
            String dbUrl = metaData.getURL();
            
            lblDbVersion.setText("MySQL " + dbVersion);
            lblDbInfo.setText(dbUrl);
            conn.close();
        } catch (Exception e) {
            lblDbVersion.setText("MySQL 8.0");
            lblDbInfo.setText("gympro@localhost:3306");
        }
    }
}

