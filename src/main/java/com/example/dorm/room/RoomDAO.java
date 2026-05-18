package com.example.dorm.room;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class RoomDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms ORDER BY room_number")) {
            while (rs.next()) rooms.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM rooms WHERE status = 'AVAILABLE' ORDER BY room_number")) {
            while (rs.next()) rooms.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return rooms;
    }

    public Room getRoomById(int id) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM rooms WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void updateStatus(int id, String status) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE rooms SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /** Returns rooms joined with building name for display.
     *  Array: [0]=roomNumber, [1]=buildingName, [2]=floor, [3]=capacity, [4]=status, [5]=rate, [6]=id(hidden) */
    public List<String[]> getAllWithBuilding() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT r.id, r.room_number, b.building_name, r.floor, r.capacity, r.status, r.monthly_rate " +
            "FROM rooms r " +
            "LEFT JOIN dorm_buildings b ON r.building_id = b.id " +
            "ORDER BY r.room_number";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String bname = rs.getString("building_name");
                list.add(new String[]{
                    rs.getString("room_number"),
                    bname != null ? bname : "—",
                    "Floor " + rs.getInt("floor"),
                    String.valueOf(rs.getInt("capacity")),
                    rs.getString("status"),
                    "\u20b1" + String.format("%.2f", rs.getDouble("monthly_rate")),
                    String.valueOf(rs.getInt("id"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Count active occupants in a room (tenants currently assigned). */
    public int countActiveOccupants(int roomId) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT COUNT(*) FROM tenants WHERE room_id = ?")) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Room map(ResultSet rs) throws SQLException {
        int floor = 1, buildingId = 1;
        try { floor = rs.getInt("floor"); } catch (SQLException ignored) {}
        try { buildingId = rs.getInt("building_id"); } catch (SQLException ignored) {}
        return new Room(rs.getInt("id"), rs.getString("room_number"),
            rs.getInt("capacity"), rs.getString("status"),
            rs.getDouble("monthly_rate"), floor, buildingId);
    }
}
