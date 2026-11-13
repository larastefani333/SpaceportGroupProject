package service;

import infrastructure.EventLog;
import model.EventType;

public class EventManager {

    private EventLog eventLog;
    private NotificationService notificationService;

    public void handleSuccessfulEvent(EventType eventType, String details) {
        // record event and trigger notification
    }

}
