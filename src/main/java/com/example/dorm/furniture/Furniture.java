package com.example.dorm.furniture;

import com.example.dorm.shared.BaseEntity;

public class Furniture extends BaseEntity {
    private int roomId;
    private String itemType;
    private String condition;
    private String serialNumber;

    public Furniture() {}

    public Furniture(int id, int roomId, String itemType, String condition, String serialNumber) {
        super(id);
        this.roomId = roomId;
        this.itemType = itemType;
        this.condition = condition;
        this.serialNumber = serialNumber;
    }

    @Override
    public String getDisplayName() { return itemType + " (" + condition + ")"; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
}
