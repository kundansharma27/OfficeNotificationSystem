package com.office.notification.server;

import com.office.notification.service.NotificationService;
import com.office.notification.gui.AdminDashboard;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import com.office.notification.util.LoggerUtil;

public class Server {

    private static final ClientManager clientManager = new ClientManager();
    private static final NotificationService notificationService = new NotificationService(clientManager);

    private static final Logger logger =
            LoggerUtil.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            logger.info("Server Started...");
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
                logger.info("New client connection accepted from {}",socket.getInetAddress());
                logger.info("Total Clients: {}", clientManager.getClientCount());
               // notificationService.sendNotification(new Message("Welcome New User!", "Information", "Server"));

            }

        } catch (Exception e) {
            logger.error("Server error occurred", e);
        }
    }
}

