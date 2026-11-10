package communication;

import java.io.Serializable;
import java.util.Map;

import model.EventType;

public class Request implements Serializable {

    private final EventType eventType;
    private final Map<String, Object> parameters;

    public Request(EventType eventType, Map<String, Object> parameters) {
        this.eventType = eventType;
        this.parameters = parameters;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}