package com.office.notification;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;

    public  ClientHandler(Socket socket) {
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = reader.readLine();
            System.out.println("Message: "+ message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
