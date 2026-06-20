// FILE: src/main/java/com/edugenius/models/Student.java
package com.edugenius.models;

/**
 * Student model - extends User
 * Demonstrates INHERITANCE
 */
public class Student extends User {
    private int yearOfStudy;
    private int semester;
    private String department;
    
    public Student(int userId, String aauId, String fullName, String email, 
                   int yearOfStudy, int semester, String department) {
        super(userId, aauId, fullName, email, "STUDENT");
        this.yearOfStudy = yearOfStudy;
        this.semester = semester;
        this.department = department;
    }
    
    public int getYearOfStudy() { return yearOfStudy; }
    public int getSemester() { return semester; }
    public String getDepartment() { return department; }
    
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public void setSemester(int semester) { this.semester = semester; }
    
    @Override
    public String getDashboardType() {
        return "STUDENT_DASHBOARD";
    }
    
    @Override
    public String getRoleLabel() {
        return "Student";
    }
    
    public String getYearSemesterString() {
        return "Year " + yearOfStudy + ", Semester " + semester;
    }
}