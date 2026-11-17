package service;

import java.util.List;
import java.util.ArrayList;
import communication.Request;
import communication.Response;
import model.MaintenanceTeam;
import model.Transport;
import model.MaintenanceTeamStatus;
import model.TransportState;

public class MaintenanceService implements Service {

    @Override
    public Response handleRequest(Request request) {
        //process maintenance requests from clients
        throw new UnsupportedOperationException("Maintenance request handling not yet implemented");
    }

    private final List<MaintenanceTeam> teams = new ArrayList<>();

    public synchronized Boolean assignTeam(Transport transport, MaintenanceTeam team) {
        // validate assignment
        if (!validateAssignment(transport, team)) {
            return false;
        }

        //link team and transport
        transport.setMaintenanceTeam(team);
        team.setAssignedTransport(transport);

        //update statuses
        transport.setState(TransportState.SERVICING);
        team.setStatus(MaintenanceTeamStatus.BUSY);

        return true;
    }

    public synchronized Boolean completeMaintenance(Transport transport) {
        // check transport exists and is being serviced
        if (transport == null || transport.getState() != TransportState.SERVICING) {
            return false;
        }

        //get assigned team
        MaintenanceTeam team = transport.getMaintenanceTeam();

        //check team exists
        if (team == null) {
            return false;
        }

        // check team's registered & assignment valid
        if (!teams.contains(team) || team.getAssignedTransport() != transport || team.getStatus() != MaintenanceTeamStatus.BUSY) {
            return false;
        }

        //remove assignment
        transport.setMaintenanceTeam(null);
        team.setAssignedTransport(null);

        //update statuses
        transport.setState(TransportState.READY);
        team.setStatus(MaintenanceTeamStatus.AVAILABLE);

        return true;
    }

    public synchronized Boolean validateAssignment(Transport transport, MaintenanceTeam team) {
        // check transport & team exist
        if (transport == null || team == null) {
            return false;
        }
        //check team's registered
        if (!teams.contains(team)) {
            return false;
        }

        //check team's available
        if (team.getStatus() != MaintenanceTeamStatus.AVAILABLE) {
            return false;
        }

        //check if transport is docked
        if (transport.getState() != TransportState.DOCKED) {
            return false;
        }

        //check they aren't already assigned
        if (transport.getMaintenanceTeam() != null || team.getAssignedTransport() != null) {
            return false;
        }
        return true;
    }

    public synchronized List<MaintenanceTeam> getTeams() {
        //return a copy of registered teams
        return new ArrayList<>(teams);
    }

    public synchronized boolean registerTeam(String teamID) {
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

    public synchronized boolean activateTeam(String teamID) {
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

    public synchronized boolean deactivateTeam(String teamID) {
        //find team
        for (MaintenanceTeam team : teams) {
            if (team.getTeamID().equals(teamID)) {

                //deactivate available team -- assuming can only be deactivated only when available
                if (team.getStatus() != MaintenanceTeamStatus.AVAILABLE) {
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
