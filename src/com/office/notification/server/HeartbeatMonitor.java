package com.office.notification.server;

public class HeartbeatMonitor implements Runnable {

    private final ClientManager clientManager;

    public HeartbeatMonitor(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @Override
    public void run() {
        System.out.println("Heartbeat Monitor Started");

        while (true) {
            System.out.println("Checking clients...");
            for (ClientHandler client : clientManager.getClients()) {
                System.out.println(
                        client.getClientName()
                                + " Alive : "
                                + client.isAliveClient()
                );

                if (!client.isAliveClient()) {

                    System.out.println(
                            client.getClientName() + " is inactive."
                    );
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;

            }
        }

    }
}
