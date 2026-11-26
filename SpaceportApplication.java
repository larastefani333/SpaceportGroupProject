
import communication.ClientConnection;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import javax.swing.SwingUtilities;

import model.Administrator;
import model.Client;
import model.User;
import presentation.AdministratorTerminal;
import presentation.ClientTerminal;

public class SpaceportApplication {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 5000;
    private static final Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        showLoginMenu();
        selectRole();
    }

    private static void showLoginMenu() {
        System.out.println("===== SPACEPORT MANAGEMENT SYSTEM =====");
        System.out.println("1. Client");
        System.out.println("2. Administrator");
        System.out.println("3. Exit");
    }

    private static void selectRole() {
        System.out.print("Select your role: ");
        String choice = in.nextLine().strip();
        switch (choice) {
            case "1" -> startTerminal(false);
            case "2" -> startTerminal(true);
            case "3" -> System.out.println("Exiting...");
            default -> {
                System.out.println("\nInvalid selection - please try again.");
                showLoginMenu();
                selectRole();
            }
        }
    }

    private static String getUsername() {
        while (true) { 
            System.out.print("Enter username (or 0 to cancel operation): ");
            String username = in.nextLine().strip();

            if (username.equals("0")) {
                return null;
            }

            if (username.isEmpty()) {
                System.out.println("Username cannot be empty - please try again (or select 0 to cancel");
            } else {
                return username;
            }
        }
    }

    private static User createUser(boolean isAdministrator) {
        // should establish rules related to usernames + userID
        // + validation in the future with checking non duplicate usernames + built out log in system
        String username = getUsername();
        String userID = UUID.randomUUID().toString();

        if (isAdministrator) {
            return new Administrator(userID, username);
        }
        return new Client(userID, username);
    }

    private static void startTerminal(boolean isAdministrator) {
        try {
            User user = createUser(isAdministrator);
            if (user == null) return;
            
            ClientConnection conn = new ClientConnection(HOSTNAME, PORT, user);
            SwingUtilities.invokeLater(() -> {
                if (isAdministrator) {
                    AdministratorTerminal terminal = new AdministratorTerminal(conn);
                    terminal.open();
                } else {
                    ClientTerminal terminal = new ClientTerminal(conn);
                    terminal.open();
                }
            });
        } catch (IOException e) {
            System.err.println("Unable to connect to the server: " + e.getMessage());
        }
    }
}
