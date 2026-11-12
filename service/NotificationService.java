package service;

import java.util.concurrent.ExecutorService;
import communication.SpaceportServer;
import model.Event;
import model.Notification;

public class NotificationService {

    private SpaceportServer spaceportServer;
    private ExecutorService notificationExecutor;

    public Notification createNotification(Event event) {
        // create notification from event
        return null;
    }

    public void distributeNotification(Notification notification) {
        // distribute notification to clients
    }

}
