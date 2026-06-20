// FILE: src/main/java/com/edugenius/models/User.java
package com.edugenius.models;

/**
 * Abstract base User class
 * Demonstrates ABSTRACTION and INHERITANCE
 */
public abstract class User {
    protected int userId;
    protected String aauId;
    protected String fullName;
    protected String email;
    protected String role;
    
    public User(int userId, String aauId, String fullName, String email, String role) {
        this.userId = userId;
        this.aauId = aauId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }
    
    // Getters
    public int getUserId() { return userId; }
    public String getAauId() { return aauId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    
    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    // Abstract methods for POLYMORPHISM
    public abstract String getDashboardType();
    public abstract String getRoleLabel();
    
    @Override
    public String toString() {
        return fullName + " (" + aauId + ")";
    }
}