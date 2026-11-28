package service;

import communication.Request;
import communication.Response;
import model.Administrator;
import model.Transport;
import model.TransportState;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 1) Creates transports and provides safe access to them for the rest of the system.
 * 2) Thread safety: every access to the transport list holds the lock on the transport list, so that no other thread can modify the list while it is being accessed.
 */
public class TransportService implements Service {

    private final List<Transport> transports = new ArrayList<>();


    /**
     * processes a transport request from client and returns the result.
     *
     * @param request
     * @return a response with either success message or failure message
     */
    @Override
    public Response handleRequest(Request request) {
        return switch (request.getEventType()) {
            case TRANSPORT_CREATED -> {
                if (!(request.getUser() instanceof Administrator)) {
                    yield new Response(false, "Unauthorised operation: only administrators can add transports.", null);
                }
                yield handleAddTransport(request.getParameters());
            }
            // TODO: reject all SYSTEM and CONNECTION requests centrally in SpaceportServer.handleRequest (using RequestType.getCategory()); this case can then be removed.
            case TRANSPORT_DEPARTED -> new Response(false,
                    "Departures are processed automatically by the server and cannot be requested.", null);
            default -> new Response(false, "TransportService cannot handle " + request.getEventType() + ".", null);
        };
    }

    /**
     * Adds new transport to the system, provided it is valid.
     *
     * @param transport
     * @return boolean indicating whether operation was successful.
     */
    public boolean addTransport(Transport transport) {
        synchronized (transports) {
            if (!validateTransport(transport)) {
                return false;
            }
            transports.add(transport);
        }
        return true;
    }

    /**
     * Checks that new transport is valid :it has a unique ID, a capacity > zero, and is in WAITING state.
     *
     * @param transport
     * @return boolean indicating whether the transport is valid
     */
    public boolean validateTransport(Transport transport) {
        if (transport == null) {
            return false;
        }
        String transportId = transport.getTransportID();
        if (transportId == null || transportId.isBlank()) {
            return false;
        }
        if (transport.getMaxCargoCapacity() <= 0) {
            return false;
        }
        LocalDateTime departure = transport.getScheduledDeparture();
        if (departure == null || !departure.isAfter(LocalDateTime.now())) {
            return false;
        }
        if (transport.getState() != TransportState.WAITING) {
            return false;
        }
        return getTransport(transportId) == null;
    }

    /**
     * Finds transport by its ID, case insensitive.
     *
     * TODO: confirm with team if this should be a case insensitive check.
     * @param transportId
     * @return matching transport if found, null if not.
     */
    public Transport getTransport(String transportId) {
        String id = readId(transportId);
        if (id == null) {
            return null;
        }
        synchronized (transports) {
            for (Transport transport : transports) {
                if (transport.getTransportID().equalsIgnoreCase(id)) {
                    return transport;
                }
            }
        }
        return null;
    }

    /**
     * @return a copy of the transport list, so callers can loop over it without holding the lock on the transport list.
     */
    public List<Transport> getTransports() {
        synchronized (transports) {
            return new ArrayList<>(transports);
        }
    }

    /**
     * @return a list of transports currently in READY state.
     * TODO: transports return can change state after this method returns, make sure caller checks again while holding transport's lock.
     */
    public List<Transport> getReadyTransports() {
        List<Transport> readyTransports = new ArrayList<>();
        for (Transport transport : getTransports()) {
            if (transport.getState() == TransportState.READY) {
                readyTransports.add(transport);
            }
        }
        return readyTransports;
    }

    /**
     * Builds new transport object with passed params, and adds it to list.
     *
     * @param params
     * @return response indicating whether operation was successful.
     */
    private Response handleAddTransport(Map<String, Object> params) {
        String id = readId(params.get("transportID"));
        if (id == null) {
            return new Response(false, "Transport ID is required.", null);
        }

        Integer capacity = readInt(params.get("maxCargoCapacity"));
        if (capacity == null) {
            return new Response(false, "Maximum cargo capacity must be a whole number.", null);
        }

        LocalDateTime departure = readDateTime(params.get("scheduledDeparture"));
        if (departure == null) {
            return new Response(false, "Scheduled departure must be a valid date and time.", null);
        }

        Transport transport = new Transport(id, departure, capacity);
        if (addTransport(transport)) {
            return new Response(true, "Transport " + id + " added (capacity " + capacity
                    + ", departs " + departure + ") and is waiting for a docking bay.", null);
        }
        return new Response(false, "Could not add transport. The ID must not already be in use, the capacity "
                + "must be greater than zero, and the departure must be in the future.", null);
    }

    /**
     * @param value
     * @return trimmed id, or null if it was missing or blank.
     */
    private String readId(Object value) {
        if (value == null || value.toString().isBlank()) {
            return null;
        }
        return value.toString().trim();
    }

    /**
     * Reads a whole number sent as an Integer or numeric String.
     *
     * @param value
     * @return the number, or null if it was missing, blank, or not a valid whole number.
     */
    private Integer readInt(Object value) {
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

    /**
     * Reads a date and time sent as a LocalDateTime or ISO 8601 formatted String.
     *
     * @param value
     * @return if valid, null if not.
     */
    private LocalDateTime readDateTime(Object value) {
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof String) {
            try {
                return LocalDateTime.parse(((String) value).trim());
            } catch (DateTimeException e) {
                return null;
            }
        }
        return null;
    }
}