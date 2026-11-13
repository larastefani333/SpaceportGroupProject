import java.io.IOException;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import communication.ClientConnection;
import presentation.ClientTerminal;
import presentation.AdministratorTerminal;

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

    private static void startTerminal(boolean isAdministrator) {
        try {
            ClientConnection conn = new ClientConnection(HOSTNAME, PORT);
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
