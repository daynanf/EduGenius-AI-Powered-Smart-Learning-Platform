// FILE: src/main/java/com/edugenius/db/DatabaseManager.java
package com.edugenius.db;

import com.edugenius.config.AppConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database connection manager - Singleton pattern
 * Handles all database connections and operations
 * One connection pool for the entire application
 */
public class DatabaseManager {
    
    private static DatabaseManager instance;
    private Connection connection;
    private AppConfig config;
    
    private DatabaseManager() {
        config = AppConfig.getInstance();
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get database connection
     * Creates new connection if none exists or if existing is closed
     */
    public Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                    config.getDbUrl(),
                    config.getDbUser(),
                    config.getDbPassword()
                );
                System.out.println("[INFO] Database connection established");
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Execute SELECT query and return ResultSet
     * IMPORTANT: Caller must close ResultSet, Statement, and Connection
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeQuery();
    }
    
    /**
     * Execute UPDATE, INSERT, DELETE query
     * Returns number of affected rows
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        setParameters(stmt, params);
        return stmt.executeUpdate();
    }
    
    /**
     * Execute INSERT and return generated ID
     */
    public int executeInsert(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        setParameters(stmt, params);
        stmt.executeUpdate();
        
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1);
        }
        return -1;
    }
    
    /**
     * Execute stored procedure
     */
    public ResultSet executeCall(String procedureName, Object... params) throws SQLException {
        StringBuilder sql = new StringBuilder("{CALL " + procedureName + "(");
        for (int i = 0; i < params.length; i++) {
            sql.append(i == 0 ? "?" : ",?");
        }
        sql.append(")}");
        
        CallableStatement stmt = getConnection().prepareCall(sql.toString());
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        
        return stmt.executeQuery();
    }
    
    /**
     * Test database connection
     * Returns true if connection is successful
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean valid = conn != null && !conn.isClosed();
            if (valid) {
                System.out.println("[INFO] Database connection test: SUCCESS");
            }
            return valid;
        } catch (SQLException e) {
            System.err.println("[ERROR] Database connection test: FAILED - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close all resources safely
     */
    public void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("[ERROR] Failed to close resource: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Close the main connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[INFO] Database connection closed");
            } catch (SQLException e) {
                System.err.println("[ERROR] Failed to close connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Helper method to set PreparedStatement parameters
     */
    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
    
    /**
     * Check if a table exists
     */
    public boolean tableExists(String tableName) {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, config.getDbName());
            stmt.setString(2, tableName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to check table existence: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Get total count of records in a table
     */
    public int getCount(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get count from " + tableName + ": " + e.getMessage());
        }
        return 0;
    }
    
    /**
     * Begin transaction
     */
    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }
    
    /**
     * Commit transaction
     */
    public void commitTransaction() throws SQLException {
        getConnection().commit();
        getConnection().setAutoCommit(true);
    }
    
    /**
     * Rollback transaction
     */
    public void rollbackTransaction() throws SQLException {
        getConnection().rollback();
        getConnection().setAutoCommit(true);
    }
}