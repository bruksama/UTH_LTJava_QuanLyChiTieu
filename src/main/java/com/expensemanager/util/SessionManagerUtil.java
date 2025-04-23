package main.java.com.expensemanager.util;

public class SessionManagerUtil {
    private static SessionManagerUtil instance;
    private int currentProfileId;
    private String currentProfileName;

    private SessionManagerUtil() {}

    public static SessionManagerUtil getInstance() {
        if (instance == null) {
            instance = new SessionManagerUtil();
        }
        return instance;
    }

    public int getCurrentProfileId() {
        return currentProfileId;
    }

    public void setCurrentProfileId(int profileId) {
        this.currentProfileId = profileId;
    }

    public void setCurrentProfileName(String profileName) {
        this.currentProfileName = profileName;
    }

    public String getCurrentProfileName() {
        return currentProfileName;
    }
}