// FILE: src/main/java/com/edugenius/services/HistoryService.java
package com.edugenius.services;

import com.edugenius.db.DatabaseManager;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class HistoryService {
    
    private DatabaseManager dbManager;
    
    public HistoryService() {
        dbManager = DatabaseManager.getInstance();
    }
    
    public List<Map<String, Object>> getUserHistory(int userId) {
        List<Map<String, Object>> history = new ArrayList<>();
        
        // Get quiz history
        String quizSql = "SELECT qs.session_id, qs.topic, qs.score_percent, qs.grade, " +
                        "qs.completed_at, c.course_name " +
                        "FROM quiz_sessions qs " +
                        "LEFT JOIN courses c ON qs.course_id = c.course_id " +
                        "WHERE qs.user_id = ? AND qs.session_status = 'COMPLETED' " +
                        "ORDER BY qs.completed_at DESC";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(quizSql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("type", "QUIZ");
                item.put("title", "Quiz: " + rs.getString("topic"));
                item.put("courseName", rs.getString("course_name") != null ? rs.getString("course_name") : "General");
                item.put("date", rs.getTimestamp("completed_at"));
                item.put("score", "Score: " + rs.getInt("score_percent") + "%");
                item.put("grade", rs.getString("grade"));
                history.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error loading quiz history: " + e.getMessage());
        }
        
        // Get study plan history
        String planSql = "SELECT plan_id, plan_title, created_at, c.course_name " +
                        "FROM study_plans sp " +
                        "LEFT JOIN courses c ON sp.course_id = c.course_id " +
                        "WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(planSql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("type", "PLAN");
                item.put("title", rs.getString("plan_title") != null ? rs.getString("plan_title") : "Study Plan");
                item.put("courseName", rs.getString("course_name") != null ? rs.getString("course_name") : "General");
                item.put("date", rs.getTimestamp("created_at"));
                item.put("score", "");
                item.put("grade", null);
                history.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error loading plan history: " + e.getMessage());
        }
        
        // Get AI chat history
        String chatSql = "SELECT chat_id, session_title, created_at, c.course_name " +
                        "FROM ai_tutor_sessions ats " +
                        "LEFT JOIN courses c ON ats.course_id = c.course_id " +
                        "WHERE user_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(chatSql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("type", "CHAT");
                item.put("title", rs.getString("session_title") != null ? rs.getString("session_title") : "AI Tutor Session");
                item.put("courseName", rs.getString("course_name") != null ? rs.getString("course_name") : "General");
                item.put("date", rs.getTimestamp("created_at"));
                item.put("score", "");
                item.put("grade", null);
                history.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error loading chat history: " + e.getMessage());
        }
        
        // Sort by date (newest first)
        history.sort((a, b) -> ((Date) b.get("date")).compareTo((Date) a.get("date")));
        
        return history;
    }
}