package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {

    // Defines the serialization version for compatibility when sending objects between client and server.
    private static final long serialVersionUID = 1L;
    private final String notificationID;
    private final String message;
    private final RequestType requestType;
    private final LocalDateTime timestamp;

    public Notification(String notificationID, String message, RequestType requestType) {
        this.notificationID = notificationID;
        this.message = message;
        this.requestType = requestType;
        this.timestamp = LocalDateTime.now();
    }

    public String getNotificationID() {
        return notificationID;
    }

    public String getMessage() {
        return message;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
