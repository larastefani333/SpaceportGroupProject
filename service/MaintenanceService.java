package service;

import java.util.List;
import model.Transport;
import model.MaintenanceTeam;

public class MaintenanceService {

    private EventManager eventManager;

    public Boolean assignTeam(Transport transport, MaintenanceTeam team) {
        // assign maintenance team to transport
        return null;
    }

    public Boolean completeMaintenance(Transport transport) {
        // complete transport maintenance
        return null;
    }

    public Boolean validateAssignment(Transport transport, MaintenanceTeam team) {
        // validate maintenance team assignment
        return null;
    }

    public List<MaintenanceTeam> getTeams() {
        // retrieve maintenance teams
        return null;
    }

    public boolean registerTeam(String teamID) {
        // register maintenance team
        return false;
    }

    public boolean activateTeam(String teamID) {
        // activate maintenance team
        return false;
    }

    public boolean deactivateTeam(String teamID) {
        // deactivate maintenance team
        return false;
    }

}
