package com.office.notification.gui;

import com.office.notification.model.NotificationHistory;
import com.office.notification.repository.NotificationRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryWindow extends JFrame {

    private final NotificationRepository repository;
    private final DefaultTableModel tableModel;
    private JButton refreshButton;

    public HistoryWindow(NotificationRepository repository) {

        this.repository = repository;

        setTitle("Notification History");
        setSize(800, 400);
        setLocationRelativeTo(null);

        String[] columns = {
                "ID",
                "Message",
                "Priority",
                "Sender",
                "Recipient",
                "Sent At"
        };

        tableModel = new DefaultTableModel(columns, 0);

        JTable historyTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        refreshButton = new JButton("Refresh");

        refreshButton.addActionListener(e -> loadHistory());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        loadHistory();

        setVisible(true);
    }

    private void loadHistory() {

        tableModel.setRowCount(0);

        List<NotificationHistory> history = repository.findAllNotifications();

        for (NotificationHistory notification : history) {

            tableModel.addRow(new Object[]{
                    notification.getId(),
                    notification.getMessage(),
                    notification.getPriority(),
                    notification.getSender(),
                    notification.getRecipient(),
                    notification.getSentAt()
            });
        }
    }
}