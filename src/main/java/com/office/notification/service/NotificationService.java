package com.office.notification.service;

import com.office.notification.model.Message;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;
import com.office.notification.server.ClientHandler;
import com.office.notification.server.ClientManager;
import com.office.notification.util.LoggerUtil;
import org.slf4j.Logger;

public class NotificationService {

    private final ClientManager clientManager;

    private static final Logger logger =
            LoggerUtil.getLogger(NotificationService.class);

    public NotificationService(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public void sendNotification(Message message) {

        Packet packet = new Packet(PacketType.MESSAGE, message);

        for (ClientHandler client : clientManager.getClients()) {

            client.sendPacket(packet);
        }

        logger.info("Notification sent to all clients. Priority: {}", message.getPriority());
    }

    public void sendNotification(ClientHandler client, Message message) {

        if (client != null) {

            Packet packet = new Packet(PacketType.MESSAGE, message);

            client.sendPacket(packet);

            logger.info("Notification sent to client: {}. Priority: {}", client.getClientName(), message.getPriority());
        }
    }
}