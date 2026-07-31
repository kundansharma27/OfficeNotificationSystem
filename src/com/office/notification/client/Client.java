package com.office.notification.client;

import com.office.notification.model.Message;
import com.office.notification.model.ClientRegistration;
import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;

import java.io.ObjectOutputStream;
import java.net.InetAddress;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Client {

    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;

    public static void main(String[] args) {

        while (true) {

            try {

                connectToServer();

            } catch (Exception e) {

                System.out.println("Connection Lost!");
                System.out.println("Retrying in 5 seconds...");

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static void connectToServer() throws Exception {
        Socket socket = null;
        try {
             socket = new Socket("localhost", 5000);

            System.out.println("Connected to Server!");

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();

            inputStream = new ObjectInputStream(socket.getInputStream());

            ClientRegistration registration = new ClientRegistration(InetAddress.getLocalHost().getHostName(),

                    socket.getLocalAddress().getHostAddress(),

                    System.getProperty("user.name")
            );

            Packet registerPacket = new Packet(PacketType.REGISTER, registration);
            outputStream.writeObject(registerPacket);
            outputStream.flush();

            while (true) {

                Packet packet = (Packet) inputStream.readObject();

                switch (packet.getType()) {

                    case MESSAGE:

                        Message message = (Message) packet.getPayload();
                        int messageType;

                        switch (message.getPriority()) {

                            case "Warning":
                                messageType = JOptionPane.WARNING_MESSAGE;
                                break;

                            case "Critical":
                                messageType = JOptionPane.ERROR_MESSAGE;
                                break;

                            default:
                                messageType = JOptionPane.INFORMATION_MESSAGE;
                        }

                        JOptionPane.showMessageDialog(null, message.getMessage(), message.getPriority(), messageType);

                        // Show popup

                        break;

                    case ACK:

                        String response = (String) packet.getPayload();

                        System.out.println(response);

                        break;
                }
            }

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            System.out.println("Disconnected from Server.");
        }
    }
}