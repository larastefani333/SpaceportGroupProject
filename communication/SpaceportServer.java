package communication;

import java.util.ArrayList;
import java.util.List;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.Notification;
import model.EventType;
import service.CargoService;
import service.DockingService;
import service.MaintenanceService;
import service.TransportService;

public class SpaceportServer {

    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;

    private CargoService cargoService;
    private DockingService dockingService;
    private MaintenanceService maintenanceService;
    private TransportService transportService;

    private final List<ConnectionHandler> clientConnections;
    
    
    public SpaceportServer(int port) {
        this.port = port;
        this.clientConnections = new ArrayList<>();
        this.cargoService = new CargoService();
        this.dockingService = new DockingService();
        this.maintenanceService = new MaintenanceService();
        this.transportService = new TransportService();
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
                registerConnection(conn);
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
        EventType eventType = request.getEventType();
        System.out.println("Received request: " + eventType);
        return switch (eventType.getGroup()) {
            case CARGO -> cargoService.handleRequest(request);
            case DOCKING_BAYS -> dockingService.handleRequest(request);
            case MAINTENANCE -> maintenanceService.handleRequest(request);
            case TRANSPORTS -> transportService.handleRequest(request);
            default -> new Response(false, "Unsupported operation.", null);
        };
    }

    synchronized void registerConnection(ConnectionHandler conn) {
        clientConnections.add(conn);
    }

    synchronized void unregisterConnection(ConnectionHandler conn) {
        clientConnections.remove(conn);
    }

    private synchronized List<ConnectionHandler> getConnectedClients() {
        return new ArrayList<>(clientConnections);
    } 

    private void sendNotification(Notification notification) {
        // leaving for now
    }

}
