// FILE: src/main/java/com/edugenius/models/QuizSession.java
package com.edugenius.models;

import java.sql.Timestamp;

public class QuizSession {
    private int sessionId;
    private int userId;
    private int courseId;
    private String topic;
    private String difficulty;
    private String questionType;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercent;
    private String grade;
    private int timeTakenSec;
    private String sessionStatus;
    private Timestamp startedAt;
    private Timestamp completedAt;
    
    public QuizSession(int sessionId, int userId, int courseId, String topic, 
                       String difficulty, String questionType, int totalQuestions,
                       int correctAnswers, double scorePercent, String grade,
                       int timeTakenSec, String sessionStatus, Timestamp startedAt, 
                       Timestamp completedAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.courseId = courseId;
        this.topic = topic;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.scorePercent = scorePercent;
        this.grade = grade;
        this.timeTakenSec = timeTakenSec;
        this.sessionStatus = sessionStatus;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }
    
    // Getters
    public int getSessionId() { return sessionId; }
    public int getUserId() { return userId; }
    public int getCourseId() { return courseId; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public String getQuestionType() { return questionType; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public double getScorePercent() { return scorePercent; }
    public String getGrade() { return grade; }
    public int getTimeTakenSec() { return timeTakenSec; }
    public String getSessionStatus() { return sessionStatus; }
    public Timestamp getStartedAt() { return startedAt; }
    public Timestamp getCompletedAt() { return completedAt; }
}