package communication;

import java.io.Serializable;

import model.RequestType;

public class Response implements Serializable {

    private final boolean success;
    private final String message;
    private final Object data;
    private final RequestType requestType;

    public Response(boolean success, String message, Object data) {
        this(success, message, data, null);
    }

    public Response(boolean success, String message, Object data, RequestType requestType) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.requestType = requestType;
    }

    public boolean isSuccessful() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}