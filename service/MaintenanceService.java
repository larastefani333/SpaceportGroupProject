package service;

import java.util.List;
import java.util.ArrayList;
import model.Transport;
import model.MaintenanceTeam;
import model.MaintenanceTeamStatus;

public class MaintenanceService {

    private EventManager eventManager;

    private final List<MaintenanceTeam> teams = new ArrayList<>();

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
        //return a copy of registered teams
        return new ArrayList<>(teams);
    }

    public boolean registerTeam(String teamID) {
        // validate teamid
        if (teamID == null || teamID.isBlank()) {
            return false;
        }

        //check for duplicate teamid
        for (MaintenanceTeam team : teams) {
            if (team.getTeamID().equals(teamID)) {
                return false;
            }
        }

        // register team
        MaintenanceTeam team = new MaintenanceTeam(teamID);
        teams.add(team);

        return true;

    }

    public boolean activateTeam(String teamID) {
        //find matching team
        for (MaintenanceTeam team : teams) {
            if (team.getTeamID().equals(teamID)) {
                //only activate teams that r inactive
                if (team.getStatus() != MaintenanceTeamStatus.INACTIVE) {
                    return false;
                }

                team.setStatus(MaintenanceTeamStatus.AVAILABLE);
                return true;

            }
        }

        //team not found
        return false;

    }

    public boolean deactivateTeam(String teamID) {
        //find team
        for (MaintenanceTeam team : teams) {
            if (team.getTeamID().equals(teamID)) {

                //deactivate available team
                if (team.getStatus() == MaintenanceTeamStatus.AVAILABLE) {
                    return false;
                }

                team.setStatus(MaintenanceTeamStatus.INACTIVE);
                return true;
            }
        }
        //team doesnt exist
        return false;
    }
}
