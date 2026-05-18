package com.example.dorm.report;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

/**
 * Handles all aggregate reporting queries across rooms, reservations, and payments.
 * Supports optional date range filtering on Revenue and Reservations reports.
 */
public class ReportDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Occupancy report — no date filter (room status is current-state only). */
    public List<String[]> getRoomOccupancyReport() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) as cnt FROM rooms GROUP BY status ORDER BY status";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("cnt");
                list.add(new String[]{status, String.valueOf(count), count + " room(s) — " + status});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Revenue by month. If dateFrom and dateTo are non-empty, filters by payment_date range.
     * Format: YYYY-MM-DD
     */
    public List<String[]> getRevenueByMonth(String dateFrom, String dateTo) {
        List<String[]> list = new ArrayList<>();
        boolean filtered = !dateFrom.isEmpty() && !dateTo.isEmpty();
        String sql = filtered
            ? "SELECT month_covered, COUNT(*) as cnt, SUM(amount) as total " +
              "FROM payments WHERE payment_date BETWEEN ? AND ? " +
              "GROUP BY month_covered ORDER BY month_covered DESC"
            : "SELECT month_covered, COUNT(*) as cnt, SUM(amount) as total " +
              "FROM payments GROUP BY month_covered ORDER BY month_covered DESC";
        try {
            PreparedStatement ps = conn().prepareStatement(sql);
            if (filtered) { ps.setString(1, dateFrom); ps.setString(2, dateTo); }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("month_covered"),
                    String.valueOf(rs.getInt("cnt")),
                    "\u20b1" + String.format("%.2f", rs.getDouble("total"))
                });
            }
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Reservation status breakdown. Optional date range filters by request_date.
     */
    public List<String[]> getReservationStatusReport(String dateFrom, String dateTo) {
        List<String[]> list = new ArrayList<>();
        boolean filtered = !dateFrom.isEmpty() && !dateTo.isEmpty();
        String sql = filtered
            ? "SELECT status, COUNT(*) as cnt FROM reservations " +
              "WHERE request_date BETWEEN ? AND ? GROUP BY status ORDER BY status"
            : "SELECT status, COUNT(*) as cnt FROM reservations GROUP BY status ORDER BY status";
        try {
            PreparedStatement ps = conn().prepareStatement(sql);
            if (filtered) { ps.setString(1, dateFrom); ps.setString(2, dateTo); }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("cnt");
                list.add(new String[]{status, String.valueOf(count), count + " reservation(s) — " + status});
            }
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public double getTotalRevenue() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COALESCE(SUM(amount), 0) as total FROM payments WHERE status = 'PAID'")) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalRevenueInRange(String dateFrom, String dateTo) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT COALESCE(SUM(amount), 0) as total FROM payments " +
            "WHERE status = 'PAID' AND payment_date BETWEEN ? AND ?")) {
            ps.setString(1, dateFrom);
            ps.setString(2, dateTo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalRooms() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getOccupiedRooms() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms WHERE status = 'OCCUPIED'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalTenants() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tenants")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getPendingReservations() {
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT COUNT(*) FROM reservations WHERE status = 'PENDING'")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
