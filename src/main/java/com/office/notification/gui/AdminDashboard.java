package com.office.notification.gui;

import com.office.notification.listener.ClientListListener;
import com.office.notification.model.Message;
import com.office.notification.repository.NotificationRepository;
import com.office.notification.server.ClientHandler;
import com.office.notification.server.ClientManager;
import com.office.notification.service.NotificationService;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame implements ClientListListener {

    private final NotificationService notificationService;
    private final ClientManager clientManager;

    private JTextArea messageArea;
    private JComboBox<String> priorityBox;
    private JButton sendButton;
    private JButton historyButton;
    private JList<ClientHandler> clientList;
    private DefaultListModel<ClientHandler> clientListModel;
    private JCheckBox sendToAllCheckBox;

    private JLabel clientCountValue;
    private JLabel onlineCountValue;
    private JLabel sentTodayValue;

    private final NotificationRepository notificationRepository;

    public AdminDashboard(
            NotificationService notificationService,
            ClientManager clientManager
    ) {

        this.notificationService = notificationService;
        this.clientManager = clientManager;
        this.notificationRepository = new NotificationRepository();

        initializeComponents();
        setupLayout();
        setupActions();

        refreshClientList();

        setVisible(true);
    }

    @Override
    public void onClientListChanged() {
        SwingUtilities.invokeLater(() -> {
            refreshClientList();
            updateClientStats();
        });
    }

    private void initializeComponents() {

        setTitle("Office Notification System");
        setSize(1000, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Message area
        messageArea = new JTextArea(10, 40);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        // Priority
        priorityBox = new JComboBox<>(
                new String[]{
                        "Information",
                        "Warning",
                        "Critical"
                }
        );

        priorityBox.setPreferredSize(
                new Dimension(130, 38)
        );

        // Send button
        sendButton = new JButton("Send Notification");
        sendButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(
                new Dimension(160, 38)
        );
        priorityBox.addActionListener(
                e -> updatePriorityStyle()
        );

        updatePriorityStyle();

        // History button
        historyButton = new JButton("History");
        historyButton.setFocusPainted(false);
        historyButton.setPreferredSize(
                new Dimension(110, 38)
        );

        // Send to all
        sendToAllCheckBox =
                new JCheckBox("Send to All", true);

        sendToAllCheckBox.setFocusPainted(false);

        // Client list
        clientListModel =
                new DefaultListModel<>();

        clientList =
                new JList<>(clientListModel);

        setupClientList();
    }

    private void setupLayout() {

        setLayout(
                new BorderLayout(15, 15)
        );

        // =========================
        // HEADER
        // =========================

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout(15, 5)
                );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 15, 12, 15
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "🔔 Office Notification System"
                );

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        11
                )
        );

        JLabel statusLabel =
                new JLabel(
                        "● Server Online"
                );

        statusLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        headerPanel.add(
                statusLabel,
                BorderLayout.EAST
        );

        // =========================
        // STATS
        // =========================

        JPanel statsPanel =
                new JPanel(
                        new GridLayout(
                                1, 3, 12, 0
                        )
                );

        JPanel clientsCard =
                createStatCard(
                        "CONNECTED CLIENTS",
                        "0"
                );

        JPanel onlineCard =
                createStatCard(
                        "ONLINE",
                        "0"
                );

        JPanel sentCard =
                createStatCard(
                        "SENT TODAY",
                        "0"
                );

        clientCountValue =
                findValueLabel(clientsCard);

        onlineCountValue =
                findValueLabel(onlineCard);

        sentTodayValue =
                findValueLabel(sentCard);

        statsPanel.add(clientsCard);
        statsPanel.add(onlineCard);
        statsPanel.add(sentCard);

        // =========================
        // TOP PANEL
        // =========================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        topPanel.add(
                statsPanel,
                BorderLayout.SOUTH
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        // =========================
        // MESSAGE PANEL
        // =========================

        JPanel messagePanel =
                new JPanel(
                        new BorderLayout(
                                10, 10
                        )
                );

        JLabel messageTitle =
                new JLabel(
                        "Send Notification"
                );

        messageTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        messagePanel.add(
                messageTitle,
                BorderLayout.NORTH
        );

        messagePanel.add(
                new JScrollPane(messageArea),
                BorderLayout.CENTER
        );

        JPanel controlsPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                8
                        )
                );

        controlsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        5, 0, 0, 0
                )
        );

        controlsPanel.add(priorityBox);
        controlsPanel.add(sendToAllCheckBox);
        controlsPanel.add(sendButton);
        controlsPanel.add(historyButton);

        messagePanel.add(
                controlsPanel,
                BorderLayout.SOUTH
        );

        // =========================
        // CLIENT PANEL
        // =========================

        JPanel clientPanel =
                new JPanel(
                        new BorderLayout(
                                10, 10
                        )
                );

        clientPanel.setPreferredSize(
                new Dimension(270, 0)
        );

        JLabel clientTitle =
                new JLabel(
                        "Connected Clients"
                );

        clientTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        clientPanel.add(
                clientTitle,
                BorderLayout.NORTH
        );

        clientPanel.add(
                new JScrollPane(clientList),
                BorderLayout.CENTER
        );

        // =========================
        // MAIN PANEL
        // =========================

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                15, 15
                        )
                );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 15, 15, 15
                )
        );

        mainPanel.add(
                messagePanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                clientPanel,
                BorderLayout.EAST
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }

    private void setupActions() {

        // Send button
        sendButton.addActionListener(e -> {

            String message =
                    messageArea.getText().trim();

            if (message.isBlank()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a message."
                );

                return;
            }

            String priority =
                    (String) priorityBox.getSelectedItem();

            Message notification =
                    new Message(
                            message,
                            priority,
                            "Admin"
                    );

            if (sendToAllCheckBox.isSelected()) {

                notificationService
                        .sendNotification(notification);

            } else {

                ClientHandler selectedClient =
                        clientList.getSelectedValue();

                if (selectedClient == null) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Please select a client."
                    );

                    return;
                }

                notificationService
                        .sendNotification(
                                selectedClient,
                                notification
                        );
            }

            messageArea.setText("");
            messageArea.requestFocusInWindow();
        });

        // History button
        historyButton.addActionListener(e -> {

            NotificationRepository repository =
                    new NotificationRepository();

            new HistoryWindow(repository);
        });
    }

    private void setupClientList() {

        clientList.setFixedCellHeight(65);

        clientList.setCellRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel label =
                                (JLabel)
                                        super.getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        ClientHandler client =
                                (ClientHandler) value;

                        String name =
                                client.getClientName();

                        label.setText(
                                "<html>"
                                        + "<b>● "
                                        + name
                                        + "</b>"
                                        + "<br>"
                                        + "&nbsp;&nbsp;&nbsp;"
                                        + "<font color='#666666'>"
                                        + "Online • Connected"
                                        + "</font>"
                                        + "</html>"
                        );

                        label.setFont(
                                new Font(
                                        "SansSerif",
                                        Font.PLAIN,
                                        14
                                )
                        );

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        6, 10, 6, 10
                                )
                        );

                        return label;
                    }
                }
        );
    }

    private JPanel createStatCard(
            String title,
            String value
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout(
                                5, 5
                        )
                );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 225, 225)),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16))
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

        JLabel valueLabel =
                new JLabel(value);

        valueLabel.setFont(
                new Font("SansSerif", Font.BOLD, 26));

        card.add(
                titleLabel,
                BorderLayout.NORTH
        );

        card.add(
                valueLabel,
                BorderLayout.CENTER
        );

        return card;
    }

    private JLabel findValueLabel(
            JPanel card
    ) {

        Component component =
                card.getComponent(
                        card.getComponentCount() - 1
                );

        return (JLabel) component;
    }

    private void updateClientStats() {

        int count =
                clientManager.getClientCount();

        int todayCount =
                notificationRepository
                        .getTodayNotificationCount();

        if (clientCountValue != null) {
            clientCountValue.setText(
                    String.valueOf(count)
            );
        }

        if (onlineCountValue != null) {
            onlineCountValue.setText(
                    String.valueOf(count)
            );
        }

        if (sentTodayValue != null) {
            sentTodayValue.setText(
                    String.valueOf(todayCount)
            );
        }
    }
    private void updatePriorityStyle() {

        String priority =
                (String) priorityBox.getSelectedItem();

        if ("Critical".equals(priority)) {

            sendButton.setText("Send Critical Alert");

        } else if ("Warning".equals(priority)) {

            sendButton.setText("Send Warning");

        } else {

            sendButton.setText("Send Notification");
        }
    }

    private void refreshClientList() {

        clientListModel.clear();

        for (
                ClientHandler client
                : clientManager.getClients()
        ) {

            if (client.getClientName() != null) {

                clientListModel.addElement(
                        client
                );
            }
        }

        updateClientStats();
    }
}