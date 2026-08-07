package com.office.notification.server;

import com.office.notification.model.Message;
import com.office.notification.service.NotificationService;
import com.office.notification.gui.AdminDashboard;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final ClientManager clientManager = new ClientManager();
    private static final NotificationService notificationService = new NotificationService(clientManager);

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Server Started...");
          AdminDashboard dashboard =  new AdminDashboard(notificationService, clientManager);
          clientManager.setClientListListener(dashboard);

            HeartbeatMonitor heartbeatMonitor = new HeartbeatMonitor(clientManager);

            Thread heartbeatThread = new Thread(heartbeatMonitor);

            heartbeatThread.setDaemon(true);

            heartbeatThread.start();

            while (true) {

                Socket socket = serverSocket.accept();
                ClientHandler client = new ClientHandler(socket, clientManager);
                client.start();
               // notificationService.sendNotification(new Message("Welcome New User!", "Information", "Server"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

