package presentation;

import java.util.Arrays;

import communication.ClientConnection;
import model.EventType;
import model.EventCategory;

public class ClientTerminal extends ControlTerminal {

    public ClientTerminal(ClientConnection conn) {
        super(conn, "Client");
        initialiseOptions(EventCategory.CLIENT);
        initialisePages();
    }

    @Override
    protected EventType[] getOptions() {
        return Arrays.stream(EventType.values())
            .filter(type -> type.getCategory() == EventCategory.CLIENT)
            .toArray(EventType[]::new);
    }
}
