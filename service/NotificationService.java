package service;

import communication.SpaceportServer;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Event;
import model.Notification;

public class NotificationService {

    private final SpaceportServer spaceportServer;
    private final ExecutorService notificationExecutor;

    public NotificationService(SpaceportServer spaceportServer) {
        this.spaceportServer = spaceportServer;
        this.notificationExecutor = Executors.newSingleThreadExecutor();
    }

    public void handleEvent(Event event) {
        Notification notification = createNotification(event);
        distributeNotification(notification);
    }

    private Notification createNotification(Event event) {
        String timestamp = event.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return new Notification(
                UUID.randomUUID().toString(),
                "[" + timestamp + "] " + event.getDescription(),
                event.getEventType()
        );
    }

    private void distributeNotification(Notification notification) {
        notificationExecutor.submit(() ->
                spaceportServer.broadcastNotification(notification)
        );
    }

    public void shutdown() {
        notificationExecutor.shutdown();
    }
}
