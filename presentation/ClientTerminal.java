package presentation;

import communication.ClientConnection;
import model.EventCategory;
import model.RequestType;

import java.util.Arrays;

public class ClientTerminal extends ControlTerminal {

    public ClientTerminal(ClientConnection conn) {
        super(conn, "Client");
        initialiseOptions(EventCategory.CLIENT);
        initialisePages();
    }

    @Override
    protected RequestType[] getOptions() {
        return Arrays.stream(RequestType.values())
            .filter(type -> type.getCategory() == EventCategory.CLIENT)
            .toArray(RequestType[]::new);
    }
}
