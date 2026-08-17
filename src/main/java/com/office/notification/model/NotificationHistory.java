package com.office.notification.model;

public class NotificationHistory {
    private int id;
    private String message;
    private String priority;
    private String sender;
    private String recipient;
    private String sentAt;

    public NotificationHistory(int id, String message,String priority, String sender, String recipient, String sentAt) {

        this.id = id;
        this.message = message;
        this.priority = priority;
        this.sender = sender;
        this.recipient = recipient;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
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

    public String getRecipient() {
        return recipient;
    }

    public String getSentAt() {
        return sentAt;
    }
}
