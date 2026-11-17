package model;

public class Administrator extends User {

    public Administrator(String userID, String username) {
        super(userID, username);
    }

    @Override
    public UserRole getRole() {
        return UserRole.ADMINISTRATOR;
    }

}
