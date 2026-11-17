package model;

import java.time.LocalDateTime;
import java.util.Map;

public class Event {

    private final String eventID;
    private final RequestType eventType;
    private final String description;
    private final LocalDateTime timestamp;

    private final User user;
    private final Map<String, String> associatedIDs;

    public Event(String eventID, RequestType eventType, User user, Map<String, String> associatedIDs) {

        this.eventID = eventID;
        this.eventType = eventType;
        this.user = user;
        this.associatedIDs = associatedIDs == null ? Map.of() : Map.copyOf(associatedIDs);
        this.timestamp = LocalDateTime.now();
        this.description = eventType.getCategory() == EventCategory.CONNECTION
            ? user.getUsername() + " (" + user.getRole().getDisplayName() + ") " + eventType.getDisplayName().toLowerCase()
            : eventType.getDisplayName();
    }

    public String getEventID() {
        return eventID;
    }

    public RequestType getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public User getUser() {
        return user;
    }

    public Map<String, String> getAssociatedIDs() {
        return associatedIDs;
    }
}