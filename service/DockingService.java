package service;

import communication.Request;
import communication.Response;
import model.DockingBay;
import model.DockingBayStatus;
import model.Transport;
import model.TransportState;
import java.time.LocalDateTime;

public class DockingService implements Service {

    private EventManager eventManager;

    @Override
    public Response handleRequest(Request request) {
        return new Response(true, "Docking operation completed.", null);
    }

    public boolean assignBay(Transport transport, DockingBay bay) {
        // assign docking bay to transport if in WAITING state
        if (transport.getState() == TransportState.WAITING) {
            bay.setAssignedTransport(transport); // do I do these both?
            transport.setDockingBay(bay);
        }
        return false;
    }

    public Boolean exchangeBay(Transport transport1, Transport transport2) {
        // exchange docking bays between transports
        DockingBay bay1 = transport1.getDockingBay();
        DockingBay bay2 = transport2.getDockingBay();

        // if the same, return false
        if (bay1.getAssignedTransport() == bay2.getAssignedTransport()) {
            System.out.println("Bay already assigned to the same transport");
            return false;
        }

        // reassign docking bays to transports, and transports to docking bays
        if (transport1.getState() == TransportState.DOCKED || transport1.getState() == TransportState.READY) {
            if (transport2.getState() == TransportState.DOCKED || transport2.getState() == TransportState.READY) {
                transport1.setDockingBay(bay2);
                bay1.setAssignedTransport(transport2);
                transport2.setDockingBay(bay1);
                bay2.setAssignedTransport(transport1);
                return true;
            }
        }
        // otherwise they remain the same
        return false;
    }

    public Boolean closeBay(DockingBay bay) {
        // close docking bay
        // The administrator may close or reopen unoccupied docking bays.
        if (bay.getStatus() != DockingBayStatus.OCCUPIED)
        {
            bay.setStatus(DockingBayStatus.CLOSED);
            return true;
        }
        return false;
    }

    public Boolean openBay(DockingBay bay) {
        // open docking bay
        if (bay.getStatus() != DockingBayStatus.OCCUPIED)
        {
            bay.setStatus(DockingBayStatus.AVAILABLE);
            return true;
        }
        return false;
    }

    // A transport may depart when it is READY and its scheduled departure time has been reached.
    public Boolean depart(DockingBay bay, Transport transport) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (transport.getState() == TransportState.READY)
        {
            if (currentTime.equals(transport.getScheduledDeparture()))
            {
                transport.setDockingBay(null);
                bay.setAssignedTransport(null);
                return true;
            }
        }
        return false;
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
