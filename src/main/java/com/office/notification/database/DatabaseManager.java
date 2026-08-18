package com.office.notification.database;

import com.office.notification.util.LoggerUtil;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final Logger logger =
            LoggerUtil.getLogger(DatabaseManager.class);
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

            logger.info("Notification history table is ready");

        } catch (SQLException e) {
            logger.error("Database operation failed", e);
        }
    }
}