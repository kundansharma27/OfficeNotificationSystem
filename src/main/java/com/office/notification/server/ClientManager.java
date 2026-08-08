package com.office.notification.server;

import com.office.notification.listener.ClientListListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientManager {

    private final List<ClientHandler> clients =
            new CopyOnWriteArrayList<>();

    private ClientListListener listener;

    public void setClientListListener(ClientListListener listener) {
        this.listener = listener;
    }

    public void addClient(ClientHandler client) {

        clients.add(client);

        if (listener != null) {
            listener.onClientListChanged();
        }
    }

    public void removeClient(ClientHandler client) {

        clients.remove(client);

        if (listener != null) {
            listener.onClientListChanged();
        }
    }

    public int getClientCount() {
        return clients.size();
    }

    public List<ClientHandler> getClients() {
        return clients;
    }
}