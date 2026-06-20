// FILE: src/main/java/com/edugenius/models/QuizQuestion.java
package com.edugenius.models;

/**
 * Represents a single quiz question (could be AI-generated or from database)
 */
public class QuizQuestion {
    private int questionNumber;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private String explanation;
    private String difficulty;
    private String studentAnswer;
    private boolean isAnswered;
    private boolean isFlagged;
    
    public QuizQuestion(int questionNumber, String questionText, String optionA, 
                        String optionB, String optionC, String optionD, 
                        String correctOption, String explanation, String difficulty) {
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.explanation = explanation;
        this.difficulty = difficulty;
        this.isAnswered = false;
        this.isFlagged = false;
    }
    
    // Getters
    public int getQuestionNumber() { return questionNumber; }
    public String getQuestionText() { return questionText; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectOption() { return correctOption; }
    public String getExplanation() { return explanation; }
    public String getDifficulty() { return difficulty; }
    public String getStudentAnswer() { return studentAnswer; }
    public boolean isAnswered() { return isAnswered; }
    public boolean isFlagged() { return isFlagged; }
    
    // Setters
    public void setStudentAnswer(String answer) { 
        this.studentAnswer = answer; 
        this.isAnswered = true;
    }
    public void setFlagged(boolean flagged) { isFlagged = flagged; }
    
    public boolean isCorrect() {
        if (studentAnswer == null) return false;
        return studentAnswer.equalsIgnoreCase(correctOption);
    }
    
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