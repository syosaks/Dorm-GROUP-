package com.example.dorm.visitor;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class VisitorLogDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Returns all visitor logs joined with resident (tenant) name.
     * Array: [0]=visitorName, [1]=residentName, [2]=visitDate, [3]=timeIn, [4]=timeOut, [5]=purpose, [6]=id(hidden)
     */
    public List<String[]> getAllForDisplay() {
        return queryDisplay(
            "SELECT vl.id, vl.visitor_name, t.name AS resident_name, vl.visit_date, " +
            "vl.time_in, vl.time_out, vl.purpose " +
            "FROM visitor_logs vl " +
            "JOIN tenants t ON vl.tenant_id = t.id " +
            "ORDER BY vl.visit_date DESC, vl.time_in DESC",
            null);
    }

    /** Returns visitor logs for a specific tenant. */
    public List<String[]> getByTenantId(int tenantId) {
        return queryDisplay(
            "SELECT vl.id, vl.visitor_name, t.name AS resident_name, vl.visit_date, " +
            "vl.time_in, vl.time_out, vl.purpose " +
            "FROM visitor_logs vl " +
            "JOIN tenants t ON vl.tenant_id = t.id " +
            "WHERE vl.tenant_id = ? " +
            "ORDER BY vl.visit_date DESC, vl.time_in DESC",
            tenantId);
    }

    private List<String[]> queryDisplay(String sql, Integer tenantId) {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn().prepareStatement(sql);
            if (tenantId != null) ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tout = rs.getString("time_out");
                list.add(new String[]{
                    rs.getString("visitor_name"),
                    rs.getString("resident_name"),
                    rs.getString("visit_date"),
                    rs.getString("time_in"),
                    tout != null && !tout.isEmpty() ? tout : "—",
                    rs.getString("purpose"),
                    String.valueOf(rs.getInt("id"))
                });
            }
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void add(VisitorLog v) {
        String sql = "INSERT INTO visitor_logs (visitor_name, tenant_id, visit_date, time_in, time_out, purpose) " +
            "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, v.getVisitorName());
            ps.setInt(2, v.getTenantId());
            ps.setString(3, v.getVisitDate());
            ps.setString(4, v.getTimeIn());
            ps.setString(5, v.getTimeOut());
            ps.setString(6, v.getPurpose());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Updates time_out for a log entry (resident can log when visitor leaves). */
    public void updateTimeOut(int id, String timeOut) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE visitor_logs SET time_out = ? WHERE id = ?")) {
            ps.setString(1, timeOut);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
