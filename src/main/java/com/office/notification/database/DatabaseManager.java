package com.office.notification.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static final String URL = "jdbc:sqlite:notification.db";

    public static Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {

        String sql = """
                CREATE TABLE IF NOT EXISTS notification_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    message TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    sender TEXT NOT NULL,
                    recipient TEXT NOT NULL,
                    sent_at TEXT NOT NULL
                )
                """;

        try (Connection connection = getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate(sql);

            System.out.println("Notification history table created successfully!"   );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        try (Connection connection = getConnection()) {

            System.out.println("Database connected successfully!");

        } catch (SQLException e) {

            e.printStackTrace();
        }

        createTables();
    }
}