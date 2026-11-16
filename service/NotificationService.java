package service;

import java.util.concurrent.ExecutorService;

import communication.Request;
import communication.Response;
import communication.SpaceportServer;
import model.Event;
import model.Notification;

public class NotificationService implements Service {

    private SpaceportServer spaceportServer;
    private ExecutorService notificationExecutor;

    @Override
    public Response handleRequest(Request request) {
        return new Response(true, "Docking operation completed.", null);
    }

    public Notification createNotification(Event event) {
        // create notification from event
        return null;
    }

    public void distributeNotification(Notification notification) {
        // distribute notification to clients
    }

}
