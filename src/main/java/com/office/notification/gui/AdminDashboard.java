package com.office.notification.gui;
import com.office.notification.listener.ClientListListener;
import com.office.notification.model.Message;
import com.office.notification.server.ClientHandler;
import com.office.notification.server.ClientManager;
import com.office.notification.service.NotificationService;
import javax.swing.*;
import java.awt.*;


public class AdminDashboard extends JFrame implements ClientListListener {
    @Override
    public void onClientListChanged(){
        SwingUtilities.invokeLater(() ->  refreshClientList());
    }
    private final NotificationService notificationService;
    private final ClientManager clientManager;


    private JTextArea messageArea;
    private JComboBox<String> priorityBox;
    private JButton sendButton;
    private JList<ClientHandler> clientList;
    private DefaultListModel<ClientHandler> clientListModel;
    private JCheckBox sendToAllCheckBox;


    public AdminDashboard(NotificationService notificationService, ClientManager clientManager) {

        this.clientManager = clientManager;

        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);


        this.notificationService = notificationService;
        setTitle("Office Notification System");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         messageArea = new JTextArea(5 , 30 );

         priorityBox = new JComboBox<>(new String[]{
                 "Information",
                 "Warning",
                 "Critical"
         });
        sendButton = new JButton("Send");
        sendToAllCheckBox = new JCheckBox("Send to All",true);
        sendButton.addActionListener(e -> {
            String message = messageArea.getText().trim();

            if (message.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please enter a message.");
                return;
            }

            String priority = (String) priorityBox.getSelectedItem();
            Message notification = new Message(message, priority, "Admin");



            if(sendToAllCheckBox.isSelected()) {
                notificationService.sendNotification(notification);
            } else {
                ClientHandler selectedClient = clientList.getSelectedValue();
                if (selectedClient == null) {
                    JOptionPane.showMessageDialog(this, "Please select a client.");
                    return;
                }

                notificationService.sendNotification(selectedClient, notification);            }

            messageArea.setText("");
            messageArea.requestFocus();

        });

        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        leftPanel.setLayout(new GridLayout(4, 1, 10, 10));

        JScrollPane scrollPane = new JScrollPane(clientList);

        leftPanel.add(messageArea);
        leftPanel.add(priorityBox);
        leftPanel.add(sendToAllCheckBox);
        leftPanel.add(sendButton);

        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        refreshClientList();

        setVisible(true);
    }

    private void refreshClientList() {

            clientListModel.clear();

            for (ClientHandler client : clientManager.getClients()) {


                if (client.getClientName() != null) {
                    clientListModel.addElement(client);
                }
            }
        }
    }


