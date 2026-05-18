package com.example.dorm.maintenance;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class MaintenanceDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Returns ACTIVE (PENDING + IN_PROGRESS) requests joined with tenant name.
     * Array: [0]=tenantName, [1]=description, [2]=requestDate, [3]=status, [4]=id(hidden), [5]=priority
     */
    public List<String[]> getAllActiveForDisplay() {
        return queryDisplay(
            "SELECT mr.id, t.name, mr.description, mr.request_date, mr.status, mr.priority " +
            "FROM maintenance_requests mr " +
            "JOIN tenants t ON mr.tenant_id = t.id " +
            "WHERE mr.status != 'RESOLVED' " +
            "ORDER BY CASE mr.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END, mr.request_date DESC",
            null);
    }

    /**
     * Returns RESOLVED records for admin/landlord report view.
     * Array: [0]=tenantName, [1]=description, [2]=requestDate, [3]=status, [4]=id(hidden), [5]=priority
     */
    public List<String[]> getAllResolvedForDisplay() {
        return queryDisplay(
            "SELECT mr.id, t.name, mr.description, mr.request_date, mr.status, mr.priority " +
            "FROM maintenance_requests mr " +
            "JOIN tenants t ON mr.tenant_id = t.id " +
            "WHERE mr.status = 'RESOLVED' " +
            "ORDER BY mr.request_date DESC",
            null);
    }

    /** Active requests for a specific tenant (PENDING + IN_PROGRESS). */
    public List<String[]> getActiveByTenantId(int tenantId) {
        return queryDisplay(
            "SELECT mr.id, t.name, mr.description, mr.request_date, mr.status, mr.priority " +
            "FROM maintenance_requests mr " +
            "JOIN tenants t ON mr.tenant_id = t.id " +
            "WHERE mr.tenant_id = ? AND mr.status != 'RESOLVED' " +
            "ORDER BY mr.request_date DESC",
            tenantId);
    }

    /** Resolved requests for a specific tenant. */
    public List<String[]> getResolvedByTenantId(int tenantId) {
        return queryDisplay(
            "SELECT mr.id, t.name, mr.description, mr.request_date, mr.status, mr.priority " +
            "FROM maintenance_requests mr " +
            "JOIN tenants t ON mr.tenant_id = t.id " +
            "WHERE mr.tenant_id = ? AND mr.status = 'RESOLVED' " +
            "ORDER BY mr.request_date DESC",
            tenantId);
    }

    private List<String[]> queryDisplay(String sql, Integer tenantId) {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn().prepareStatement(sql);
            if (tenantId != null) ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String priority = "MEDIUM";
                try { priority = rs.getString("priority"); if (priority == null) priority = "MEDIUM"; }
                catch (SQLException ignored) {}
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("request_date"),
                    rs.getString("status"),
                    String.valueOf(rs.getInt("id")),
                    priority
                });
            }
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void add(MaintenanceRequest r) {
        String priority = r.getPriority() != null ? r.getPriority() : "MEDIUM";
        String sql = "INSERT INTO maintenance_requests (tenant_id, description, request_date, status, priority) " +
            "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, r.getTenantId());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getRequestDate());
            ps.setString(4, r.getStatus());
            ps.setString(5, priority);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateStatus(int id, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE maintenance_requests SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
