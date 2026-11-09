package model;

public class DockingBay {

    private final String bayID;
    private DockingBayStatus status;
    private Transport assignedTransport;

    public DockingBay(String bayID) {
        this.bayID = bayID;
        this.status = DockingBayStatus.AVAILABLE;
        this.assignedTransport = null;
    }

    public String getBayID() {
        return bayID;
    }

    public DockingBayStatus getStatus() {
        return status;
    }

    public void setStatus(DockingBayStatus status) {
        this.status = status;
    }

    public Transport getAssignedTransport() {
        return assignedTransport;
    }

    public void setAssignedTransport(Transport transport) {
        this.assignedTransport = transport;
    }
}