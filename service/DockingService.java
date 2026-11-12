package service;

import model.Transport;
import model.DockingBay;

public class DockingService {

    private EventManager eventManager;

    public boolean assignBay(Transport transport) {
        // assign docking bay to transport
        return false;
    }

    public Boolean exchangeBay(Transport transport1, Transport transport2) {
        // exchange docking bays between transports
        return null;
    }

    public Boolean closeBay(DockingBay bay) {
        // close docking bay
        return null;
    }

    public Boolean openBay(DockingBay bay) {
        // open docking bay
        return null;
    }

    public Boolean processDeparture(Transport transport) {
        // process transport departure
        return null;
    }

    public Boolean validateAssignment(Transport transport, DockingBay bay) {
        // validate docking bay assignment
        return null;
    }

}
