package communication;

import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class ClientConnection {

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public void connect() {
        // establish server connection
    }

    public void disconnect() {
        // close server connection
    }

    public Request createRequest(RequestType type, Map<String, Object> parameters) {
        // create request using type and parameters
        return null;
    }

    public void sendRequest(Request request) {
        // send request to server
    }

    public Object receiveMessage() {
        // receive message from server
        return null;
    }

    public void sendMessage(Object message) {
        // send message to client
    }

}
