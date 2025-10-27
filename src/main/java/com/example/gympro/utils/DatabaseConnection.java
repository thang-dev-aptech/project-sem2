package com.example.gympro.utils;

import java.io.InputStream;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private String url;
    private String username;
    private String password;
    private String driver;

    private DatabaseConnection() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            url = prop.getProperty("db.url");
            username = prop.getProperty("db.username");
            password = prop.getProperty("db.password");
            driver = prop.getProperty("db.driver");
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                System.out.println(" Connected to database: " + conn.getMetaData().getURL());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

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

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

}