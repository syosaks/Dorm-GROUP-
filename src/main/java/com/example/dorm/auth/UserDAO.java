package com.example.dorm.auth;

import com.example.dorm.util.DatabaseConnection;
import com.example.dorm.util.PasswordUtil;

import java.sql.*;

public class UserDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Authenticates by hashing the input password before comparing. */
    public User authenticate(String username, String password, String role) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"),
                    rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT COUNT(*) FROM users WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Stores a SHA-256 hash of the password, never the plaintext. */
    public int addUser(String username, String password, String role) {
        try (PreparedStatement ps = conn().prepareStatement(
            "INSERT INTO users (username, password, role) VALUES (?,?,?)",
            Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hash(password));
            ps.setString(3, role);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
