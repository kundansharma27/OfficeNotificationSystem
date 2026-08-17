package com.office.notification.repository;

import com.office.notification.database.DatabaseManager;
import com.office.notification.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import com.office.notification.model.NotificationHistory;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    public void saveNotification(Message message, String recipient) {
        String sql = """
                INSERT INTO notification_history
                (message, priority, sender, recipient, sent_at)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, message.getMessage());
            statement.setString(2, message.getPriority());
            statement.setString(3, message.getSender());
            statement.setString(4, recipient);
            statement.setString(5, LocalDateTime.now().toString());

            statement.executeUpdate();

            System.out.println("Notification saved to database.");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
    public List<NotificationHistory> findAllNotifications() {

        List<NotificationHistory> historyList = new ArrayList<>();

        String sql = """
            SELECT id, message, priority, sender, recipient, sent_at
            FROM notification_history
            ORDER BY id DESC
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                NotificationHistory history =
                        new NotificationHistory(
                                resultSet.getInt("id"),
                                resultSet.getString("message"),
                                resultSet.getString("priority"),
                                resultSet.getString("sender"),
                                resultSet.getString("recipient"),
                                resultSet.getString("sent_at")
                        );

                historyList.add(history);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return historyList;
    }
    public int getTodayNotificationCount() {

        String sql = """
            SELECT COUNT(*)
            FROM notification_history
            WHERE date(sent_at) = date('now', 'localtime')
            """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             var resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
