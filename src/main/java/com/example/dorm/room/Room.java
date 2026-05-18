package com.example.dorm.room;

import com.example.dorm.shared.BaseEntity;

public class Room extends BaseEntity {
    private String roomNumber;
    private int capacity;
    private String status;
    private double monthlyRate;
    private int floor;
    private int buildingId;

    public Room() {}

    public Room(int id, String roomNumber, int capacity, String status, double monthlyRate,
                int floor, int buildingId) {
        super(id);
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.status = status;
        this.monthlyRate = monthlyRate;
        this.floor = floor;
        this.buildingId = buildingId;
    }

    public Room(int id, String roomNumber, int capacity, String status, double monthlyRate) {
        this(id, roomNumber, capacity, status, monthlyRate, 1, 1);
    }

    @Override
    public String getDisplayName() { return "Room " + roomNumber; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(double monthlyRate) { this.monthlyRate = monthlyRate; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public int getBuildingId() { return buildingId; }
    public void setBuildingId(int buildingId) { this.buildingId = buildingId; }
}
