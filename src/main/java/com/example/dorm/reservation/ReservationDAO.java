package com.example.dorm.reservation;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ReservationDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Returns all reservations joined with tenant and room data.
     * Array layout: [0]=id, [1]=tenantName, [2]=roomNumber, [3]=requestDate,
     *               [4]=status, [5]=tenantId (hidden), [6]=roomId (hidden)
     */
    public List<String[]> getReservationsForDisplay() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT r.id, t.name, ro.room_number, r.request_date, r.status, " +
            "r.tenant_id, r.room_id " +
            "FROM reservations r " +
            "JOIN tenants t ON r.tenant_id = t.id " +
            "JOIN rooms ro ON r.room_id = ro.id " +
            "ORDER BY r.request_date DESC";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("name"),
                    rs.getString("room_number"),
                    rs.getString("request_date"),
                    rs.getString("status"),
                    String.valueOf(rs.getInt("tenant_id")),
                    String.valueOf(rs.getInt("room_id"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean hasPendingReservation(int tenantId) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT COUNT(*) FROM reservations WHERE tenant_id = ? AND status = 'PENDING'")) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Returns [roomNumber, status, requestDate] for the tenant's latest
     * non-rejected reservation, or null if none exists.
     */
    public String[] getLatestReservationForTenant(int tenantId) {
        String sql = "SELECT ro.room_number, r.status, r.request_date " +
            "FROM reservations r JOIN rooms ro ON r.room_id = ro.id " +
            "WHERE r.tenant_id = ? AND r.status != 'REJECTED' " +
            "ORDER BY r.request_date DESC LIMIT 1";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("room_number"),
                    rs.getString("status"),
                    rs.getString("request_date")
                };
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(Reservation r) {
        String sql = "INSERT INTO reservations (tenant_id, room_id, request_date, status) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, r.getTenantId());
            ps.setInt(2, r.getRoomId());
            ps.setString(3, r.getRequestDate());
            ps.setString(4, r.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateStatus(int id, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE reservations SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
