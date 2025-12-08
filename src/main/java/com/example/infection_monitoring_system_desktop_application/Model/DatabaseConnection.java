package com.example.infection_monitoring_system_desktop_application.Model;

import javafx.application.Platform;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DatabaseConnection {

    private static final Properties config = new Properties();
    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(10);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            InputStream input = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("Config.properties");

            if (input == null) {
                throw new RuntimeException("Config.properties not found");
            }
            config.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password")
        );
    }

    public static <T> void runAsync(DbTask<T> task,
                                    Consumer<T> onSuccess,
                                    Consumer<Exception> onError) {

        dbExecutor.submit(() -> {
            try (Connection conn = getConnection()) {
                T result = task.execute(conn);
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Exception e) {
                Platform.runLater(() -> onError.accept(e));
            }
        });
    }

    public interface DbTask<T> {
        T execute(Connection conn) throws Exception;
    }
}

