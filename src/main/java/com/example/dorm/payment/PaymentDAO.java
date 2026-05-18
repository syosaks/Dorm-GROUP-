package com.example.dorm.payment;

import com.example.dorm.util.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class PaymentDAO {
    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Payment> getByTenantId(int tenantId) {
        List<Payment> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement(
            "SELECT * FROM payments WHERE tenant_id = ? ORDER BY payment_date DESC")) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void add(Payment p) {
        String sql = "INSERT INTO payments (tenant_id, amount, payment_date, month_covered, status) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, p.getTenantId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getPaymentDate());
            ps.setString(4, p.getMonthCovered());
            ps.setString(5, p.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Payment map(ResultSet rs) throws SQLException {
        return new Payment(rs.getInt("id"), rs.getInt("tenant_id"),
            rs.getDouble("amount"), rs.getString("payment_date"),
            rs.getString("month_covered"), rs.getString("status"));
    }
}
