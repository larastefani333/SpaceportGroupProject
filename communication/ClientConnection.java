package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import model.RequestType;
import model.User;

public class ClientConnection {

    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private final User user;

    public ClientConnection(String hostname, int port, User user) throws IOException {
        this.socket = new Socket(hostname, port);
        initialiseStreams();
        this.user = user;
        sendMessage(user);
    }

    private void initialiseStreams() throws IOException {
        output = new ObjectOutputStream(socket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socket.getInputStream());
    }

    public void disconnect() throws IOException {
        if (input != null) input.close();
        if (output != null) output.close();
        if (!socket.isClosed()) socket.close();
    }

    public void sendRequest(RequestType type, Map<String, Object> parameters) throws IOException {
        Request request = createRequest(type, parameters);
        sendMessage(request);
    }

    private Request createRequest(RequestType type, Map<String, Object> parameters) {
        return new Request(type, parameters, user);
    }

    private synchronized void sendMessage(Object msg) throws IOException {
        output.writeObject(msg);
        output.flush();
    }

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    public User getUser() {
        return user;
    }

}