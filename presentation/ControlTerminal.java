package presentation;

import java.util.Scanner;
import communication.ClientConnection;
import communication.Response;
import model.Notification;

public abstract class ControlTerminal {
    private Scanner scanner;
    private ClientConnection connection;

    public void handleIncomingMessage(Object message) {
        // handle responses and notification from the server
    }

    public abstract void displayMenu();

    public String getUserInput() {
        // read user input from terminal
        return null;
    }

    public void handleUserInput() {
        // process user input and send request to the server
    }

    public void displayResponse(Response response) {
        // display server response to user
    }

    public void displayNotification(Notification notification) {
        // display notifications received from server
    }

    public void open() {
        // open terminal and establish connection
    }

    public void close() {
        // close terminal and disconnect from server
    }

    public void displayMessage(String message) {
        // display message in terminal
    }

}
