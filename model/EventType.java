package model;

public enum EventType {

    // Cargo
    CARGO_LOADED("Load Cargo", EventCategory.CLIENT, EventGroup.CARGO),
    CARGO_UNLOADED("Unload Cargo", EventCategory.CLIENT, EventGroup.CARGO),
    CARGO_TRANSFERRED("Transfer Cargo", EventCategory.CLIENT, EventGroup.CARGO),

    // Docking Bays
    BAY_ALLOCATED("Allocate Docking Bay", EventCategory.CLIENT, EventGroup.DOCKING_BAYS),
    BAY_EXCHANGED("Exchange Docking Bay", EventCategory.CLIENT, EventGroup.DOCKING_BAYS),
    BAY_CLOSED("Close Docking Bay", EventCategory.ADMINISTRATOR, EventGroup.DOCKING_BAYS),
    BAY_OPENED("Reopen Docking Bay", EventCategory.ADMINISTRATOR, EventGroup.DOCKING_BAYS),

    // Maintenance
    MAINTENANCE_ASSIGNED("Assign Maintenance Team", EventCategory.CLIENT, EventGroup.MAINTENANCE),
    MAINTENANCE_COMPLETED("Complete Maintenance", EventCategory.CLIENT, EventGroup.MAINTENANCE),
    MAINTENANCE_DECOMMISSIONED("Decommission Maintenance Team", EventCategory.ADMINISTRATOR, EventGroup.MAINTENANCE
),

    // Transports
    TRANSPORT_CREATED("Add Transport", EventCategory.ADMINISTRATOR, EventGroup.TRANSPORTS),
    TRANSPORT_DEPARTED("Depart Transport", EventCategory.SYSTEM, EventGroup.TRANSPORTS);

    private final String displayName;
    private final EventCategory category;
    private final EventGroup group;

    EventType(String displayName, EventCategory category, EventGroup group) {
        this.displayName = displayName;
        this.category = category;
        this.group = group;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EventCategory getCategory() {
        return category;
    }

    public EventGroup getGroup() {
        return group;
    }
}