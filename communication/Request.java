package communication;

import java.io.Serializable;
import java.util.Map;

import model.RequestType;
import model.User;

public class Request implements Serializable {

    private final RequestType eventType;
    private final Map<String, Object> parameters;
    private final User user;

    public Request(RequestType eventType, Map<String, Object> parameters, User user) {
        this.eventType = eventType;
        this.parameters = parameters;
        this.user = user;
    }

    public RequestType getEventType() {
        return eventType;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public User getUser() {
        return user;
    }
}