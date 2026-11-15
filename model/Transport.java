package model;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transport {

    private final String transportID;
    private TransportState state;
    private DockingBay dockingBay;
    private MaintenanceTeam maintenanceTeam;
    private LocalDateTime scheduledDeparture;

    private final List<Cargo> cargo;
    private final int maxCargoCapacity;
    

    public Transport(String transportId, LocalDateTime scheduledDeparture, int maxCargoCapacity) {
        this.transportID = transportId;
        this.scheduledDeparture = scheduledDeparture;
        this.state = TransportState.WAITING;

        this.cargo = new ArrayList<>();
        this.maxCargoCapacity = maxCargoCapacity;
    }

    // below fields are final so no lock needed
    public String getTransportID() {
        return transportID;
    }

    public int getMaxCargoCapacity() {
        return maxCargoCapacity;
    }

    // below fields can be changed by different client threads so lock is needed
    public synchronized TransportState getState() {
        return state;
    }

    public synchronized void setState(TransportState state) {
        this.state = state;
    }

    public synchronized LocalDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    public synchronized void setScheduledDeparture(LocalDateTime scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public synchronized DockingBay getDockingBay() {
        return dockingBay;
    }

    public synchronized void setDockingBay(DockingBay dockingBay) {
        this.dockingBay = dockingBay;
    }

    public synchronized MaintenanceTeam getMaintenanceTeam() {
        return maintenanceTeam;
    }

    public synchronized void setMaintenanceTeam(MaintenanceTeam maintenanceTeam) {
        this.maintenanceTeam = maintenanceTeam;
    }

    public synchronized List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    public synchronized void addCargo(Cargo cargoItem) {
        cargo.add(cargoItem);
    }

    public synchronized void removeCargo(Cargo cargoItem) {
        cargo.remove(cargoItem);
    }
}