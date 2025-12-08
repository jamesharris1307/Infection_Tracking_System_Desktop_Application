package com.example.infection_monitoring_system_desktop_application.Model;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties config = new Properties();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            InputStream input = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("Config.properties");
            if (input == null) {
                throw new RuntimeException("Config.properties not found in resources folder");
            }
            config.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password")
        );
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connection successful!");
            } else {
                System.out.println("Failed to connect!");
            }
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}