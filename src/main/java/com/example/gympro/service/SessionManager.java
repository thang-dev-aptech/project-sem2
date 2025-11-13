package com.example.gympro.service;

import com.example.gympro.domain.Role;
import com.example.gympro.domain.User;

import java.util.Collections;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private List<Role> currentRoles = Collections.emptyList();

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public void startSession(User user, List<Role> roles) {
        this.currentUser = user;
        this.currentRoles = roles == null ? Collections.emptyList() : roles;
    }

    public void endSession() {
        this.currentUser = null;
        this.currentRoles = Collections.emptyList();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Role> getCurrentRoles() {
        return currentRoles;
    }

    public boolean hasRole(String roleName) {
        if (roleName == null || currentRoles == null) return false;
        return currentRoles.stream().anyMatch(r -> roleName.equalsIgnoreCase(r.getName()));
    }
}


