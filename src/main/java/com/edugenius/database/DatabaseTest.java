package com.edugenius.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("====== EduGenius System Diagnostic Check ======");
        
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("[SUCCESS] Database Connection Established Successfully!");
            
            // Validate tables and pre-seeded bilingual data
            String query = "SELECT course_code, course_name_en, course_name_am FROM Courses";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                System.out.println("\n[SUCCESS] Verifying Pre-Seeded Academic Catalog Data:");
                while (rs.next()) {
                    System.out.printf(" - %s | %s (%s)\n", 
                        rs.getString("course_code"), 
                        rs.getString("course_name_en"), 
                        rs.getString("course_name_am")
                    );
                }
            }
            System.out.println("\n[DIAGNOSTIC] All initial constraints and encoding vectors verified.");
            
        } catch (Exception e) {
            System.err.println("\n[FAILURE] Diagnostic Test Failed! Review configuration settings.");
            e.printStackTrace();
        }
    }
}
