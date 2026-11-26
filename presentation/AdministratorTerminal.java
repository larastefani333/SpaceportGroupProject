package presentation;

import communication.ClientConnection;
import communication.Response;
import model.EventCategory;
import model.RequestType;
import model.User;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AdministratorTerminal extends ControlTerminal {

    private JTextArea outputArea;
    private JLabel outputHeading;
    private static final String OUTPUT_PANEL_NAME = "OUTPUT";

    public AdministratorTerminal(ClientConnection conn) {
        super(conn, "Administrator");

        // output panel is unique to admin options at the moment
        // if client also needs to see and interpret displayed output
        // then move the extra output panel into control terminal
        interactionPnl.add(buildOutputPanel(), OUTPUT_PANEL_NAME);

        initialiseOptions(EventCategory.ADMINISTRATOR);
        initialisePages();
    }

    @Override
    protected RequestType[] getOptions() {
        return Arrays.stream(RequestType.values())
            .filter(type -> type.getCategory() != EventCategory.CONNECTION)
            .toArray(RequestType[]::new);
    }

    protected void displayOutput(String title, String content) {
        outputHeading.setText(title);
        outputArea.setText(content);
        showPage(OUTPUT_PANEL_NAME);
    }

    private JPanel buildOutputPanel() {
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputHeading = new JLabel();
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        outputPanel.add(outputHeading, BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        return outputPanel;
    }

    private void displayConnectedUsers(Response response) {

        List<User> users = (List<User>) response.getData();
        StringBuilder output = new StringBuilder();
        output.append("Connected Users (").append(users.size()).append(")\n\n");

        for (User user : users) {
            output.append("Username: ").append(user.getUsername()).append("\n");
            output.append("User ID: ").append(user.getUserID()).append("\n");
            output.append("Role: ").append(user.getRole().getDisplayName()).append("\n\n");
        }
        displayOutput("Connected Users", output.toString());
    }

    @Override 
    protected void displayResponse(Response response) {
        if ((response.getRequestType() == RequestType.CONNECTED_USERS_VIEWED) && response.isSuccessful()) {
            displayConnectedUsers(response);
        } else {
            super.displayResponse(response);
        }
    }
}
