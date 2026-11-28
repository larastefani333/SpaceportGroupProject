package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Transport implements Serializable {

    private final String transportID;
    private TransportState state;
    private DockingBay dockingBay;
    private MaintenanceTeam maintenanceTeam;
    private LocalDateTime scheduledDeparture;

    private int currentCargo;
    private final int maxCargoCapacity;

    public Transport(String transportId, LocalDateTime scheduledDeparture, int maxCargoCapacity) {
        this.transportID = transportId;
        this.scheduledDeparture = scheduledDeparture;
        this.state = TransportState.WAITING;

        this.currentCargo = 0;
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

    public synchronized int getCurrentCargo() {
        return currentCargo;
    }

    // Last line of defence: the transport itself refuses an impossible cargo amount,
    // even if a caller forgot to check. CargoService always checks first, so this
    // should never actually be triggered.
    public synchronized void setCurrentCargo(int currentCargo) {
        if (currentCargo < 0 || currentCargo > maxCargoCapacity) {
            throw new IllegalArgumentException("Cargo must be between 0 and " + maxCargoCapacity
                    + " units, but was " + currentCargo + ".");
        }
        this.currentCargo = currentCargo;
    }
}