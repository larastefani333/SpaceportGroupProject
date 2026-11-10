package communication;

import java.util.ArrayList;
import java.util.List;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.Notification;

public class SpaceportServer {

    private final int port;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private boolean running;

    private final List<ConnectionHandler> clientConnections;
    
    
    public SpaceportServer(int port) {
        this.port = port;
        this.clientConnections = new ArrayList<>();
        this.running = false;
    }

    public static void main(String[] args) {
        SpaceportServer server = new SpaceportServer(5000);
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("SpaceportServer failed to start");
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
                System.out.println("Error accepting client connection.");
            }
        }
    }

    Response handleRequest(Request request) {
        // Process request through appropriate service
        System.out.println("Received request: " + request.getEventType());
        return new Response(true, "Request received successfully!", null);
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
