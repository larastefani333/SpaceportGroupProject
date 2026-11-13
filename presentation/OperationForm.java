
package presentation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;

import model.EventType;

public class OperationForm extends JPanel {

    private final ControlTerminal terminal;
    private final EventType eventType;

    private final Map<String, JTextField> fields = new LinkedHashMap<>();

    private JSpinner departureDate;
    private JSpinner departureTime;

    private int nextRow = 0;

    public OperationForm(ControlTerminal terminal, EventType eventType) {
        this.terminal = terminal;
        this.eventType = eventType;
        setLayout(new BorderLayout(10, 15));
        initialiseForm();
    }

    private void initialiseForm() {

        JPanel fieldsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(8, 5, 8, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;

        switch (eventType.getGroup()) {
            case CARGO ->
                initialiseCargoFields(fieldsPanel, constraints);

            case DOCKING_BAYS ->
                initialiseDockingBayFields(fieldsPanel, constraints);

            case MAINTENANCE ->
                initialiseMaintenanceFields(fieldsPanel, constraints);

            case TRANSPORTS ->
                initialiseTransportFields(fieldsPanel, constraints);

            default -> {
                add(new JLabel("This operation does not have an input form."), BorderLayout.CENTER);
                return;
            }
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(fieldsPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> submit());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initialiseCargoFields(JPanel panel, GridBagConstraints constraints) {
        switch (eventType) {
            case CARGO_LOADED, CARGO_UNLOADED -> {
                addField(panel, constraints, "Transport ID:", "transportID");
                addField(panel, constraints, "Cargo ID:", "cargoID");
                addField(panel, constraints, "Cargo Units:", "cargoUnits");
            }
            case CARGO_TRANSFERRED -> {
                addField(panel, constraints, "Source Transport ID:", "sourceTransportID");
                addField(panel, constraints, "Destination Transport ID:", "destinationTransportID");
                addField(panel, constraints, "Cargo ID:", "cargoID");
                addField(panel, constraints, "Cargo Units:", "cargoUnits");
            }
            default -> {}
        }
    }

    private void initialiseDockingBayFields(JPanel panel, GridBagConstraints constraints) {
        switch (eventType) {
            case BAY_ALLOCATED -> {
                addField(panel, constraints, "Transport ID:", "transportID");
                addField(panel, constraints, "Docking Bay ID:", "bayID");
            }
            case BAY_EXCHANGED -> {
                addField(panel, constraints, "First Transport ID:", "firstTransportID");
                addField(panel, constraints, "Second Transport ID:", "secondTransportID");
            }
            case BAY_CLOSED, BAY_OPENED ->
                addField(panel, constraints, "Docking Bay ID:", "bayID");
            default -> {}
        }
    }

    private void initialiseMaintenanceFields(JPanel panel, GridBagConstraints constraints) {
        switch (eventType) {
            case MAINTENANCE_ASSIGNED -> {
                addField(panel, constraints, "Transport ID:", "transportID");
                addField(panel, constraints, "Maintenance Team ID:", "teamID");
            }
            case MAINTENANCE_COMPLETED ->
                addField(panel, constraints, "Transport ID:", "transportID");
            case MAINTENANCE_DECOMMISSIONED ->
                addField(panel, constraints, "Maintenance Team ID:", "teamID");
            default -> {}
        }
    }

    private void initialiseTransportFields(JPanel panel, GridBagConstraints constraints) {
        switch (eventType) {
            case TRANSPORT_CREATED -> {
                addField(panel, constraints, "Transport ID:", "transportID");
                addDepartureDateTime(panel, constraints);
                addField(panel, constraints, "Maximum Cargo Capacity:", "maxCargoCapacity");
            }
            default -> {}
        }
    }

    private void addField(JPanel panel, GridBagConstraints constraints, String label, String parameterName) {

        int row = nextRow++;
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        panel.add(new JLabel(label), constraints);
        JTextField textField = new JTextField(15);

        constraints.gridx = 1;
        constraints.weightx = 1;

        panel.add(textField, constraints);
        fields.put(parameterName, textField);
    }

    private void addDepartureDateTime(JPanel panel, GridBagConstraints constraints) {

        int row = nextRow++;

        departureDate = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        departureDate.setEditor(new JSpinner.DateEditor(departureDate, "dd/MM/yyyy"));

        departureTime = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE));
        departureTime.setEditor(new JSpinner.DateEditor(departureTime, "HH:mm"));

        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        dateTimePanel.add(new JLabel("Date:"));
        dateTimePanel.add(departureDate);
        dateTimePanel.add(new JLabel("Time:"));
        dateTimePanel.add(departureTime);

        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 0;

        panel.add(new JLabel("Scheduled Departure:"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;

        panel.add(dateTimePanel, constraints);
    }

    private void submit() {
        try {
            Map<String, Object> params = collectParams();
            if (eventType == EventType.TRANSPORT_CREATED) {
                params.put("scheduledDeparture", getScheduledDeparture());
            }
            terminal.submitRequest(eventType, params);
        } catch (IllegalArgumentException e) {
            terminal.showError("Invalid Input", e.getMessage());

        } catch (java.text.ParseException e) {
            terminal.showError("Invalid Input", "Please enter a valid date and time.");
        }
    }

    private Map<String, Object> collectParams() {
        Map<String, Object> params = new LinkedHashMap<>();
        for (Map.Entry<String, JTextField> entry : fields.entrySet()) {
            String paramName = entry.getKey();
            String value = entry.getValue().getText().trim();
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Please complete all fields.");
            }
            params.put(paramName, convertValue(paramName, value));
        }
        return params;
    }

    private Object convertValue(String paramName, String value) {
        switch (paramName) {
            case "cargoUnits":
            case "maxCargoCapacity":
                int num;
                try {
                    num = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(paramName + " must be a whole number.");
                }
                if (num <= 0) {
                    throw new IllegalArgumentException(paramName + " must be greater than zero.");
                }
                return num;
            default:
                return value;
        }
    }

    private LocalDateTime getScheduledDeparture() throws java.text.ParseException {
        departureDate.commitEdit();
        departureTime.commitEdit();

        Date date = (Date) departureDate.getValue();
        Date time = (Date) departureTime.getValue();

        LocalDate localDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalTime localTime = time.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();

        localTime = LocalTime.of(localTime.getHour(), localTime.getMinute());

        return LocalDateTime.of(localDate, localTime);
    }
}
