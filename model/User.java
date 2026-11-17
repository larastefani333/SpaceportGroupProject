package model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private final String userID;
    private final String username;

    public User(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public abstract UserRole getRole();

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

}
