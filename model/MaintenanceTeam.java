package model;

public class MaintenanceTeam {

    private final String teamID;
    private MaintenanceTeamStatus status;
    private Transport assignedTransport;

    public MaintenanceTeam(String teamID) {
        this.teamID = teamID;
        this.status = MaintenanceTeamStatus.AVAILABLE;
        this.assignedTransport = null;
    }

    public String getTeamID() {
        return teamID;
    }

    public MaintenanceTeamStatus getStatus() {
        return status;
    }

    public void setStatus(MaintenanceTeamStatus status) {
        this.status = status;
    }

    public Transport getAssignedTransport() {
        return assignedTransport;
    }

    public void setAssignedTransport(Transport transport) {
        this.assignedTransport = transport;
    }
}