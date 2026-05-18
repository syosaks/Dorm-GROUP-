package com.example.dorm.util;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // ── XAMPP MySQL connection settings ───────────────────────────────────────
    private static final String DB_URL  =
        "jdbc:mysql://localhost:3306/dormlink" +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";   // XAMPP default: no password

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread-safe Singleton using synchronized keyword.
     *
     * Multithreading criterion: 'synchronized' ensures only one thread can
     * enter this method at a time, preventing two threads from both seeing
     * instance == null and creating duplicate connections (race condition).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // ── Transaction helpers ───────────────────────────────────────────────────

    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    public void commit() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    public void rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {}
    }

    private void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // ── Core tables (MySQL syntax) ────────────────────────────────────
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id       INT AUTO_INCREMENT PRIMARY KEY," +
                "  username VARCHAR(50)  NOT NULL UNIQUE," +
                "  password VARCHAR(64)  NOT NULL," +      // SHA-256 = 64 hex chars
                "  role     VARCHAR(20)  NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS dorm_buildings (" +
                "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                "  building_name VARCHAR(100) NOT NULL UNIQUE," +
                "  total_floors  INT          NOT NULL DEFAULT 1," +
                "  address       VARCHAR(255) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS rooms (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  room_number  VARCHAR(10)    NOT NULL UNIQUE," +
                "  capacity     INT            NOT NULL," +
                "  status       VARCHAR(20)    NOT NULL DEFAULT 'AVAILABLE'," +
                "  monthly_rate DECIMAL(10,2)  NOT NULL," +
                "  floor        INT            NOT NULL DEFAULT 1," +
                "  building_id  INT            NOT NULL DEFAULT 1" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS tenants (" +
                "  id             INT AUTO_INCREMENT PRIMARY KEY," +
                "  name           VARCHAR(100) NOT NULL," +
                "  contact_number VARCHAR(20)," +
                "  email          VARCHAR(100)," +
                "  room_id        INT," +
                "  user_id        INT" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reservations (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  tenant_id    INT         NOT NULL," +
                "  room_id      INT         NOT NULL," +
                "  request_date VARCHAR(10) NOT NULL," +
                "  status       VARCHAR(20) NOT NULL DEFAULT 'PENDING'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS payments (" +
                "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                "  tenant_id     INT           NOT NULL," +
                "  amount        DECIMAL(10,2) NOT NULL," +
                "  payment_date  VARCHAR(10)   NOT NULL," +
                "  month_covered VARCHAR(20)   NOT NULL," +
                "  status        VARCHAR(20)   NOT NULL DEFAULT 'PAID'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS maintenance_requests (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  tenant_id    INT         NOT NULL," +
                "  description  TEXT        NOT NULL," +
                "  request_date VARCHAR(10) NOT NULL," +
                "  status       VARCHAR(20) NOT NULL DEFAULT 'PENDING'," +
                "  priority     VARCHAR(10) NOT NULL DEFAULT 'MEDIUM'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS visitor_logs (" +
                "  id           INT AUTO_INCREMENT PRIMARY KEY," +
                "  visitor_name VARCHAR(100) NOT NULL," +
                "  tenant_id    INT          NOT NULL," +
                "  visit_date   VARCHAR(10)  NOT NULL," +
                "  time_in      VARCHAR(5)   NOT NULL," +
                "  time_out     VARCHAR(5)," +
                "  purpose      VARCHAR(255) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS furniture (" +
                "  id            INT AUTO_INCREMENT PRIMARY KEY," +
                "  room_id       INT         NOT NULL," +
                "  item_type     VARCHAR(100) NOT NULL," +
                "  `condition`   VARCHAR(20) NOT NULL DEFAULT 'GOOD'," +
                "  serial_number VARCHAR(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        }

        seedBuildings();
        seedUsers();
        seedRooms();
        seedTenants();
        seedReservations();
        seedPayments();
        seedMaintenance();
        seedFurniture();
        seedVisitorLogs();
    }

    private void seedBuildings() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM dorm_buildings")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO dorm_buildings (building_name, total_floors, address) VALUES " +
                "('Building A - Main', 3, '123 Dormitory St., Main Campus')," +
                "('Building B - Annex', 2, '125 Dormitory St., Annex Campus')");
        }
    }

    private void seedUsers() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        // Passwords stored as SHA-256 hashes — never plaintext
        String hAdmin = PasswordUtil.hash("admin123");
        String hLand  = PasswordUtil.hash("land123");
        String hTen   = PasswordUtil.hash("ten123");
        try (PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO users (username, password, role) VALUES (?,?,?)")) {
            Object[][] users = {
                {"admin",    hAdmin, "ADMIN"},
                {"landlord", hLand,  "LANDLORD"},
                {"tenant1",  hTen,   "TENANT"},
                {"tenant2",  hTen,   "TENANT"},
                {"tenant3",  hTen,   "TENANT"}
            };
            for (Object[] u : users) {
                ps.setString(1, (String) u[0]);
                ps.setString(2, (String) u[1]);
                ps.setString(3, (String) u[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void seedRooms() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM rooms")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO rooms (room_number, capacity, status, monthly_rate, floor, building_id) VALUES " +
                "('101', 2, 'AVAILABLE', 3500.00, 1, 1)," +
                "('102', 3, 'OCCUPIED',  4500.00, 1, 1)," +
                "('103', 1, 'OCCUPIED',  2500.00, 2, 1)," +
                "('104', 2, 'AVAILABLE', 3500.00, 2, 1)," +
                "('105', 4, 'AVAILABLE', 6000.00, 3, 1)," +
                "('201', 2, 'AVAILABLE', 3800.00, 1, 2)," +
                "('202', 3, 'AVAILABLE', 4800.00, 2, 2)");
        }
    }

    private void seedTenants() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM tenants")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO tenants (name, contact_number, email, room_id, user_id) VALUES " +
                "('Juan dela Cruz', '09171234567', 'juan@email.com', 2, 3)," +
                "('Maria Santos',   '09281234567', 'maria@email.com', 3, 4)," +
                "('Pedro Reyes',    '09391234567', 'pedro@email.com', NULL, 5)");
        }
    }

    private void seedReservations() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM reservations")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO reservations (tenant_id, room_id, request_date, status) VALUES " +
                "(3, 1, '2026-04-01', 'PENDING')," +
                "(2, 4, '2026-03-15', 'APPROVED')");
        }
    }

    private void seedPayments() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM payments")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO payments (tenant_id, amount, payment_date, month_covered, status) VALUES " +
                "(1, 4500.00, '2026-03-01', 'March 2026', 'PAID')," +
                "(1, 4500.00, '2026-04-01', 'April 2026', 'PAID')," +
                "(2, 2500.00, '2026-04-02', 'April 2026', 'PAID')");
        }
    }

    private void seedMaintenance() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM maintenance_requests")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO maintenance_requests (tenant_id, description, request_date, status, priority) VALUES " +
                "(1, 'Leaking faucet in bathroom',     '2026-04-10', 'IN_PROGRESS', 'HIGH')," +
                "(2, 'Air conditioner not cooling',    '2026-04-12', 'PENDING',     'MEDIUM')," +
                "(1, 'Broken window latch - Room 102', '2026-03-25', 'RESOLVED',    'LOW')," +
                "(2, 'Flickering ceiling light',       '2026-03-20', 'RESOLVED',    'LOW')");
        }
    }

    private void seedFurniture() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM furniture")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO furniture (room_id, item_type, `condition`, serial_number) VALUES " +
                "(2, 'Bed Frame',        'GOOD',    'BF-102-001')," +
                "(2, 'Study Desk',       'GOOD',    'SD-102-001')," +
                "(2, 'Wardrobe',         'DAMAGED', 'WD-102-001')," +
                "(3, 'Bed Frame',        'GOOD',    'BF-103-001')," +
                "(3, 'Air Conditioner',  'DAMAGED', 'AC-103-001')," +
                "(1, 'Bed Frame',        'GOOD',    'BF-101-001')," +
                "(1, 'Study Desk',       'GOOD',    'SD-101-001')");
        }
    }

    private void seedVisitorLogs() throws SQLException {
        try (Statement s = connection.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM visitor_logs")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }
        try (Statement s = connection.createStatement()) {
            s.executeUpdate(
                "INSERT INTO visitor_logs (visitor_name, tenant_id, visit_date, time_in, time_out, purpose) VALUES " +
                "('Ana Reyes',     1, '2026-05-10', '10:00', '12:30', 'Family visit')," +
                "('Ben Lim',       1, '2026-05-08', '14:00', '16:00', 'Study group')," +
                "('Celine Torres', 2, '2026-05-09', '09:00', '10:45', 'Package delivery')");
        }
    }
}
