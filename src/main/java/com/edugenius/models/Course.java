// FILE: src/main/java/com/edugenius/models/Course.java
package com.edugenius.models;

/**
 * Course model representing AAU CS courses
 */
public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int yearLevel;
    private int semester;
    private int credits;
    private String iconColor;
    private boolean isActive;
    
    public Course(int courseId, String courseCode, String courseName, String description,
                  int yearLevel, int semester, int credits, String iconColor, boolean isActive) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.yearLevel = yearLevel;
        this.semester = semester;
        this.credits = credits;
        this.iconColor = iconColor;
        this.isActive = isActive;
    }
    
    // Getters
    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getDescription() { return description; }
    public int getYearLevel() { return yearLevel; }
    public int getSemester() { return semester; }
    public int getCredits() { return credits; }
    public String getIconColor() { return iconColor; }
    public boolean isActive() { return isActive; }
    
    @Override
    public String toString() {
        return courseCode + " - " + courseName;
    }
}