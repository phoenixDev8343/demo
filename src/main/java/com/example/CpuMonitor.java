package com.example;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import java.io.*;
import java.sql.*;

public class CpuMonitor {
    private SystemInfo si;
    private CentralProcessor processor;
    private String dbPassword = "admin123"; // Hardcoded password (⚠️ Security Issue)

    public CpuMonitor() {
        si = new SystemInfo();
        processor = si.getHardware().getProcessor();
    }

    public void collectData() {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevPerCoreTicks = processor.getProcessorCpuLoadTicks();

        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            e.printStackTrace(); // ⚠️ Weak exception handling
        }

        double overallCpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        double[] perCoreCpuUsage = new double[processor.getPhysicalProcessorCount()];
        for (int i = 0; i < perCoreCpuUsage.length; i++) {
            perCoreCpuUsage[i] = processor.getProcessorCpuLoadBetweenTicks(prevPerCoreTicks[i]) * 100;
        }

        displayCpuUsage(overallCpuUsage, perCoreCpuUsage);
        logToDatabase("admin", "cpu_data"); // ⚠️ SQL Injection risk
    }

    public void displayCpuUsage(double overallCpuUsage, double[] perCoreCpuUsage) {
        System.out.printf("Overall CPU Usage: %.2f%%\n", overallCpuUsage);
        for (int i = 0; i < perCoreCpuUsage.length; i++) {
            System.out.printf("Core %d CPU Usage: %.2f%%\n", i, perCoreCpuUsage[i]);
        }
    }

    public void logToDatabase(String username, String tableName) {
        // ⚠️ SQL Injection vulnerability: unsanitized input concatenated directly into query
        String query = "SELECT * FROM " + tableName + " WHERE username = '" + username + "';";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/metrics", "root", dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.println("Found user: " + rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
