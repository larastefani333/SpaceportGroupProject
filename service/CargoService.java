package service;

import communication.Request;
import communication.Response;
import model.Transport;
import model.TransportState;

import java.util.Map;

/**
 * 1) Handles cargo operations: loading, unloading and transferring cargo between transports.
 * 2) Cargo is stored on each transport as a number of units.
 * 3) Thread safety: each operation locks only the transport(s) being used for that operation, so operations on different transports can run in parallel.
 */
public class CargoService implements Service {

    private final TransportService transportService;

    public CargoService(TransportService transportService) {
        this.transportService = transportService;
    }

    /**
     * Processes cargo request from a client and returns the result.
     *
     * @param request
     * @return a response with either success message or failure message
     */
    @Override
    public Response handleRequest(Request request) {
        Map<String, Object> params = request.getParameters();

        Integer cargoUnits = readCargoUnits(params.get("cargoUnits"));
        if (cargoUnits == null || cargoUnits <= 0) {
            return new Response(false, "Cargo units must be a whole number greater than zero.", null);
        }

        return switch (request.getEventType()) {
            case CARGO_LOADED -> {
                Transport transport = findTransport(params.get("transportID"));
                if (transport == null) {
                    yield new Response(false, "Transport not found.", null);
                }
                if (loadCargo(transport, cargoUnits)) {
                    yield new Response(true, cargoUnits + " units loaded onto " + transport.getTransportID()
                            + ". Cargo is now " + transport.getCurrentCargo() + "/"
                            + transport.getMaxCargoCapacity() + ".", null);
                }
                yield new Response(false, "Load failed. The transport must be DOCKED, SERVICING or READY "
                        + "and have room for " + cargoUnits + " more units.", null);
            }
            case CARGO_UNLOADED -> {
                Transport transport = findTransport(params.get("transportID"));
                if (transport == null) {
                    yield new Response(false, "Transport not found.", null);
                }
                if (unloadCargo(transport, cargoUnits)) {
                    yield new Response(true, cargoUnits + " units unloaded from " + transport.getTransportID()
                            + ". Cargo is now " + transport.getCurrentCargo() + "/"
                            + transport.getMaxCargoCapacity() + ".", null);
                }
                yield new Response(false, "Unload failed. The transport must be DOCKED, SERVICING or READY "
                        + "and hold at least " + cargoUnits + " units.", null);
            }
            case CARGO_TRANSFERRED -> {
                Transport source = findTransport(params.get("sourceTransportID"));
                Transport destination = findTransport(params.get("destinationTransportID"));
                if (source == null || destination == null) {
                    yield new Response(false, "Source and/or destination transport not found.", null);
                }
                if (source == destination) {
                    yield new Response(false, "Source transport must be different from destination transport.", null);
                }
                if (transferCargo(source, destination, cargoUnits)) {
                    yield new Response(true, cargoUnits + " units transferred from " + source.getTransportID()
                            + " to " + destination.getTransportID() + ".", null);
                }
                yield new Response(false, "Transfer failed. Both transports must be DOCKED, SERVICING or READY, "
                        + "the source must hold " + cargoUnits + " units and the destination must have room.", null);
            }
            default -> new Response(false, "CargoService cannot handle " + request.getEventType() + ".", null);
        };
    }


    /**
     * Loads cargo onto a transport, provided the transport is in a state that allows loading and has enough capacity.
     *
     * @param transport
     * @param cargoUnits
     * @return boolean indicating if operation was successful.
     */
    public boolean loadCargo(Transport transport, int cargoUnits) {
        if (transport == null || cargoUnits <= 0) {
            return false;
        }
        synchronized (transport) {
            if (!validateCargoOperation(transport)) {
                return false;
            }
            int newTotal = transport.getCurrentCargo() + cargoUnits;
            if (newTotal > transport.getMaxCargoCapacity()) {
                return false;
            }
            transport.setCurrentCargo(newTotal);
        }
        return true;
    }

    /**
     * Unloads cargo from a transport, provided the transport is in a state that allows unloading and has enough cargo to remove requested amount.
     *
     * @param transport
     * @param cargoUnits
     * @return boolean indicating if operation was successful.
     */
    public boolean unloadCargo(Transport transport, int cargoUnits) {
        if (transport == null || cargoUnits <= 0) {
            return false;
        }
        synchronized (transport) {
            if (!validateCargoOperation(transport)) {
                return false;
            }
            int newTotal = transport.getCurrentCargo() - cargoUnits;
            if (newTotal < 0) {
                return false;
            }
            transport.setCurrentCargo(newTotal);
        }
        return true;
    }

    /**
     * Move cargo from one transport to another. The transfer either fully succeeds or transfers nothing to avoid cargo being lost or duplicated.
     *
     * @param source
     * @param destination
     * @param cargoUnits
     * @return boolean indicating if operation was successful.
     */
    public boolean transferCargo(Transport source, Transport destination, int cargoUnits) {
        if (source == null || destination == null || source == destination || cargoUnits <= 0) {
            return false;
        }

        Transport tempOne;
        Transport tempTwo;
        if (source.getTransportID().compareTo(destination.getTransportID()) < 0) {
            tempOne = source;
            tempTwo = destination;
        } else {
            tempOne = destination;
            tempTwo = source;
        }

        synchronized (tempOne) {
            synchronized (tempTwo) {
                if (!validateTransferOperation(source, destination, cargoUnits)) {
                    return false;
                }
                source.setCurrentCargo(source.getCurrentCargo() - cargoUnits);
                destination.setCurrentCargo(destination.getCurrentCargo() + cargoUnits);
            }
        }
        return true;
    }

    /**
     * Checks that transport is in a state that allows cargo operations (loading or unloading).
     *
     * @param transport
     * @return boolean indicating if operation can occur.
     */
    public boolean validateCargoOperation(Transport transport) {
        if (transport == null || transport.getDockingBay() == null) {
            return false;
        }
        TransportState state = transport.getState();
        return state == TransportState.DOCKED
                || state == TransportState.SERVICING
                || state == TransportState.READY;
    }

    /**
     * Checks that transfer is possible: both transports are in valid state, the source has enough cargo and the destination has capacity.
     *
     * @param source
     * @param destination
     * @param cargoUnits
     * @return boolean indicating if transfer can occur.
     */
    public boolean validateTransferOperation(Transport source, Transport destination, int cargoUnits) {
        if (source == null || destination == null || source == destination || cargoUnits <= 0) {
            return false;
        }
        if (!validateCargoOperation(source) || !validateCargoOperation(destination)) {
            return false;
        }
        if (source.getCurrentCargo() < cargoUnits) {
            return false;
        }
        return destination.getCurrentCargo() + cargoUnits <= destination.getMaxCargoCapacity();
    }

    /**
     * Looks up transport by its ID (case-insensitive).
     *
     * @param id
     * @return transport if found, null if not.
     * todo: check with team if this should be a case insensitive check or not?
     */
    private Transport findTransport(Object id) {
        if (id == null) {
            return null;
        }
        return transportService.getTransport(id.toString());
    }

    /**
     * Reads a cargo amount in request as an Integer or a numeric String.
     *
     * @param value
     * @return Integer if valid, null if not.
     */
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
}