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

        String escapedEventType = escape(event.getEventType().getDisplayName());
        String escapedUser  = escape(event.getUser().getUsername());
        String escapedTimestamp = escape(event.getTimestamp().toString());
        String escapedDescription = escape(event.getDescription());

        String filename = createFile();
        try (FileWriter myWriter = new FileWriter(filename))
        {
            myWriter.write("{\n");
            myWriter.write("  \"" + eventID + "\": {\n");
            myWriter.write(String.format("\"Event Type\"" + ": " + "\"%s\",", escapedEventType).indent(4));
            myWriter.write(String.format("\"User\"" + ": " + "\"%s\",", escapedUser).indent(4));
            myWriter.write(String.format("\"Timestamp\"" + ": " + "\"%s\",", escapedTimestamp).indent(4));
            myWriter.write(String.format("\"Description\"" + ": " + "\"%s\"", escapedDescription).indent(4));
            myWriter.write(String.format("}\n").indent(2));
            myWriter.write("}");
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String escape(String text) {
        if (text == null)
        {
            return "";
        }
        return text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
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
