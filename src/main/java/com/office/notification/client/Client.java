package com.office.notification.client;

import com.office.notification.model.Message;
import com.office.notification.model.ClientRegistration;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;
import com.office.notification.util.AppConfig;
import com.office.notification.util.LoggerUtil;
import com.office.notification.util.AppConfig;
import org.slf4j.Logger;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private static Socket socket;

    private static boolean heartbeatStarted;
    private static HeartbeatSender heartbeatSender;
    private static Thread heartbeatThread;

    private static final Logger logger = LoggerUtil.getLogger(Client.class);

    public static void main(String[] args) {

        while (true) {

            try {

                connectToServer();

            } catch (Exception e) {

                logger.warn("Unable to connect to server. Retrying in 5 seconds.");

                try {

                    Thread.sleep(AppConfig.getInt("reconnect.delay"));

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();

                    logger.error("Reconnect thread was interrupted", ex);

                    break;
                }
            }
        }
    }

    private static void connectToServer() throws Exception {

        try {
            String host = AppConfig.get("server.host");
            int port = AppConfig.getInt("server.port");
            socket = new Socket(host,port);

            logger.info("Connecting to server {}:{}", host, port);

            outputStream =
                    new ObjectOutputStream(
                            socket.getOutputStream()
                    );

            outputStream.flush();

            inputStream =
                    new ObjectInputStream(
                            socket.getInputStream()
                    );

            ClientRegistration registration =
                    new ClientRegistration(
                            InetAddress.getLocalHost().getHostName(),
                            socket.getLocalAddress().getHostAddress(),
                            System.getProperty("user.name")
                    );

            Packet registerPacket =
                    new Packet(
                            PacketType.REGISTER,
                            registration
                    );

            outputStream.writeObject(registerPacket);
            outputStream.flush();

            while (true) {

                Packet packet =
                        (Packet) inputStream.readObject();

                switch (packet.getType()) {

                    case MESSAGE:

                        Message message =
                                (Message) packet.getPayload();

                        int messageType;

                        switch (message.getPriority()) {

                            case "Warning":
                                messageType =
                                        JOptionPane.WARNING_MESSAGE;
                                break;

                            case "Critical":
                                messageType =
                                        JOptionPane.ERROR_MESSAGE;
                                break;

                            default:
                                messageType =
                                        JOptionPane.INFORMATION_MESSAGE;
                        }

                        JOptionPane.showMessageDialog(
                                null,
                                message.getMessage(),
                                message.getPriority(),
                                messageType
                        );

                        logger.info(
                                "Notification received with priority: {}",
                                message.getPriority()
                        );

                        break;

                    case ACK:

                        String response = (String) packet.getPayload();

                        logger.info("Server registration response: {}", response);

                        if (!heartbeatStarted) {

                            HeartbeatSender heartbeatSender = new HeartbeatSender(outputStream);

                            heartbeatThread = new Thread(heartbeatSender);

                            heartbeatThread.setDaemon(true);

                            heartbeatThread.start();

                            heartbeatStarted = true;

                            logger.info("Heartbeat sender started");
                        }

                        break;

                    default:

                        logger.warn("Unknown packet received from server");

                        break;
                }
            }

        } finally {

            heartbeatStarted = false;

            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
                heartbeatThread = null;
            }

            heartbeatSender = null;

            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.debug("Error closing input stream", e);
                }

                inputStream = null;
            }

            if (outputStream != null) {

                try {
                    outputStream.close();
                } catch (Exception e) {
                    logger.debug("Error closing output stream", e);
                }

                outputStream = null;
            }

            if (socket != null && !socket.isClosed()) {

                try {
                    socket.close();
                } catch (Exception e) {
                    logger.debug("Error closing socket", e);
                }
            }

            socket = null;
        }
    }
}
