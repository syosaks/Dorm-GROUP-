package com.example.dorm.furniture;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class FurnitureDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Returns all furniture joined with room number.
     * Array: [0]=roomNumber, [1]=itemType, [2]=condition, [3]=serialNumber, [4]=id(hidden), [5]=roomId(hidden)
     */
    public List<String[]> getAllForDisplay() {
        return queryDisplay(
            "SELECT f.id, r.room_number, f.item_type, f.`condition`, f.serial_number, f.room_id " +
            "FROM furniture f JOIN rooms r ON f.room_id = r.id " +
            "ORDER BY r.room_number, f.item_type",
            null);
    }

    /** Returns furniture for a specific room. */
    public List<String[]> getByRoomId(int roomId) {
        return queryDisplay(
            "SELECT f.id, r.room_number, f.item_type, f.`condition`, f.serial_number, f.room_id " +
            "FROM furniture f JOIN rooms r ON f.room_id = r.id " +
            "WHERE f.room_id = ? ORDER BY f.item_type",
            roomId);
    }

    private List<String[]> queryDisplay(String sql, Integer roomId) {
        List<String[]> list = new ArrayList<>();
        try {
            PreparedStatement ps = conn().prepareStatement(sql);
            if (roomId != null) ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    "Room " + rs.getString("room_number"),
                    rs.getString("item_type"),
                    rs.getString("condition"),
                    rs.getString("serial_number"),
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("room_id"))
                });
            }
            ps.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Furniture getById(int id) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT * FROM furniture WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public int add(int roomId, String itemType, String condition, String serialNumber) {
        try (PreparedStatement ps = conn().prepareStatement(
            "INSERT INTO furniture (room_id, item_type, `condition`, serial_number) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, roomId);
            ps.setString(2, itemType);
            ps.setString(3, condition);
            ps.setString(4, serialNumber);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void updateCondition(int id, String condition) {
        try (PreparedStatement ps = conn().prepareStatement(
            "UPDATE furniture SET `condition` = ? WHERE id = ?")) {
            ps.setString(1, condition);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Furniture map(ResultSet rs) throws SQLException {
        return new Furniture(rs.getInt("id"), rs.getInt("room_id"),
            rs.getString("item_type"), rs.getString("condition"),  // backtick only in DDL, not here
            rs.getString("serial_number"));
    }
}
