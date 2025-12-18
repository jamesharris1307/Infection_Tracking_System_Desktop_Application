package com.example.infection_monitoring_system_desktop_application.Util;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

public final class DatabaseConnection {
    private DatabaseConnection() {}

    private static final Properties CONFIG = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("Config.properties")) {
            if (input == null) {
                throw new RuntimeException("Config.properties not found in resources folder");
            }
            CONFIG.load(input);

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = CONFIG.getProperty("db.url");
        String user = CONFIG.getProperty("db.user");
        String password = CONFIG.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException("Database credentials not configured properly");
        }

        return DriverManager.getConnection(url, user, password);
    }
}


