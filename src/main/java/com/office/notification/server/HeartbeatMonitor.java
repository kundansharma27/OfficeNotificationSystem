package com.office.notification.server;

import com.office.notification.util.LoggerUtil;
import org.slf4j.Logger;

public class HeartbeatMonitor implements Runnable {

    private final ClientManager clientManager;

    private static final Logger logger =
            LoggerUtil.getLogger(HeartbeatMonitor.class);

    public HeartbeatMonitor(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void run() {

        logger.info("Heartbeat monitor started");

        while (true) {

            try {

                for (ClientHandler client : clientManager.getClients()) {

                    if (!client.isAliveClient()) {

                        logger.warn(
                                "Client appears inactive: {}",
                                client.getClientName()
                        );
                    }
                }

                Thread.sleep(10000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                logger.info(
                        "Heartbeat monitor interrupted"
                );

                break;

            } catch (Exception e) {

                logger.error("Error occurred in heartbeat monitor", e);
            }
        }
    }
}