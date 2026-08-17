package com.office.notification.client;

import com.office.notification.protocol.Packet;
import com.office.notification.protocol.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectOutputStream;

public class HeartbeatSender implements Runnable {
    private final ObjectOutputStream outputStream;

    public HeartbeatSender(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }
    private static final Logger logger =
            LoggerFactory.getLogger(HeartbeatSender.class);

    @Override
    public void run() {

        while (true) {

            try {
                Thread.sleep(3000);

                Packet heartbeatPacket = new Packet(PacketType.HEARTBEAT, null);
                outputStream.writeObject(heartbeatPacket);
                outputStream.flush();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.warn("Heartbeat sender stopped because connection was closed.");
                break;

            }
        }
    }
}
