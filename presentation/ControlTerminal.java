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
    }

}
