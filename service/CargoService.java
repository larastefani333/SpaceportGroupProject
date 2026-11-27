package service;

import communication.Request;
import communication.Response;
import model.Cargo;
import model.Transport;
import model.TransportState;

public class CargoService implements Service {

    private EventManager eventManager;

    @Override
    public Response handleRequest(Request request) {
        // Handle cargo operations
        return new Response(true, "Cargo operation completed.", null);
    }

    public boolean loadCargo(Transport transport, Cargo cargo) {
        // load cargo to transport
        if (transport == null) return false;
        if (canOccur(transport, transport.getState()))
        {
            int totalCargoUnits = transport.getTotalCargoUnits() + cargo.getCargoUnits();
            if (totalCargoUnits >= transport.getTotalCargoUnits())
            {
                transport.setState(TransportState.READY); // probs do need to change state
                System.out.println("Unable to complete operation, transport capacity is already full");
                return false;
            }
            transport.addCargo(cargo);
        }
        return true;
    }

    public boolean unloadCargo(Transport transport, Cargo cargo) {
        // unload cargo from transport
        if (canOccur(transport, transport.getState()))
        {
            int totalCargoUnits = transport.getTotalCargoUnits() - cargo.getCargoUnits();
            if (totalCargoUnits < 0)
            {
                transport.setState(TransportState.READY); // probs do need to change state
                System.out.println("Unable to complete operation, transport capacity will be below zero");
                return false;
            }
            transport.removeCargo(cargo);
        }
        return true;
    }

    public boolean canOccur(Transport transport, TransportState state)
    {
        return transport.getState() == TransportState.DOCKED || transport.getState() == TransportState.SERVICING || transport.getState() == TransportState.READY;
    }

    public boolean transferCargo(Transport source, Transport destination, Cargo cargo) {
        // transfer cargo between transports
        if (canOccur(source, source.getState())) {
            source.removeCargo(cargo);
            destination.addCargo(cargo);
            return true;
        }
        return false;
    }

    public Boolean validateCargoOperation(Transport transport) {
        // validate cargo operation
        return null;
    }

    public Boolean validateTransferOperation(Transport source, Transport destination, int quantity) {
        // validate cargo transfer
        return null;
    }

}
