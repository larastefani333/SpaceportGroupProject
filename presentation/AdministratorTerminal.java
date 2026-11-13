package presentation;

import java.util.Arrays;

import communication.ClientConnection;
import model.EventCategory;
import model.EventType;

public class AdministratorTerminal extends ControlTerminal {

    public AdministratorTerminal(ClientConnection conn) {
        super(conn, "Administrator");
        initialiseOptions(EventCategory.ADMINISTRATOR);
        initialisePages();
    }

    @Override
    protected EventType[] getOptions() {
        return Arrays.stream(EventType.values())
            .filter(type -> type.getCategory() != EventCategory.CONNECTION)
            .toArray(EventType[]::new);
    }
}
