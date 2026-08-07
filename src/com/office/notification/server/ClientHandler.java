package com.office.notification.server;

import com.office.notification.model.ClientRegistration;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final ClientManager clientManager;

    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private String clientName;
    private LocalDateTime lastHeartbeat;

    public ClientHandler(Socket socket, ClientManager clientManager) {
        this.socket = socket;
        this.clientManager = clientManager;

        try {

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet) {

        try {

            outputStream.writeObject(packet);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {

            // Registration Packet
            Packet packet = (Packet) inputStream.readObject();

            if (packet.getType() == PacketType.REGISTER) {

                ClientRegistration registration =
                        (ClientRegistration) packet.getPayload();

                clientName = registration.getComputerName();

                lastHeartbeat = LocalDateTime.now();

                clientManager.addClient(this);

                System.out.println("Client Registered : " + clientName);

                Packet ackPacket = new Packet(
                        PacketType.ACK,
                        "Registration Successful"
                );

                sendPacket(ackPacket);
            }

            // Future packets
            while (!socket.isClosed()) {

                Packet incomingPacket =
                        (Packet) inputStream.readObject();

                switch (incomingPacket.getType()) {

                    case HEARTBEAT:

                        lastHeartbeat = LocalDateTime.now();

                        System.out.println(
                                "Heartbeat Received : "
                                        + clientName
                        );

                        break;

                    default:
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {

            System.out.println(clientName + " disconnected.");

        } finally {

            clientManager.removeClient(this);

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(
                    "Remaining Clients : "
                            + clientManager.getClientCount()
            );
        }
    }

    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }

    public String getClientName() {
        return clientName;
    }
    public boolean isAliveClient() {
        return lastHeartbeat != null && lastHeartbeat.isAfter(LocalDateTime.now().minusSeconds(60));
    }

    @Override
    public String toString() {
        return clientName;
    }
}