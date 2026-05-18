package com.example.dorm.visitor;

import com.example.dorm.shared.BaseEntity;

public class VisitorLog extends BaseEntity {
    private String visitorName;
    private int tenantId;
    private String visitDate;
    private String timeIn;
    private String timeOut;
    private String purpose;

    public VisitorLog() {}

    public VisitorLog(int id, String visitorName, int tenantId, String visitDate,
                      String timeIn, String timeOut, String purpose) {
        super(id);
        this.visitorName = visitorName;
        this.tenantId = tenantId;
        this.visitDate = visitDate;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.purpose = purpose;
    }

    @Override
    public String getDisplayName() { return visitorName + " - " + visitDate; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }
    public String getTimeIn() { return timeIn; }
    public void setTimeIn(String timeIn) { this.timeIn = timeIn; }
    public String getTimeOut() { return timeOut; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}
