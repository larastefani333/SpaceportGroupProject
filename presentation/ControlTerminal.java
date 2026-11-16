
package presentation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import communication.ClientConnection;
import communication.Response;
import model.Notification;
import model.EventCategory;
import model.EventGroup;
import model.EventType;

public abstract class ControlTerminal extends JFrame {

    protected final ClientConnection conn;

    protected JPanel optionsPnl;
    protected JPanel interactionPnl;
    protected JPanel notificationPnl;

    protected CardLayout interactionLayout;
    protected JTextArea notificationArea;

    private static final Font TITLE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);
    private static final Font HEADING_FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);
    private static final Font BUTTON_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    protected volatile boolean running;

    public ControlTerminal(ClientConnection conn, String role) {
        this.conn = conn;

        setTitle("Spaceport Management System - " + role);
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 550));
        setLocationRelativeTo(null);

        // Prevents Swing from automatically closing the window before disconnecting from the server
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // When user clicks the windows close button it disconnects from server properly
        addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        buildUI(role);
    }

    protected abstract EventType[] getOptions();

    protected void displayResponse(Response response) {
        JOptionPane.showMessageDialog(
            this,
            response.getMessage(),
            response.isSuccessful() ? "Success" : "Error",
            response.isSuccessful() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }

    private void buildUI(String role) {
        JPanel mainPnl = new JPanel(new BorderLayout(10, 10));
        mainPnl.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPnl.add(buildHeader(role), BorderLayout.NORTH);

        JPanel panels = new JPanel(new GridLayout(1, 3, 10, 0));

        panels.add(buildOptionsPanel());
        panels.add(buildInteractionPanel());
        panels.add(buildNotificationPanel());

        mainPnl.add(panels, BorderLayout.CENTER);

        setContentPane(mainPnl);
    }

    private JLabel buildHeader(String role) {
        JLabel title = new JLabel("Spaceport Management System (" + role + ")", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        return title;
    }

    private JScrollPane buildOptionsPanel() {
        optionsPnl = new JPanel();
        optionsPnl.setLayout(new BoxLayout(optionsPnl, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(optionsPnl);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Options"));
        return scrollPane;
    }

    private JPanel buildInteractionPanel() {
        interactionLayout = new CardLayout();
        interactionPnl = new JPanel(interactionLayout);
        interactionPnl.setBorder(BorderFactory.createTitledBorder("Interaction"));
        return interactionPnl;
    }

    private JPanel buildNotificationPanel() {
        notificationPnl = new JPanel(new BorderLayout(5, 5));
        notificationPnl.setBorder(BorderFactory.createTitledBorder("Notifications"));
        notificationArea = new JTextArea();
        notificationArea.setEditable(false);
        notificationArea.setLineWrap(true);
        notificationArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(notificationArea);
        notificationPnl.add(scrollPane, BorderLayout.CENTER);
        return notificationPnl;
    }

    protected void showPage(String page) {
        interactionLayout.show(interactionPnl, page);
    }

    public void handleIncomingMessage(Object message) {
        SwingUtilities.invokeLater(() -> {
            if (message instanceof Response response) {
                displayResponse(response);
            } else if (message instanceof Notification notification) {
                displayNotification(notification);
            }
        });
    }

    public void displayNotification(Notification notification) {
        notificationArea.append(notification.getMessage() + "\n");
    }

    public void open() {
        running = true;
        setVisible(true);

        Thread listener = new Thread(() -> {
            while (running) {
                try {
                    Object msg = conn.receiveMessage();
                    handleIncomingMessage(msg);
                } catch (IOException | ClassNotFoundException e) {
                    if (running) {
                        SwingUtilities.invokeLater(() -> {
                            // Creates pop up window to inform user of connection to the server being lost
                           showError("Connection Error", "Connection lost: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                           close();
                        });
                    }
                    break;
                }
            }
        });
        listener.start();
    }

    public void close() {
        running = false;
        try {
            conn.disconnect();
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> dispose());
    }

    protected void addOptionButton(String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.addActionListener(event -> action.run());
        optionsPnl.add(btn);
    }

    protected void initialiseOptions(EventCategory category) {
        for (EventGroup group : EventGroup.values()) {

            JPanel groupPanel = new JPanel();
            groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
            groupPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

            JLabel heading = new JLabel(group.getDisplayName());
            heading.setFont(HEADING_FONT);
            groupPanel.add(heading);

            for (EventType event : EventType.values()) {
                if(category.allows(event.getCategory()) && event.getGroup() == group) {
                    JButton btn = new JButton(event.getDisplayName());
                    btn.setFont(BUTTON_FONT);
                    btn.addActionListener(e -> showPage(event.name()));
                    groupPanel.add(btn);
                }
            }
            // each group msut contain at least one heading and one button (option)
            if (groupPanel.getComponentCount() > 1) optionsPnl.add(groupPanel);
        }
}

    protected void initialisePages() {
        for (EventType type : getOptions()) {
            OperationForm form = new OperationForm(this, type);
            interactionPnl.add(form, type.name());
        }
    }

    public void submitRequest(EventType type, Map<String, Object> params) {
        try {
            conn.sendRequest(type, params);
        } catch (IOException e) {
            showError("Communication Error", "Failed to send request: " + e.getMessage());
        }
    }

    protected void showError(String title, String msg) {
        JOptionPane.showMessageDialog(
            this,
            msg,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }
}