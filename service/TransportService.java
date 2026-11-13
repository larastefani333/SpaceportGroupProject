package service;

import java.util.List;

import communication.Request;
import communication.Response;
import model.Transport;

public class TransportService implements Service{

    private EventManager eventManager;

    @Override
    public Response handleRequest(Request request) {
        return new Response(true, "Transport operation completed.", null);
    }

    public Boolean addTransport(Transport transport) {
        // add transport to system
        return null;
    }

    public boolean validateTransport(Transport transport) {
        // validate transport details
        return false;
    }

    public List<Transport> getReadyTransports() {
        // retrieve ready transports
        return null;
    }

}
