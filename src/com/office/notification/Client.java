package com.office.notification;

import java.io.*;
import java.net.Socket;

public class Client {
    static void main(String[] args) {
        try {

            Socket socket = new Socket("localhost",5000);
            System.out.println("Connected to Server!");

            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            writer.println("Hello Server");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = reader.readLine();
            System.out.println("Server says: "+ response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
