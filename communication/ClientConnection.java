package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import model.EventType;

public class ClientConnection implements Runnable{

    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    private final SpaceportServer server;

    public ClientConnection(Socket socket, SpaceportServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            connect();
            while (!socket.isClosed()) {
                Object msg = receiveMessage();
                if (msg instanceof Request request) {
                    Response response = server.handleRequest(request);
                    sendMessage(response);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Connection lost...");
        } finally {
            try {
                disconnect();
            } catch (IOException e) {
                System.out.println("Error closing the client connection");
            }
            server.unregisterConnection(this);
        }
    }

    public void connect() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();

        input = new ObjectInputStream(socket.getInputStream());
    }

    public void disconnect() throws IOException {
        if (input != null) input.close();
        if (output != null) output.close();

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public Request createRequest(EventType type, Map<String, Object> parameters) {
        return new Request(type, parameters);
    }

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    public synchronized void sendMessage(Object message) throws IOException {
        output.writeObject(message);
        output.flush();
    }
}