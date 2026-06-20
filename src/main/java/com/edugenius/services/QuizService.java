// FILE: src/main/java/com/edugenius/services/QuizService.java
package com.edugenius.services;

import com.edugenius.db.DatabaseManager;
import com.edugenius.models.QuizSession;
import com.edugenius.models.QuizAnswer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for quiz operations
 * Handles quiz sessions, answers, and scoring
 */
public class QuizService {
    
    private DatabaseManager dbManager;
    
    public QuizService() {
        dbManager = DatabaseManager.getInstance();
    }
    
    public int createQuizSession(int userId, int courseId, String topic, String difficulty, 
                                  int totalQuestions, String questionType) throws SQLException {
        String sql = "INSERT INTO quiz_sessions (user_id, course_id, topic, difficulty, " +
                     "total_questions, question_type, session_status) VALUES (?, ?, ?, ?, ?, ?, 'IN_PROGRESS')";
        
        return dbManager.executeInsert(sql, userId, courseId, topic, difficulty, totalQuestions, questionType);
    }
    
    public void saveQuizAnswer(int sessionId, int questionNo, String questionText,
                                String optionA, String optionB, String optionC, String optionD,
                                String correctOption, String studentAnswer, boolean isCorrect,
                                String difficulty) throws SQLException {
        String sql = "INSERT INTO quiz_answers (session_id, question_no, question_text, " +
                     "option_a, option_b, option_c, option_d, correct_option, student_answer, " +
                     "is_correct, difficulty) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        dbManager.executeUpdate(sql, sessionId, questionNo, questionText,
            optionA, optionB, optionC, optionD, correctOption, studentAnswer, isCorrect, difficulty);
    }
    
    public void completeQuizSession(int sessionId, int correctAnswers, double scorePercent, 
                                     String grade, int timeTakenSec) throws SQLException {
        String sql = "UPDATE quiz_sessions SET correct_answers = ?, score_percent = ?, " +
                     "grade = ?, time_taken_sec = ?, session_status = 'COMPLETED', " +
                     "completed_at = CURRENT_TIMESTAMP WHERE session_id = ?";
        
        dbManager.executeUpdate(sql, correctAnswers, scorePercent, grade, timeTakenSec, sessionId);
    }
    
    public List<QuizAnswer> getQuizAnswers(int sessionId) throws SQLException {
        List<QuizAnswer> answers = new ArrayList<>();
        String sql = "SELECT * FROM quiz_answers WHERE session_id = ? ORDER BY question_no";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                QuizAnswer answer = new QuizAnswer(
                    rs.getInt("answer_id"),
                    rs.getInt("session_id"),
                    rs.getInt("question_no"),
                    rs.getString("question_text"),
                    rs.getString("option_a"),
                    rs.getString("option_b"),
                    rs.getString("option_c"),
                    rs.getString("option_d"),
                    rs.getString("correct_option"),
                    rs.getString("student_answer"),
                    rs.getBoolean("is_correct"),
                    rs.getString("ai_explanation"),
                    rs.getString("difficulty")
                );
                answers.add(answer);
            }
        }
        return answers;
    }
    
    public QuizSession getQuizSession(int sessionId) throws SQLException {
        String sql = "SELECT * FROM quiz_sessions WHERE session_id = ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new QuizSession(
                    rs.getInt("session_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getString("topic"),
                    rs.getString("difficulty"),
                    rs.getString("question_type"),
                    rs.getInt("total_questions"),
                    rs.getInt("correct_answers"),
                    rs.getDouble("score_percent"),
                    rs.getString("grade"),
                    rs.getInt("time_taken_sec"),
                    rs.getString("session_status"),
                    rs.getTimestamp("started_at"),
                    rs.getTimestamp("completed_at")
                );
            }
        }
        return null;
    }
    
    public void updateAIExplanation(int answerId, String explanation) throws SQLException {
        String sql = "UPDATE quiz_answers SET ai_explanation = ? WHERE answer_id = ?";
        dbManager.executeUpdate(sql, explanation, answerId);
    }
    
    public List<QuizSession> getUserQuizHistory(int userId, int limit) throws SQLException {
        List<QuizSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM quiz_sessions WHERE user_id = ? AND session_status = 'COMPLETED' " +
                     "ORDER BY completed_at DESC LIMIT ?";
        
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                QuizSession session = new QuizSession(
                    rs.getInt("session_id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getString("topic"),
                    rs.getString("difficulty"),
                    rs.getString("question_type"),
                    rs.getInt("total_questions"),
                    rs.getInt("correct_answers"),
                    rs.getDouble("score_percent"),
                    rs.getString("grade"),
                    rs.getInt("time_taken_sec"),
                    rs.getString("session_status"),
                    rs.getTimestamp("started_at"),
                    rs.getTimestamp("completed_at")
                );
                sessions.add(session);
            }
        }
        return sessions;
    }
}