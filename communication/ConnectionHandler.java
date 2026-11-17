package communication;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.Notification;
import model.User;

public class ConnectionHandler implements Runnable {

    private final Socket socket;
    private final SpaceportServer server;
    private User user;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    // volatile means changed to connected flag are visible across multiple threads
    private volatile boolean connected;

    public ConnectionHandler(Socket socket, SpaceportServer server) {
        this.socket = socket;
        this.server = server;
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

            registerUser();

            while (connected) {
                Object message = input.readObject();
                if (message instanceof Request request) {
                    // Forward request to server
                    Response response = server.handleRequest(request);

                    // send result back to client as response
                    sendMessage(response);
               } else {
                    sendMessage(new Response(false, "Unsupported message received.", null));
               }
            }
        } catch (EOFException e) {
            // client connection is closed
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                System.out.println("Connection error: " + e.getMessage());
            }
        } finally {
            disconnect();
            server.unregisterConnection(this);
        }
    }

    public synchronized void sendMessage(Object message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    public void disconnect() {
        connected = false;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public User getUser() {
        return user;
    }

    private void registerUser() throws IOException, ClassNotFoundException {
        Object initialMessage = input.readObject();
        if (!(initialMessage instanceof User)) {
            throw new IOException("Invalid initial connection message :(");
        }
        User receivedUser = (User) initialMessage;
        user = receivedUser;
        server.registerConnection(this);
    }

    public void sendNotification(Notification notification) {
        try {
            sendMessage(notification);
        } catch (IOException e) {
            System.out.println("Error sending notification: " + e.getMessage());
        }
    }
}
