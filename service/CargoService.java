package service;

import model.Transport;

public class CargoService {

    private EventManager eventManager;

    public boolean loadCargo(Transport transport, int quantity) {
        // load cargo to transport
        return false;
    }

    public boolean unloadCargo(Transport transport, int quantity) {
        // unload cargo from transport
        return false;
    }

    public boolean transferCargo(Transport source, Transport destination, int quantity) {
        // transfer cargo between transports
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
