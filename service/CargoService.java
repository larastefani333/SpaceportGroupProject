package service;

import communication.Request;
import communication.Response;
import model.Cargo;
import model.Transport;
import model.TransportState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CargoService implements Service {

    private final TransportService transportService;
    private int nextCargoIdNumber = 1;

    public CargoService(TransportService transportService) {
        this.transportService = transportService;
    }

    @Override
    public Response handleRequest(Request request) {
        Map<String, Object> params = request.getParameters();
        Integer CargoUnits = readCargoUnits(params.get("Cargo units"));

        if (CargoUnits == null) {
            return new Response(false, "Cargo units must be a whole number.", null);
        }

        switch (request.getEventType()) {
            case CARGO_LOADED: {
                Transport transport = findTransport(params.get("transportId"));
                if (transport == null) {
                    return new Response(false, "Transport not found.", null);
                }
                if (loadCargo(transport, CargoUnits)) {
                    return new Response(true, CargoUnits + " units loaded onto " + transport.getTransportID()
                            + ". Cargo is now " + totalCargoUnits(transport) + "/" + transport.getMaxCargoCapacity() + ".",
                            null);
                }
                return new Response(false, "Load failed", null);
            }
            case CARGO_UNLOADED: {
                Transport transport = findTransport(params.get("transportId"));
                if (transport == null) {
                    return new Response(false, "Transport not found.", null);
                }
                if (unloadCargo(transport, CargoUnits)) {
                    return new Response(true, CargoUnits + " units unloaded from " + transport.getTransportID()
                            + ". Cargo is now " + totalCargoUnits(transport) + "/" + transport.getMaxCargoCapacity() + ".",
                            null);
                }
                return new Response(false, "Unload failed", null);
            }
            case CARGO_TRANSFERRED:
                Transport source = findTransport(params.get("sourceTransportId"));
                Transport destination = findTransport(params.get("destinationTransportId"));
                if (source == null || destination == null) {
                    return new Response(false, "Source and/or destination transport not found", null);
                }
                if (source == destination) {
                    return new Response(false, "Source transport must be different from destination transport", null);
                }
                if (transferCargo(source, destination, CargoUnits)) {
                    return new Response(true, CargoUnits + " units transferred from " + source.getTransportID()
                            + " to " + destination.getTransportID() + ".", null);
                }
                return new Response(false, "Transfer failed", null);
            default:
                return new Response(false, "CargoService cannot handle " + request.getEventType() + ".", null);
        }
    }

    public boolean loadCargo(Transport transport, int CargoUnits) {
        if (transport == null) {
            return false;
        }

        Cargo cargo;
        int newTotal;
        synchronized (transport) {
            if (!validateCargoOperation(transport) || CargoUnits <= 0) {
                return false;
            }
            newTotal = totalCargoUnits(transport) + CargoUnits;
            if (newTotal > transport.getMaxCargoCapacity()) {
                return false;
            }
            cargo = new Cargo(newCargoId(), CargoUnits);
            transport.addCargo(cargo);
        }

        return true;
    }

    public boolean unloadCargo(Transport transport, int CargoUnits) {
        if (transport == null) {
            return false;
        }

        synchronized (transport) {
            if (!validateCargoOperation(transport) || CargoUnits <= 0) {
                return false;
            }
            if (totalCargoUnits(transport) < CargoUnits) {
                return false;
            }
            removeCargoUnits(transport, CargoUnits);
        }

        return true;
    }

    public boolean transferCargo(Transport source, Transport destination, int CargoUnits) {
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
                if (!validateTransferOperation(source, destination, CargoUnits)) {
                    return false;
                }
                List<Cargo> transferredCargo = removeCargoUnits(source, CargoUnits);
                for (Cargo cargo : transferredCargo) {
                    destination.addCargo(cargo);
                }
            }
        }
        return true;
    }

    public boolean validateCargoOperation(Transport transport) {
        if (transport == null || transport.getDockingBay() == null) {
            return false;
        }
        TransportState state = transport.getState();
        return state == TransportState.DOCKED || state == TransportState.READY;
    }

    public boolean validateTransferOperation(Transport source, Transport destination, int CargoUnits) {
        if (source == null || destination == null || source == destination) {
            return false;
        }
        if (!validateCargoOperation(source) || !validateCargoOperation(destination)) {
            return false;
        }
        if (CargoUnits <= 0 || totalCargoUnits(source) < CargoUnits) {
            return false;
        }
        return totalCargoUnits(destination) + CargoUnits <= destination.getMaxCargoCapacity();
    }

    // Below are private helper methods
    private int totalCargoUnits(Transport transport) {
        int total = 0;
        for (Cargo cargo : transport.getCargo()) {
            total += cargo.getCargoUnits();
        }
        return total;
    }

    private List<Cargo> removeCargoUnits(Transport transport, int CargoUnits) {
        List<Cargo> removedCargo = new ArrayList<>();
        int cargoUnitToRemove = CargoUnits;
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

    private Integer readCargoUnits(Object value) {
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
