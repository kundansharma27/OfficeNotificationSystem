package com.office.notification.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final String priority;
    private final String sender;
    private final LocalDateTime timestamp;

    public Message(String message, String priority, String sender) {
        this.message = message;
        this.priority = priority;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
    public String getPriority() {
        return priority;
    }
    public String getSender() {
        return sender;
    }
}
