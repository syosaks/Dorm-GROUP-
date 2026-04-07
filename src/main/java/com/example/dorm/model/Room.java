package com.example.dorm.model;

public class Room {
    private int id;
    private String roomNumber;
    private int capacity;
    private String status;
    private double monthlyRate;

    public Room() {}

    public Room(int id, String roomNumber, int capacity, String status, double monthlyRate) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.status = status;
        this.monthlyRate = monthlyRate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getMonthlyRate() { return monthlyRate; }
    public void setMonthlyRate(double monthlyRate) { this.monthlyRate = monthlyRate; }
}
