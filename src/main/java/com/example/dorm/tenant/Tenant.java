package com.example.dorm.tenant;

import com.example.dorm.shared.BaseEntity;

public class Tenant extends BaseEntity {
    private String name;
    private String contactNumber;
    private String email;
    private int roomId;
    private int userId;

    public Tenant() {}

    public Tenant(int id, String name, String contactNumber, String email, int roomId, int userId) {
        super(id);
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.roomId = roomId;
        this.userId = userId;
    }

    @Override
    public String getDisplayName() { return name; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
