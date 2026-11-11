package infrastructure;

import java.util.List;
import model.Event;
import model.EventType;

public class EventLog {

    private List<Event> event;

    public void logEvent(Event event) {
        // record event
    }

    public List<Event> getEventHistory() {
        // get event history
        return null;
    }

    public List<Event> getEventsByType(EventType eventType) {
        // get events by type
        return null;
    }

}
