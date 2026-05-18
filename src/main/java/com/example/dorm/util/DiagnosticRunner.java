package com.example.dorm.util;

import java.sql.*;

/**
 * Run this to diagnose MySQL connection issues.
 * Usage: mvn compile exec:java -Dexec.mainClass="com.example.dorm.util.DiagnosticRunner"
 */
public class DiagnosticRunner {

    public static void main(String[] args) {
        System.out.println("=== DormLink MySQL Diagnostic ===\n");

        String url  = "jdbc:mysql://localhost:3306/dormlink" +
                      "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "";

        // Step 1 — Driver present?
        System.out.print("[1] MySQL driver on classpath... ");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("OK");
        } catch (ClassNotFoundException e) {
            System.out.println("FAIL — driver JAR missing from classpath");
            return;
        }

        // Step 2 — Can we reach MySQL at all (port open)?
        System.out.print("[2] Connecting to localhost:3306... ");
        try (var sock = new java.net.Socket()) {
            sock.connect(new java.net.InetSocketAddress("localhost", 3306), 3000);
            System.out.println("OK — port is open");
        } catch (Exception e) {
            System.out.println("FAIL — " + e.getMessage());
            System.out.println("     >> XAMPP MySQL is not running. Start it in XAMPP Control Panel.");
            return;
        }

        // Step 3 — Can we log in as root?
        System.out.print("[3] Login as root (no password)... ");
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                user, pass)) {
            System.out.println("OK");
        } catch (SQLException e) {
            System.out.println("FAIL — " + e.getMessage());
            if (e.getMessage().contains("Access denied")) {
                System.out.println("     >> Your MySQL root account has a password.");
                System.out.println("     >> Open DatabaseConnection.java and set DB_PASS to your password.");
            }
            return;
        }

        // Step 4 — Does the 'dormlink' database exist?
        System.out.print("[4] Database 'dormlink' exists... ");
        try (Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                user, pass);
             ResultSet rs = c.createStatement().executeQuery(
                "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = 'dormlink'")) {
            if (rs.next()) {
                System.out.println("OK");
            } else {
                System.out.println("FAIL — database does not exist");
                System.out.println("     >> Open phpMyAdmin → New → name: dormlink → Create");
                return;
            }
        } catch (SQLException e) {
            System.out.println("FAIL — " + e.getMessage());
            return;
        }

        // Step 5 — Full connection to dormlink
        System.out.print("[5] Full connection to dormlink... ");
        try (Connection c = DriverManager.getConnection(url, user, pass)) {
            System.out.println("OK");

            // Step 6 — Count tables
            System.out.print("[6] Tables in dormlink... ");
            ResultSet rs = c.createStatement().executeQuery(
                "SELECT COUNT(*) FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = 'dormlink'");
            rs.next();
            int count = rs.getInt(1);
            System.out.println(count + " table(s) found");

            if (count == 0) {
                System.out.println("     >> Tables are created when the app RUNS (not by this tool).");
                System.out.println("     >> Launch DormLink normally — tables appear after first start.");
            } else {
                System.out.println("\n>> All good! DormLink should connect and work.");
            }
        } catch (SQLException e) {
            System.out.println("FAIL — " + e.getMessage());
        }

        System.out.println("\n=== Done ===");
    }
}
