package com.office.notification.client;

import com.office.notification.model.Message;
import com.office.notification.model.ClientRegistration;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;
import com.office.notification.util.LoggerUtil;
import org.slf4j.Logger;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;

    private static boolean heartbeatStarted;

    private static final Logger logger =
            LoggerUtil.getLogger(Client.class);

    public static void main(String[] args) {

        while (true) {

            try {

                connectToServer();

            } catch (Exception e) {

                logger.warn("Unable to connect to server. Retrying in 5 seconds.");

                try {

                    Thread.sleep(5000);

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();

                    logger.error("Reconnect thread was interrupted", ex);

                    break;
                }
            }
        }
    }

    private static void connectToServer() throws Exception {

        Socket socket = null;

        try {

            socket = new Socket("localhost", 5000);

            logger.info("Connected to server");

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

                        String response =
                                (String) packet.getPayload();

                        logger.info(
                                "Server registration response: {}",
                                response
                        );

                        if (!heartbeatStarted) {

                            HeartbeatSender heartbeatSender =
                                    new HeartbeatSender(outputStream);

                            Thread heartbeatThread =
                                    new Thread(heartbeatSender);

                            heartbeatThread.setDaemon(true);

                            heartbeatThread.start();

                            heartbeatStarted = true;

                            logger.info(
                                    "Heartbeat sender started"
                            );
                        }

                        break;

                    default:

                        logger.warn(
                                "Unknown packet received from server"
                        );

                        break;
                }
            }

        } finally {

            if (inputStream != null) {

                inputStream.close();
                inputStream = null;
            }

            if (outputStream != null) {

                outputStream.close();
                outputStream = null;
            }

            if (socket != null && !socket.isClosed()) {

                socket.close();
            }

            heartbeatStarted = false;


        }
    }
}