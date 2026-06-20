// FILE: src/main/java/com/edugenius/models/Teacher.java
package com.edugenius.models;

/**
 * Teacher model - extends User
 * Demonstrates INHERITANCE
 */
public class Teacher extends User {
    private String staffId;
    private String subjectArea;
    private String department;
    
    public Teacher(int userId, String aauId, String fullName, String email,
                   String staffId, String subjectArea, String department) {
        super(userId, aauId, fullName, email, "TEACHER");
        this.staffId = staffId;
        this.subjectArea = subjectArea;
        this.department = department;
    }
    
    public String getStaffId() { return staffId; }
    public String getSubjectArea() { return subjectArea; }
    public String getDepartment() { return department; }
    
    public void setSubjectArea(String subjectArea) { this.subjectArea = subjectArea; }
    
    @Override
    public String getDashboardType() {
        return "TEACHER_DASHBOARD";
    }
    
    @Override
    public String getRoleLabel() {
        return "Teacher";
    }
}