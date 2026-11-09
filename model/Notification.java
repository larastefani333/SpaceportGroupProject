package model;

import java.time.LocalDateTime;

public class Notification {

    private final String notificationID;
    private final String message;
    private final EventType eventType;
    private final LocalDateTime timestamp;

    public Notification(String notificationID, String message, EventType eventType) {
        this.notificationID = notificationID;
        this.message = message;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }

    public String getNotificationID() {
        return notificationID;
    }

    public String getMessage() {
        return message;
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}