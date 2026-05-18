package com.example.dorm.tenant;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class TenantDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Tenant> getAllTenants() {
        List<Tenant> tenants = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tenants ORDER BY name")) {
            while (rs.next()) tenants.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return tenants;
    }

    /** Returns only tenants that have no room assigned yet. */
    public List<Tenant> getUnassignedTenants() {
        List<Tenant> tenants = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM tenants WHERE room_id IS NULL OR room_id = 0 ORDER BY name")) {
            while (rs.next()) tenants.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return tenants;
    }

    public Tenant getByUserId(int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT * FROM tenants WHERE user_id = ?")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updateRoomId(int tenantId, int roomId) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE tenants SET room_id = ? WHERE id = ?")) {
            ps.setInt(1, roomId);
            ps.setInt(2, tenantId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Removes room assignment from tenant and is called alongside roomDAO.updateStatus(AVAILABLE). */
    public void vacateRoom(int tenantId) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE tenants SET room_id = NULL WHERE id = ?")) {
            ps.setInt(1, tenantId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void addTenant(String name, String contact, String email, int userId) {
        try (PreparedStatement ps = conn().prepareStatement(
            "INSERT INTO tenants (name, contact_number, email, user_id) VALUES (?,?,?,?)")) {
            ps.setString(1, name);
            ps.setString(2, contact);
            ps.setString(3, email);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Returns current room assignments.
     * Array layout: [0]=tenantName, [1]=roomNumber, [2]=capacity, [3]=monthlyRate,
     *               [4]=tenantId (hidden), [5]=roomId (hidden)
     */
    public List<String[]> getAssignmentsDisplay() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT t.id AS tid, r.id AS rid, t.name, r.room_number, r.capacity, r.monthly_rate " +
            "FROM tenants t JOIN rooms r ON t.room_id = r.id ORDER BY t.name";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("room_number"),
                    String.valueOf(rs.getInt("capacity")),
                    String.format("%.2f", rs.getDouble("monthly_rate")),
                    String.valueOf(rs.getInt("tid")),
                    String.valueOf(rs.getInt("rid"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Returns all tenants joined with their user account and room (for admin view).
     * Array layout: [0]=name, [1]=username, [2]=contact, [3]=email, [4]=room (or "Unassigned")
     */
    public List<String[]> getTenantsWithDetails() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT t.name, u.username, t.contact_number, t.email, " +
            "COALESCE(r.room_number, 'Unassigned') AS room " +
            "FROM tenants t " +
            "JOIN users u ON t.user_id = u.id " +
            "LEFT JOIN rooms r ON t.room_id = r.id " +
            "ORDER BY t.name";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("room")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Tenant map(ResultSet rs) throws SQLException {
        return new Tenant(rs.getInt("id"), rs.getString("name"),
            rs.getString("contact_number"), rs.getString("email"),
            rs.getInt("room_id"), rs.getInt("user_id"));
    }
}
