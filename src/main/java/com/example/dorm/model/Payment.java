package com.example.dorm.model;

public class Payment {
    private int id;
    private int tenantId;
    private double amount;
    private String paymentDate;
    private String monthCovered;
    private String status;

    public Payment() {}

    public Payment(int id, int tenantId, double amount, String paymentDate, String monthCovered, String status) {
        this.id = id;
        this.tenantId = tenantId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.monthCovered = monthCovered;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public String getMonthCovered() { return monthCovered; }
    public void setMonthCovered(String monthCovered) { this.monthCovered = monthCovered; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
