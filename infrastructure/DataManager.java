package infrastructure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import model.DockingBay;
import model.MaintenanceTeam;
import model.Transport;

public class DataManager {

    private List<Transport> transports;
    private List<DockingBay> dockingBays;
    private List<MaintenanceTeam> maintenanceTeams;
    private EventLog eventLog;

    public String loadSystemData() {
        // load system data
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("test.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content.toString();
        // can print to console instead if preferred
    }

    public void saveSystemData() {
        // save system data

    }

}
