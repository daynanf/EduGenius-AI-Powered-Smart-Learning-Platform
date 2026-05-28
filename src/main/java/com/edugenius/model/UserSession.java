// FILE: src/main/java/com/edugenius/model/UserSession.java
package com.edugenius.model;

public class UserSession {
    private static UserSession instance;
    
    private final int userId;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;
    private final String aauStudentId;

    private UserSession(int userId, String username, String fullName, String email, String role, String aauStudentId) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.aauStudentId = aauStudentId;
    }

    public static void initialize(int userId, String username, String fullName, String email, String role, String aauStudentId) {
        instance = new UserSession(userId, username, fullName, email, role, aauStudentId);
    }

    public static UserSession getInstance() {
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getAauStudentId() { return aauStudentId; }
}
