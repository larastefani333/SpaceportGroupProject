package service;

import communication.Request;
import communication.Response;
import model.EventType;
import model.Transport;
import model.TransportState;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransportService implements Service {

    private final EventManager eventManager;
    private final DockingService dockingService;
    private final List<Transport> transports;

    public TransportService(EventManager eventManager, DockingService dockingService, List<Transport> transports) {
        this.eventManager = eventManager;
        this.dockingService = dockingService;
        this.transports = transports;
    }

    @Override
    public Response handleRequest(Request request) {
        return switch (request.getEventType()) {
            case TRANSPORT_CREATED -> handleAddTransport(request.getParameters());
            case TRANSPORT_DEPARTED -> handleDepartTransport(request.getParameters());
            default -> new Response(false, "TransportService cannot handle " + request.getEventType() + ".", null);
        };
    }

    public Boolean addTransport(Transport transport) {
        synchronized (transports) {
            if (!validateTransport(transport)) {
                return false;
            }
            transports.add(transport);
        }
        eventManager.handleSuccessfulEvent(EventType.TRANSPORT_CREATED,
                "Transport " + transport.getTransportID() + " created (capacity " + transport.getMaxCargoCapacity()
                        + ", departs " + transport.getScheduledDeparture() + ")");
        return true;
    }

    public boolean validateTransport(Transport transport) {
        if (transport == null) {
            return false;
        }
        String transportId = transport.getTransportID();
        if (transportId == null || transportId.isBlank()) {
            return false;
        }
        if (getTransport(transportId) != null) {
            return false;   // IDs must be unique
        }
        if (transport.getMaxCargoCapacity() <= 0) {
            return false;
        }
        LocalDateTime departureTime = transport.getScheduledDeparture();
        if (departureTime == null || !departureTime.isAfter(LocalDateTime.now())) {
            return false;
        }
        return transport.getState() == TransportState.WAITING;
    }

    public List<Transport> getReadyTransports() {
        List<Transport> temp;
        synchronized (transports) {
            temp = new ArrayList<>(transports);
        }

        List<Transport> readyTransports = new ArrayList<>();
        for (Transport transport : temp) {
            if (transport.getState() == TransportState.READY) {
                readyTransports.add(transport);
            }
        }
        return readyTransports;
    }

    public Transport getTransport(String transportId) {
        if (transportId == null) {
            return null;
        }
        synchronized (transports) {
            for (Transport transport : transports) {
                if (transport.getTransportID().equalsIgnoreCase(transportId.trim())) {
                    return transport;
                }
            }
            return null;
        }
    }

    private Response handleAddTransport(Map<String, Object> params) {
        Object idValue = params.get("trasnportId");
        String id = "";
        if (idValue != null) {
            id = idValue.toString().trim();
        }

        Integer capacity = readInt(params.get("maxCargoCapacity"));
        if (capacity == null) {
            return new Response(false, "Maximum cargo capacity must be a whole number.", null);
        }

        LocalDateTime departureTime = readDateTime(params.get("scheduledDeparture"));
        if (departureTime == null) {
            return new Response(false, "Departure must be a date and time like 2026-11-20T14:30.", null);
        }

        Transport transport = new Transport(id, departureTime, capacity);
        if (addTransport(transport)) {
            return new Response(true, "Transport " + id + " added and waiting for a docking bay.", null);
        }
        return new Response(false, "Could not add transport", null);
    }

    private Response handleDepartTransport(Map<String, Object> params) {
        Object idValue = params.get("transportId");
        Transport transport = null;
        if (idValue != null) {
            transport = getTransport(idValue.toString().trim());
        }
        if (transport == null) {
            return new Response(false, "Transport not found", null);
        }

        if (dockingService.processDeparture(transport)) {
            return new Response(true, "Transport " + transport.getTransportID() + " has departed.", null);
        }
        return new Response(false, "Transport " + transport.getTransportID() + " is " + transport.getState()
                + "; cannot depart", null);
    }

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
