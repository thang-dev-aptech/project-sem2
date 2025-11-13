package com.example.gympro.utils;

import java.io.InputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing database connections
 * Loads configuration from application.properties
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private static  String url;
    private static  String username;
    private static  String password;
    private final String driver;
    private final String schema;

    private DatabaseConnection() {
        Properties prop = new Properties();
        
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties not found in classpath! Make sure it's in src/main/resources/");
            }
            
            prop.load(input);
            
            // Load database configuration
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
            driver = prop.getProperty("db.driver");
            schema = prop.getProperty("db.schema", "gympro");
            
            // Load JDBC driver
            Class.forName(driver);
            
            // Test connection
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                System.out.println("✓ Connected to database: " + conn.getMetaData().getURL());
                System.out.println("✓ Database: " + schema);
            }
            
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database connection initialization failed", e);
        }
    }

    /**
     * Get singleton instance of DatabaseConnection
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Get a new database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        // Ensure instance is initialized before using static variables
        if (instance == null) {
            getInstance();
        }
        if (url == null || username == null || password == null) {
            throw new SQLException("Database connection not initialized. Call getInstance() first.");
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Get database schema name
     * @return Schema name
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Test database connection
     * @return true if connection is valid, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}