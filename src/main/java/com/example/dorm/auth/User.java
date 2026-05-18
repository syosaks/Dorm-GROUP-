package com.example.dorm.auth;

import com.example.dorm.shared.BaseEntity;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String role;

    public User() {}

    public User(int id, String username, String password, String role) {
        super(id);
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public String getDisplayName() { return username + " (" + role + ")"; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
