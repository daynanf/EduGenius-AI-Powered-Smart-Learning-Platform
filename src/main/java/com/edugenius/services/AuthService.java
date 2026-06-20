// FILE: src/main/java/com/edugenius/services/AuthService.java
package com.edugenius.services;

import com.edugenius.db.DatabaseManager;
import com.edugenius.models.User;
import com.edugenius.models.Student;
import com.edugenius.models.Teacher;
import com.edugenius.utils.SecurityUtils;
import com.edugenius.utils.ValidationUtils;

import java.sql.*;
import java.util.Map;

/**
 * Authentication Service - Singleton
 * Handles login, registration, and session management
 */
public class AuthService {
    
    private static AuthService instance;
    private User currentUser;
    private DatabaseManager dbManager;
    
    private AuthService() {
        dbManager = DatabaseManager.getInstance();
    }
    
    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Register a new user
     */
    public void register(String aauId, String fullName, String password, String role, Map<String, Object> extraFields) 
            throws Exception {
        
        // Validate AAU ID format
        if (!ValidationUtils.isValidAauId(aauId)) {
            throw new Exception("Invalid AAU ID format. Use UGR/XXXX/XX, SGR/XXXX/XX, or EMP/XXXX/XX");
        }
        
        // Check if AAU ID already exists
        if (isAauIdTaken(aauId)) {
            throw new Exception("AAU ID already registered. Please login or use a different ID.");
        }
        
        // Validate password strength
        if (!SecurityUtils.isPasswordStrong(password)) {
            throw new Exception("Password must be at least 8 characters long");
        }
        
        // Hash password
        String hashedPassword = SecurityUtils.hashPassword(password);
        
        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            dbManager.beginTransaction();
            
            // Insert into users table
            String userSql = "INSERT INTO users (aau_id, full_name, password_hash, role) VALUES (?, ?, ?, ?)";
            int userId = dbManager.executeInsert(userSql, aauId, fullName, hashedPassword, role);
            
            // Insert role-specific data
            if (role.equals("STUDENT")) {
                int year = (int) extraFields.getOrDefault("year", 1);
                int semester = (int) extraFields.getOrDefault("semester", 1);
                String department = (String) extraFields.getOrDefault("department", "Computer Science");
                
                String studentSql = "INSERT INTO students (user_id, year_of_study, semester, department) VALUES (?, ?, ?, ?)";
                dbManager.executeUpdate(studentSql, userId, year, semester, department);
                
            } else if (role.equals("TEACHER")) {
                String subjectArea = (String) extraFields.getOrDefault("subject_area", "");
                String department = (String) extraFields.getOrDefault("department", "Computer Science");
                String staffId = "EMP/" + System.currentTimeMillis() % 10000 + "/25";
                
                String teacherSql = "INSERT INTO teachers (user_id, staff_id, subject_area, department) VALUES (?, ?, ?, ?)";
                dbManager.executeUpdate(teacherSql, userId, staffId, subjectArea, department);
            }
            
            dbManager.commitTransaction();
            
            // Auto-login after registration
            login(aauId, password);
            
        } catch (SQLException e) {
            if (conn != null) {
                dbManager.rollbackTransaction();
            }
            throw new Exception("Database error during registration: " + e.getMessage());
        }
    }
    
    /**
     * Login user
     */
    public User login(String aauId, String password) throws Exception {
        String sql = "SELECT user_id, aau_id, full_name, email, password_hash, role, is_active FROM users WHERE aau_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, aauId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                throw new Exception("Invalid AAU ID or password");
            }
            
            // Check if account is active
            if (!rs.getBoolean("is_active")) {
                throw new Exception("Account is deactivated. Please contact administrator.");
            }
            
            // Verify password
            String storedHash = rs.getString("password_hash");
            if (!SecurityUtils.verifyPassword(password, storedHash)) {
                throw new Exception("Invalid AAU ID or password");
            }
            
            // Create user object based on role
            String role = rs.getString("role");
            int userId = rs.getInt("user_id");
            String fullName = rs.getString("full_name");
            String email = rs.getString("email");
            
            if (role.equals("STUDENT")) {
                currentUser = loadStudentData(userId, aauId, fullName, email);
            } else if (role.equals("TEACHER")) {
                currentUser = loadTeacherData(userId, aauId, fullName, email);
            }
            
            // Update last login timestamp
            String updateSql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
            dbManager.executeUpdate(updateSql, userId);
            
            return currentUser;
        } catch (SQLException e) {
            throw new Exception("Database error during login: " + e.getMessage());
        }
    }
    
    /**
     * Load student-specific data
     */
    private Student loadStudentData(int userId, String aauId, String fullName, String email) throws SQLException {
        String sql = "SELECT year_of_study, semester, department FROM students WHERE user_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Student(
                    userId, aauId, fullName, email,
                    rs.getInt("year_of_study"),
                    rs.getInt("semester"),
                    rs.getString("department")
                );
            }
        }
        return new Student(userId, aauId, fullName, email, 1, 1, "Computer Science");
    }
    
    /**
     * Load teacher-specific data
     */
    private Teacher loadTeacherData(int userId, String aauId, String fullName, String email) throws SQLException {
        String sql = "SELECT staff_id, subject_area, department FROM teachers WHERE user_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Teacher(
                    userId, aauId, fullName, email,
                    rs.getString("staff_id"),
                    rs.getString("subject_area"),
                    rs.getString("department")
                );
            }
        }
        return new Teacher(userId, aauId, fullName, email, "", "", "Computer Science");
    }
    
    /**
     * Check if AAU ID is already taken
     */
    public boolean isAauIdTaken(String aauId) {
        String sql = "SELECT COUNT(*) FROM users WHERE aau_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, aauId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to check AAU ID: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Set current user (for testing)
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}