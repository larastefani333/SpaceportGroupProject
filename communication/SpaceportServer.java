package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import model.Administrator;
import model.Notification;
import model.RequestType;
import model.User;
import model.UserRole;
import service.CargoService;
import service.DockingService;
import service.EventManager;
import service.MaintenanceService;
import service.NotificationService;
import service.TransportService;

public class SpaceportServer {

    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;

    private final CargoService cargoService;
    private final DockingService dockingService;
    private final MaintenanceService maintenanceService;
    private final TransportService transportService;
    private final NotificationService notificationService;
    private final EventManager eventManager;

    private final List<ConnectionHandler> connections;
    
    
    public SpaceportServer(int port) {
        this.port = port;
        this.connections = new ArrayList<>();
        this.cargoService = new CargoService();
        this.dockingService = new DockingService();
        this.maintenanceService = new MaintenanceService();
        this.transportService = new TransportService();

        this.notificationService = new NotificationService(this);
        // need to implement event logger but leaving as null parameter for now
        this.eventManager = new EventManager(null, notificationService);

        this.running = false;
    }

    public static void main(String[] args) {
        SpaceportServer server = new SpaceportServer(5000);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("SpaceportServer failed to start: " + e.getMessage());
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        int maxClientThreads = 10;
        threadPool = Executors.newFixedThreadPool(maxClientThreads);
        System.out.println("Spaceport Server started on port : " + port);
        running = true;
        acceptConnections();
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket");
        }

        notificationService.shutdown();
     
        if (threadPool != null) {
            threadPool.shutdown();
        }
        running = false;
        System.out.println("\nSpaceport Server shutting down...");

    }

    private void acceptConnections() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                ConnectionHandler conn = new ConnectionHandler(socket, this);
                threadPool.submit(conn);
            } catch (IOException e) {
                if (running) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    Response handleRequest(Request request) {
        // Process request through appropriate service
        RequestType eventType = request.getEventType();
        System.out.println("Received request: " + eventType);
        Response response = switch (eventType.getGroup()) {
            case CARGO -> cargoService.handleRequest(request);
            case DOCKING_BAYS -> dockingService.handleRequest(request);
            case MAINTENANCE -> maintenanceService.handleRequest(request);
            case TRANSPORTS -> transportService.handleRequest(request);
            case CONNECTIONS -> handleConnectionRequest(request);
            default -> new Response(false, "Unsupported operation.", null);
        };

        // generate notification and log event if successful
        if (response.isSuccessful() && eventType.generatesEvent()) {
            eventManager.createEvent(request, response);
        }
        return response;
    }

    private Response handleConnectionRequest(Request request) {
        if (request.getEventType() == RequestType.CONNECTED_USERS_VIEWED) {
            if (!(request.getUser() instanceof Administrator)) {
                return new Response(false, "Unauthorised operation.", null);
            }
            return new Response(true, "Connected users retrieved successfully.", getConnectedUsers(), request.getEventType());
        }
        return new Response(false, "Unsupported connection operation.", null);
    }

    synchronized void registerConnection(ConnectionHandler conn) {
        connections.add(conn);

        // log user connected
        User user = conn.getUser();
        RequestType eventType = user.getRole() == UserRole.ADMINISTRATOR ? RequestType.ADMIN_CONNECTED : RequestType.CLIENT_CONNECTED;
        eventManager.createConnectionEvent(eventType, user);
    }

    synchronized void unregisterConnection(ConnectionHandler conn) {
        if (connections.remove(conn)) {
            User user = conn.getUser();
            if (user != null) {
                RequestType eventType = user.getRole() == UserRole.ADMINISTRATOR ? RequestType.ADMIN_DISCONNECTED : RequestType.CLIENT_DISCONNECTED;

                // log user disconnected
                eventManager.createConnectionEvent(eventType, user);
            }
        }
    }

    private synchronized List<User> getConnectedUsers() {
        return new ArrayList<>(connections.stream()
                .map(ConnectionHandler::getUser)
                .toList());
    } 

    public synchronized void broadcastNotification(Notification notification) {
        for (ConnectionHandler conn : connections) {
            conn.sendNotification(notification);
        }
    }
}
