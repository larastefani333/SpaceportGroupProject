package model;

import communication.ClientConnection;
import communication.Request;
import communication.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Client extends User {
    
    public Client(String userID, String username) {
        super(userID, username);
    }

    public static void main(String[] args) {

        try {
            ClientConnection connection = new ClientConnection("localhost", 5000);
            connection.connect();
            System.out.println("Connected to spaceport server.");

            // test request
            Map<String, Object> parameters = new HashMap<>();

            Request request = connection.createRequest(EventType.TRANSPORT_CREATED, parameters);

            connection.sendMessage(request);

            // Receive response from server.
            Object message = connection.receiveMessage();
            if (message instanceof Response response) {
                System.out.println(response.getMessage());
            }
            connection.disconnect();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client communication error: " + e.getMessage());
        }
    }
}
