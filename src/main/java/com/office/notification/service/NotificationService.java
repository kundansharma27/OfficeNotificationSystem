package com.office.notification.service;

import com.office.notification.model.Message;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;
import com.office.notification.server.ClientHandler;
import com.office.notification.server.ClientManager;

public class NotificationService {

    private final ClientManager clientManager;

    public NotificationService(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public void sendNotification(Message message) {

        for (ClientHandler client : clientManager.getClients()) {
            Packet packet = new Packet(PacketType.MESSAGE,message);
            client.sendPacket(packet);
        }

        System.out.println("Notification Sent: " + message.getMessage());
    }

    public void sendNotification(ClientHandler client, Message message) {

        if (client != null) {
            Packet packet = new Packet(PacketType.MESSAGE, message);

            client.sendPacket(packet);

            System.out.println(
                    "Notification Sent to "
                            + client.getClientName()
                            + ": "
                            + message.getMessage()
            );
        }
    }
}