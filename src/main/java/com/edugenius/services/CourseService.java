// FILE: src/main/java/com/edugenius/services/CourseService.java
package com.edugenius.services;

import com.edugenius.db.DatabaseManager;
import com.edugenius.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for course-related operations
 */
public class CourseService {
    
    private DatabaseManager dbManager;
    
    public CourseService() {
        dbManager = DatabaseManager.getInstance();
    }
    
    public List<Course> getCoursesByYearAndSemester(int year, int semester) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE year_level = ? AND semester = ? AND is_active = TRUE ORDER BY course_code";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, semester);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("year_level"),
                    rs.getInt("semester"),
                    rs.getInt("credits"),
                    rs.getString("icon_color"),
                    rs.getBoolean("is_active")
                );
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to load courses: " + e.getMessage());
        }
        
        return courses;
    }
    
    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Course(
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getString("description"),
                    rs.getInt("year_level"),
                    rs.getInt("semester"),
                    rs.getInt("credits"),
                    rs.getString("icon_color"),
                    rs.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to get course: " + e.getMessage());
        }
        return null;
    }
}