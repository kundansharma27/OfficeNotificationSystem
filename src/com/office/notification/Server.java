package com.office.notification;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            System.out.println("Server Started...");
            System.out.println("Waiting for client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
