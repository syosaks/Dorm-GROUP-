package com.example.dorm.shared;

public abstract class BaseEntity implements Displayable {
    protected int id;

    public BaseEntity() {}

    public BaseEntity(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public abstract String getDisplayName();
}
