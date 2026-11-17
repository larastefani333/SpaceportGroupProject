package service;

import communication.Request;
import communication.Response;
import infrastructure.EventLog;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import model.Event;
import model.RequestType;
import model.User;

public class EventManager {

    private EventLog eventLog;
    private NotificationService notificationService;

    public EventManager(EventLog eventLog, NotificationService notificationService) {
        this.eventLog = eventLog;
        this.notificationService = notificationService;
    }

    public void createConnectionEvent(RequestType eventType, User user) {
        Event event = new Event(
                UUID.randomUUID().toString(),
                eventType,
                user,
                Map.of()
        );
        notificationService.handleEvent(event);
    }

    public void createEvent(Request request, Response response) {
        if (!response.isSuccessful()) return;
        Event event = new Event(
                UUID.randomUUID().toString(),
                request.getEventType(),
                request.getUser(),
                getAssociatedIDs(request)
        );
        notificationService.handleEvent(event);
    }

    private Map<String, String> getAssociatedIDs(Request request) {
        Map<String, String> associatedIDs = new HashMap<>();
        for (Map.Entry<String, Object> parameter : request.getParameters().entrySet()) {
            if (parameter.getKey().endsWith("ID")) {
                associatedIDs.put(parameter.getKey(), parameter.getValue().toString());
            }
        }
        return associatedIDs;
    }
}
