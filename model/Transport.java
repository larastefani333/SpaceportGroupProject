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

    public String getTransportID() {
        return transportID;
    }

    public TransportState getState() {
        return state;
    }

    public int getMaxCargoCapacity() {
        return maxCargoCapacity;
    }

    public void setState(TransportState state) {
        this.state = state;
    }

    public LocalDateTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(LocalDateTime scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public DockingBay getDockingBay() {
        return dockingBay;
    }

    public void setDockingBay(DockingBay dockingBay) {
        this.dockingBay = dockingBay;
    }

    public MaintenanceTeam getMaintenanceTeam() {
        return maintenanceTeam;
    }

    public void setMaintenanceTeam(MaintenanceTeam maintenanceTeam) {
        this.maintenanceTeam = maintenanceTeam;
    }

    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    public void addCargo(Cargo cargoItem) {
        cargo.add(cargoItem);
    }

    public void removeCargo(Cargo cargoItem) {
        cargo.remove(cargoItem);
    }
}