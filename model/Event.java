package model;

import java.time.LocalDateTime;

public class Event {

    private final String eventID;
    private final EventType eventType;
    private final String description;
    private final LocalDateTime timestamp;

    private final String transportID;
    private final String cargoID;

    public Event(String eventID, EventType eventType, String description,
                 String transportId, String cargoId) {

        this.eventID = eventID;
        this.eventType = eventType;
        this.description = description;
        this.transportID = transportId;
        this.cargoID = cargoId;
        this.timestamp = LocalDateTime.now();
    }

    public String getEventID() {
        return eventID;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTransportID() {
        return transportID;
    }

    public String getCargoID() {
        return cargoID;
    }
}