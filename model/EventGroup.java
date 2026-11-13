package model;

public enum EventGroup {
    CARGO("Cargo"),
    DOCKING_BAYS("Docking Bays"),
    MAINTENANCE("Maintenance"),
    TRANSPORTS("Transports"),
    CONNECTIONS("Connections");

    private final String displayName;

    EventGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}