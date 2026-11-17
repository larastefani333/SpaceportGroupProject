package model;

public class Client extends User {
    
    public Client(String userID, String username) {
        super(userID, username);
    }

    @Override
    public UserRole getRole() {
        return UserRole.CLIENT;
    }
}
