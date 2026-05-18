package com.example.dorm.building;

import com.example.dorm.shared.BaseEntity;

public class DormBuilding extends BaseEntity {
    private String buildingName;
    private int totalFloors;
    private String address;

    public DormBuilding() {}

    public DormBuilding(int id, String buildingName, int totalFloors, String address) {
        super(id);
        this.buildingName = buildingName;
        this.totalFloors = totalFloors;
        this.address = address;
    }

    @Override
    public String getDisplayName() { return buildingName; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public int getTotalFloors() { return totalFloors; }
    public void setTotalFloors(int totalFloors) { this.totalFloors = totalFloors; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() { return buildingName; }
}
