// FILE: src/main/java/com/edugenius/model/AIQuestion.java
package com.edugenius.model;

import java.util.List;

public class AIQuestion {
    private String questionText;
    private List<String> options; // Must contain exactly 4 options
    private String correctOption; // "A", "B", "C", or "D"

    // Getters and Setters
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String q) { this.questionText = q; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> o) { this.options = o; }
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String c) { this.correctOption = c; }
}
