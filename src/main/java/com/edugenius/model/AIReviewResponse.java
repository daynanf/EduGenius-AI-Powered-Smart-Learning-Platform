// FILE: src/main/java/com/edugenius/model/AIReviewResponse.java
package com.edugenius.model;

public class AIReviewResponse {
    private boolean isCorrect;
    private String explanation;
    private String correctionTip;

    // Getters and Setters
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean c) { this.isCorrect = c; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String e) { this.explanation = e; }
    public String getCorrectionTip() { return correctionTip; }
    public void setCorrectionTip(String t) { this.correctionTip = t; }
}
