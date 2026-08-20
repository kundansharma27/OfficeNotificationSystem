# Office Notification System

A Java-based desktop notification system designed to broadcast notifications from an administrator to multiple client computers over a local network.

The system uses socket programming for client-server communication and supports client registration, heartbeat monitoring, automatic reconnection, notification priorities, notification history, logging, and a Swing-based admin dashboard.

---

## Features

- Client-server communication using Java Sockets
- Multiple simultaneous client connections
- Client registration with computer information
- Broadcast notifications to all connected clients
- Send notifications to a specific client
- Notification priorities:
    - Information
    - Warning
    - Critical
- Heartbeat mechanism for client health monitoring
- Automatic client reconnection
- Automatic client removal after disconnect
- Real-time connected-client list in Admin Dashboard
- Notification history stored in SQLite
- Notification history viewer
- SLF4J + Logback application logging
- External configuration using `server.properties`
- Maven-based project
- FlatLaf-based Swing UI
- Packaged JAR deployment

---

## Architecture

```text
                         ┌─────────────────────┐
                         │   Admin Dashboard   │
                         │      (Swing)        │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │ NotificationService │
                         └──────────┬──────────┘
                                    │
                     ┌──────────────┴──────────────┐
                     │                             │
                     ▼                             ▼
              ┌──────────────┐            ┌──────────────────┐
              │ ClientManager│            │ Notification     │
              │              │            │ Repository       │
              └──────┬───────┘            └────────┬─────────┘
                     │                             │
          ┌──────────┼──────────┐                  ▼
          ▼          ▼          ▼             ┌─────────┐
      Client 1   Client 2   Client N          │ SQLite  │
                                             └─────────┘