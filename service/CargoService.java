package service;

import communication.Request;
import communication.Response;
import model.Cargo;
import model.EventType;
import model.Transport;
import model.TransportState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CargoService implements Service {

    private final EventManager eventManager;
    private final TransportService transportService;
    private int nextCargoIdNumber = 1;

    public CargoService(EventManager eventManager, TransportService transportService) {
        this.eventManager = eventManager;
        this.transportService = transportService;
    }

    @Override
    public Response handleRequest(Request request) {
        Map<String, Object> params = request.getParameters();
        Integer quantity = readQuantity(params.get("quantity"));

        if (quantity == null) {
            return new Response(false, "Quantity must be a whole number.", null);
        }

        switch (request.getEventType()) {
            case CARGO_LOADED: {
                Transport transport = findTransport(params.get("transportId"));
                if (transport == null) {
                    return new Response(false, "Transport not found.", null);
                }
                if (loadCargo(transport, quantity)) {
                    return new Response(true, quantity + " units loaded onto " + transport.getTransportID()
                            + ". Cargo is now " + transport.getCargo() + "/" + transport.getMaxCargoCapacity() + ".",
                            null);
                }
                return new Response(false, "Load failed", null);
            }
            case CARGO_UNLOADED: {
                Transport transport = findTransport(params.get("transportId"));
                if (transport == null) {
                    return new Response(false, "Transport not found.", null);
                }
                if (unloadCargo(transport, quantity)) {
                    return new Response(true, quantity + " units unloaded from " + transport.getTransportID()
                            + ". Cargo is now " + transport.getCargo() + "/" + transport.getMaxCargoCapacity() + ".",
                            null);
                }
                return new Response(false, "Unload failed", null);
            }
            case CARGO_TRANSFERRED:
                Transport source = findTransport(params.get("sourceTransportId"));
                Transport destination = findTransport(params.get("destinationTransportId"));
                if (source == null || destination == null) {
                    return new Response(false, "Source and/or destination trasnport not found", null);
                }
                if (source == destination) {
                    return new Response(false, "Source transport must be different from destination transport", null);
                }
                if (transferCargo(source, destination, quantity)) {
                    return new Response(true, quantity + " units transferred from " + source.getTransportID()
                            + " to " + destination.getTransportID() + ".", null);
                }
                return new Response(false, "Transfer failed", null);
        }

        return new Response(true, "Cargo operation completed.", null);
    }

    public boolean loadCargo(Transport transport, int quantity) {
        if (transport == null) {
            return false;
        }

        Cargo cargo;
        int newTotal;
        synchronized (transport) {
            if (!validateCargoOperation(transport) || quantity <= 0) {
                return false;
            }
            newTotal = totalCargoUnits(transport) + quantity;
            if (newTotal > transport.getMaxCargoCapacity()) {
                return false;
            }
            cargo = new Cargo(newCargoId(), quantity);
            transport.addCargo(cargo);
        }

        eventManager.handleSuccessfulEvent(EventType.CARGO_LOADED,
                "Cargo " + cargo.getCargoID() + " (" + quantity + " units) loaded onto "
                        + transport.getTransportID() + " (now " + newTotal + " units)");

        return true;
    }

    public boolean unloadCargo(Transport transport, int quantity) {
        if (transport == null) {
            return false;
        }

        int newTotal;
        synchronized (transport) {
            if (!validateCargoOperation(transport) || quantity <= 0) {
                return false;
            }
            if (totalCargoUnits(transport) < quantity) {
                return false;
            }
            removeCargoUnits(transport, quantity);
            newTotal = totalCargoUnits(transport);
        }

        eventManager.handleSuccessfulEvent(EventType.CARGO_UNLOADED,
                quantity + " units unloaded from " + transport.getTransportID()
                        + " (now " + newTotal + " units)");

        return false;
    }

    public boolean transferCargo(Transport source, Transport destination, int quantity) {
        if (source == null || destination == null || source == destination) {
            return false;
        }

        Transport first;
        Transport second;
        if (source.getTransportID().compareTo(destination.getTransportID()) < 0) {
            first = source;
            second = destination;
        } else {
            first = destination;
            second = source;
        }

        synchronized (first) {
            synchronized (second) {
                if (!validateTransferOperation(source, destination, quantity)) {
                    return false;
                }
                List<Cargo> transferredCargo = removeCargoUnits(source, quantity);
                for (Cargo cargo : transferredCargo) {
                    destination.addCargo(cargo);
                }
            }
        }

        eventManager.handleSuccessfulEvent(EventType.CARGO_TRANSFERRED,
                quantity + " units transferred from " + source.getTransportID()
                        + " to " + destination.getTransportID());
        return true;
    }

    public Boolean validateCargoOperation(Transport transport) {
        if (transport == null || transport.getDockingBay() == null) {
            return false;
        }
        TransportState state = transport.getState();
        return state == TransportState.DOCKED || state == TransportState.READY;
    }

    public Boolean validateTransferOperation(Transport source, Transport destination, int quantity) {
        if (source == null || destination == null || source == destination) {
            return false;
        }
        if (!validateCargoOperation(source) || !validateCargoOperation(destination)) {
            return false;
        }
        if (quantity <= 0 || totalCargoUnits(source) < quantity) {
            return false;
        }
        return totalCargoUnits(destination) + quantity <= destination.getMaxCargoCapacity();
    }

    // Below are private helper methods
    private int totalCargoUnits(Transport transport) {
        int total = 0;
        for (Cargo cargo : transport.getCargo()) {
            total += cargo.getCargoUnits();
        }
        return total;
    }

    private List<Cargo> removeCargoUnits(Transport transport, int quantity) {
        List<Cargo> removedCargo = new ArrayList<>();
        int cargoUnitToRemove = quantity;
        for (Cargo cargo : transport.getCargo()) {
            if (cargoUnitToRemove == 0) {
                break;
            }
            transport.removeCargo(cargo);
            if (cargo.getCargoUnits() <= cargoUnitToRemove) {
                removedCargo.add(cargo);
                cargoUnitToRemove -= cargo.getCargoUnits();
            } else {
                removedCargo.add(new Cargo(cargo.getCargoID(), cargoUnitToRemove));
                transport.addCargo(new Cargo(cargo.getCargoID(), cargo.getCargoUnits() - cargoUnitToRemove));
                cargoUnitToRemove = 0;
            }
        }
        return removedCargo;
    }

    private synchronized String newCargoId() {
        String cargoId = "C" + nextCargoIdNumber;
        nextCargoIdNumber++;
        return cargoId;
    }

    private Integer readQuantity(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt(((String) value).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Transport findTransport(Object id) {
        if (id == null) {
            return null;
        }
        return transportService.getTransport(id.toString());
    }
}
