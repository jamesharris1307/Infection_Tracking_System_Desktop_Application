package com.example.infection_monitoring_system_desktop_application.Util;

import com.example.infection_monitoring_system_desktop_application.Model.User;

public class SessionManager {

    private static final SessionManager instance = new SessionManager();

    private User currentUser;

    private SessionManager() { }

    public static SessionManager getInstance() {
        return instance;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }
}
