package com.office.notification.server;
import com.formdev.flatlaf.FlatLightLaf;
import com.office.notification.service.NotificationService;
import com.office.notification.gui.AdminDashboard;
import java.net.ServerSocket;
import java.net.Socket;

import com.office.notification.util.AppConfig;
import org.slf4j.Logger;
import com.office.notification.util.LoggerUtil;
import java.io.IOException;


public class Server {

    private static final ClientManager clientManager = new ClientManager();
    private static final NotificationService notificationService = new NotificationService(clientManager);
    private static AdminDashboard dashboard;

    private static final Logger logger =
            LoggerUtil.getLogger(Server.class);

    public static void main(String[] args) {
        FlatLightLaf.setup();
        try {
            int port = AppConfig.getInt("server.port");
            ServerSocket serverSocket = new ServerSocket(port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {

                logger.info("Server shutting down...");

                try {
                    serverSocket.close();
                    logger.info("Server socket closed.");

                } catch (IOException e) {
                    logger.error("Error while closing server socket", e);
                }
            }));

            logger.info("Server started on port {}", port);

            dashboard = new AdminDashboard(notificationService, clientManager);

            dashboard.onServerStatusChanged(true);
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

        }  catch (java.net.SocketException e) {

        if (e.getMessage().equals("Socket closed")) {
            if (dashboard != null) {
                dashboard.onServerStatusChanged(false);
            }
            logger.info("Server stopped gracefully.");
        } else {
            logger.error("Server socket error occurred", e);
        }

    } catch (Exception e) {

        logger.error("Server error occurred", e);
    }
        }
    }


