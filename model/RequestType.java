package model;

public enum RequestType {

    CARGO_LOADED("Load Cargo", EventCategory.CLIENT, EventGroup.CARGO),
    CARGO_UNLOADED("Unload Cargo", EventCategory.CLIENT, EventGroup.CARGO),
    CARGO_TRANSFERRED("Transfer Cargo", EventCategory.CLIENT, EventGroup.CARGO),

    BAY_ALLOCATED("Allocate Docking Bay", EventCategory.CLIENT, EventGroup.DOCKING_BAYS),
    BAY_EXCHANGED("Exchange Docking Bay", EventCategory.CLIENT, EventGroup.DOCKING_BAYS),
    BAY_CLOSED("Close Docking Bay", EventCategory.ADMINISTRATOR, EventGroup.DOCKING_BAYS),
    BAY_OPENED("Reopen Docking Bay", EventCategory.ADMINISTRATOR, EventGroup.DOCKING_BAYS),

    MAINTENANCE_ASSIGNED("Assign Maintenance Team", EventCategory.CLIENT, EventGroup.MAINTENANCE),
    MAINTENANCE_COMPLETED("Complete Maintenance", EventCategory.CLIENT, EventGroup.MAINTENANCE),
    MAINTENANCE_DECOMMISSIONED("Decommission Maintenance Team", EventCategory.ADMINISTRATOR, EventGroup.MAINTENANCE),

    TRANSPORT_CREATED("Add Transport", EventCategory.ADMINISTRATOR, EventGroup.TRANSPORTS),
    TRANSPORT_DEPARTED("Depart Transport", EventCategory.SYSTEM, EventGroup.TRANSPORTS),

    CLIENT_CONNECTED("Client Connected", EventCategory.CONNECTION, EventGroup.CONNECTIONS),
    CLIENT_DISCONNECTED("Client Disconnected", EventCategory.CONNECTION, EventGroup.CONNECTIONS),
    ADMIN_CONNECTED("Administrator Connected", EventCategory.CONNECTION, EventGroup.CONNECTIONS),
    ADMIN_DISCONNECTED("Administrator Disconnected", EventCategory.CONNECTION, EventGroup.CONNECTIONS),

    CONNECTED_USERS_VIEWED("View Connected Users", EventCategory.ADMINISTRATOR, EventGroup.CONNECTIONS, false, false);

    private final String displayName;
    private final EventCategory category;
    private final EventGroup group;
    private final boolean requiresForm;
    private final boolean generatesEvent;

    RequestType(String displayName, EventCategory category, EventGroup group) {
        this(displayName, category, group, true, true);
    }


    RequestType(String displayName, EventCategory category, EventGroup group, boolean requiresForm, boolean generatesEvent) {
        this.displayName = displayName;
        this.category = category;
        this.group = group;
        this.requiresForm = requiresForm;
        this.generatesEvent = generatesEvent;
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

    public boolean requiresForm() {
        return requiresForm;
    }

    public boolean generatesEvent() {
        return generatesEvent;
    }
}
