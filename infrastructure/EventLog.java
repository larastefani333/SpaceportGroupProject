package infrastructure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import model.Event;
import model.RequestType;
import model.User;

public class EventLog {

    private List<Event> event;

    public void logEvent(Event event) {
        // record event
        String eventID = event.getEventID().toString();
        RequestType eventType = event.getEventType();
        User user = event.getUser();
        LocalDateTime timestamp = event.getTimestamp();
        String description = event.getDescription();

        try
        {
            String filename = createFile();
            FileWriter myWriter = new FileWriter(filename);
            String json = formatJson(event);
            myWriter.write(json);
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String formatJson(Event event) {
        String json = """
            {
                "%s": {
                    "Event Type": "%s",
                    "User": "%s",
                    "Timestamp": "%s",
                    "Description": "%s"
                }
            }
            """.formatted(
                event.getEventID(),
                event.getEventType().getDisplayName(),
                event.getUser().getUsername(),
                event.getTimestamp(),
                event.getDescription()
        );
        return json;
    }

    public String createFile()
    {
        String filename = "";
        try {
            File myObj = new File("test.json");
            filename = myObj.getPath();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return filename;
    }

    public List<Event> getEventHistory() {
        // get event history
        return null;
    }

    public List<Event> getEventsByType(RequestType eventType) {
        // get events by type
        return null;
    }

}
