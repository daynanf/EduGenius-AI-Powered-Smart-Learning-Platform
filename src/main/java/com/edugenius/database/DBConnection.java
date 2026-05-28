package com.edugenius.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // UPDATED: Standard local MySQL connection URL pointing to our new database
    private static final String URL = "jdbc:mysql://localhost:3306/edugenius_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // Default MySQL user
    private static final String PASSWORD = "12345678"; // Replace with your actual MySQL password

    static {
        try {
            // Force explicit MySQL driver class initialization
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Database Engine Error: MySQL Connector/J Driver missing from classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a standard live connection pipeline to the MySQL database instance.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
