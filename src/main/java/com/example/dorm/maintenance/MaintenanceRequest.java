package com.example.dorm.maintenance;

import com.example.dorm.shared.BaseEntity;

public class MaintenanceRequest extends BaseEntity {
    private int tenantId;
    private String description;
    private String requestDate;
    private String status;
    private String priority;

    public MaintenanceRequest() {}

    public MaintenanceRequest(int id, int tenantId, String description,
                              String requestDate, String status, String priority) {
        super(id);
        this.tenantId = tenantId;
        this.description = description;
        this.requestDate = requestDate;
        this.status = status;
        this.priority = priority;
    }

    @Override
    public String getDisplayName() { return "[" + priority + "] " + description; }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
