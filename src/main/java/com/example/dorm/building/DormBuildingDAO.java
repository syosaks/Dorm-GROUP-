package com.example.dorm.building;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class DormBuildingDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<DormBuilding> getAllBuildings() {
        List<DormBuilding> list = new ArrayList<>();
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM dorm_buildings ORDER BY building_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * Returns display data including room count per building.
     * Array: [0]=buildingName, [1]=totalFloors, [2]=address, [3]=roomCount, [4]=id(hidden)
     */
    public List<String[]> getAllForDisplay() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT b.id, b.building_name, b.total_floors, b.address, " +
            "COUNT(r.id) AS room_count " +
            "FROM dorm_buildings b " +
            "LEFT JOIN rooms r ON r.building_id = b.id " +
            "GROUP BY b.id ORDER BY b.building_name";
        try (Statement stmt = conn().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("building_name"),
                    rs.getInt("total_floors") + " floors",
                    rs.getString("address"),
                    rs.getInt("room_count") + " room(s)",
                    String.valueOf(rs.getInt("id"))
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public DormBuilding getById(int id) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT * FROM dorm_buildings WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /** Adds a new building and returns its generated id, or -1 on failure. */
    public int add(String buildingName, int totalFloors, String address) {
        try (PreparedStatement ps = conn().prepareStatement(
            "INSERT INTO dorm_buildings (building_name, total_floors, address) VALUES (?,?,?)",
            Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, buildingName);
            ps.setInt(2, totalFloors);
            ps.setString(3, address);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    private DormBuilding map(ResultSet rs) throws SQLException {
        return new DormBuilding(rs.getInt("id"), rs.getString("building_name"),
            rs.getInt("total_floors"), rs.getString("address"));
    }
}
