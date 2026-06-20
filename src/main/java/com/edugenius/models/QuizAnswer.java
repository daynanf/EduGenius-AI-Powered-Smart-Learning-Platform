// FILE: src/main/java/com/edugenius/models/QuizAnswer.java
package com.edugenius.models;

public class QuizAnswer {
    private int answerId;
    private int sessionId;
    private int questionNo;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String studentAnswer;
    private boolean isCorrect;
    private String aiExplanation;
    private String difficulty;
    
    public QuizAnswer(int answerId, int sessionId, int questionNo, String questionText,
                      String optionA, String optionB, String optionC, String optionD,
                      String correctOption, String studentAnswer, boolean isCorrect,
                      String aiExplanation, String difficulty) {
        this.answerId = answerId;
        this.sessionId = sessionId;
        this.questionNo = questionNo;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.studentAnswer = studentAnswer;
        this.isCorrect = isCorrect;
        this.aiExplanation = aiExplanation;
        this.difficulty = difficulty;
    }
    
    // Getters
    public int getAnswerId() { return answerId; }
    public int getSessionId() { return sessionId; }
    public int getQuestionNo() { return questionNo; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectOption() { return correctOption; }
    public String getStudentAnswer() { return studentAnswer; }
    public boolean isCorrect() { return isCorrect; }
    public String getAiExplanation() { return aiExplanation; }
    public String getDifficulty() { return difficulty; }
    
    public String getCorrectAnswerText() {
        switch (correctOption) {
            case "A": return optionA;
            case "B": return optionB;
            case "C": return optionC;
            case "D": return optionD;
            default: return "";
        }
    }
}