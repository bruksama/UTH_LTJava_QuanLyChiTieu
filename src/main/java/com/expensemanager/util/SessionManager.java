package main.java.com.expensemanager.util;

public class SessionManager {
    private static SessionManager instance;
    private int currentProfileId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public int getCurrentProfileId() {
        return currentProfileId;
    }

    public void setCurrentProfileId(int profileId) {
        this.currentProfileId = profileId;
    }
}