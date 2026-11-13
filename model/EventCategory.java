package model;

public enum EventCategory {
    CLIENT,
    ADMINISTRATOR,
    CONNECTION,
    SYSTEM;

    public boolean allows(EventCategory eventCategory) {
        if (this == ADMINISTRATOR) {
            return eventCategory == CLIENT || eventCategory == ADMINISTRATOR;
        }
        return this == eventCategory;
    }
}
