package com.office.notification.server;

import com.office.notification.model.ClientRegistration;
import com.office.notification.model.Message;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.office.notification.protocol.PacketType.REGISTER;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final ClientManager clientManager;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String clientName;

    public ClientHandler(Socket socket, ClientManager clientManager) {
        this.socket = socket;
        this.clientManager = clientManager;

        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(
                    socket.getInputStream()
            );

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
        Packet packet;

        try {
             packet = (Packet) inputStream.readObject();
        } catch (IOException| ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        switch (packet.getType()) {
            case REGISTER:

            ClientRegistration registration =
                    (ClientRegistration) packet.getPayload();

            clientName = registration.getComputerName();

            clientManager.addClient(this);

            System.out.println("Client Registered : " + clientName);


            Packet ackPacket = new Packet(PacketType.ACK, "Registration Successful");
            sendPacket(ackPacket);

            break;

            default:
                System.out.println("Unknown Packet");
        }

        try {
            while (!socket.isClosed()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {

            clientManager.removeClient(this);

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Client Disconnected");
            System.out.println("Remaining Clients : "
                    + clientManager.getClientCount());
        }
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public String toString() {
        return clientName;
    }
}