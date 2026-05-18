package com.example.dorm.reservation;

import com.example.dorm.shared.BaseEntity;

public class Reservation extends BaseEntity {
    private int tenantId;
    private int roomId;
    private String requestDate;
    private String status;

    public Reservation() {}

    public Reservation(int id, int tenantId, int roomId, String requestDate, String status) {
        super(id);
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.requestDate = requestDate;
        this.status = status;
    }

    @Override
    public String getDisplayName() { return "Reservation #" + id + " - " + status; }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
