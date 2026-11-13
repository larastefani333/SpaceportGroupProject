package infrastructure;

import java.util.concurrent.ScheduledExecutorService;
import service.DockingService;
import service.TransportService;

public class DepartureScheduler {

    private DockingService dockingService;
    private TransportService transportService;
    private ScheduledExecutorService scheduler;

    public void start() {
        // start scheduler
    }

    public void stop() {
        // stop scheduler
    }

    public void checkScheduledDepartures() {
        // check transports ready for departure
    }

}
