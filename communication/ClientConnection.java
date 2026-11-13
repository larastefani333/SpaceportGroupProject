package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import model.EventType;

public class ClientConnection {

    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ClientConnection(String hostname, int port) throws IOException {
        this.socket = new Socket(hostname, port);
        initialiseStreams();
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

    public void sendRequest(EventType type, Map<String, Object> parameters) throws IOException {
        Request request = createRequest(type, parameters);
        sendMessage(request);
    }

    private Request createRequest(EventType type, Map<String, Object> parameters) {
        return new Request(type, parameters);
    }

    private synchronized void sendMessage(Object message) throws IOException {
        output.writeObject(message);
        output.flush();
    }

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

}