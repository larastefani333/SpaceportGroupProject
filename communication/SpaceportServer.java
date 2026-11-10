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

    private final int maxClientThreads = 10;
    private final List<ClientConnection> clientConnections;
    
    
    public SpaceportServer(int port) {
        this.port = port;
        this.clientConnections = new ArrayList<>();
        this.running = false;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(maxClientThreads);
        System.out.println("Spaceport Server started on port : " + port);
        acceptConnections();
        running = true;
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

    private void acceptConnections() throws IOException{
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                ClientConnection conn = new ClientConnection(socket, this);
                registerConnection(conn);
                threadPool.submit(conn);
            } catch (IOException e) {
                System.out.println("Error accepting client connection.");
            }
        }
    }

    Response handleRequest(Request request) {
        // Process request through appropriate service
        return new Response(false, "Request not implemented", null);
    }

    synchronized void registerConnection(ClientConnection conn) {
        clientConnections.add(conn);
    }

    synchronized void unregisterConnection(ClientConnection conn) {
        clientConnections.remove(conn);
    }

    private synchronized List<ClientConnection> getConnectedClients() {
        return new ArrayList<>(clientConnections);
    } 

    private void sendNotification(Notification notification) {
        // leaving for now
    }

}
