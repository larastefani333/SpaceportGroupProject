package model;

public abstract class User {
    private final String userID;
    private final String username;

    public User(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }
} 