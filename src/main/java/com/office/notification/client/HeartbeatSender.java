package com.office.notification.client;

import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;

import java.io.ObjectOutputStream;
import java.io.OutputStream;

import static java.io.OutputStream.*;

public class HeartbeatSender implements Runnable {
    private final ObjectOutputStream outputStream;

    public HeartbeatSender(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void run() {

        while (true) {

            try {
                Thread.sleep(3000);

                Packet heartbeatPacket = new Packet(PacketType.HEARTBEAT, null);
                outputStream.writeObject(heartbeatPacket);
                outputStream.flush();

                System.out.println("Heartbeat Sent");

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {

                e.printStackTrace();
                break;

            }
        }
    }
}

