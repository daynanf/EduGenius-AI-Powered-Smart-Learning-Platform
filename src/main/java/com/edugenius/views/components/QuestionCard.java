// FILE: src/main/java/com/edugenius/views/components/QuestionCard.java
package com.edugenius.views.components;

import com.edugenius.config.AppTheme;
import com.edugenius.models.QuizQuestion;

import javax.swing.*;
import java.awt.*;

public class QuestionCard extends JPanel {
    
    private QuizQuestion question;
    private boolean isTeacherMode;
    
    public QuestionCard(QuizQuestion question, boolean isTeacherMode) {
        this.question = question;
        this.isTeacherMode = isTeacherMode;
        setOpaque(false);
        setPreferredSize(new Dimension(280, 200));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Header with Q number and difficulty
        g2.setColor(AppTheme.TEAL);
        g2.fillRoundRect(0, 0, 40, 24, 12, 12);
        g2.setColor(AppTheme.WHITE);
        g2.setFont(AppTheme.FONT_SMALL);
        g2.drawString("Q" + question.getQuestionNumber(), 12, 16);
        
        // Difficulty badge
        Color diffColor = getDifficultyColor();
        g2.setColor(diffColor);
        g2.fillRoundRect(w - 70, 0, 60, 24, 12, 12);
        g2.setColor(AppTheme.WHITE);
        g2.drawString(question.getDifficulty(), w - 65, 16);
        
        // Question text
        g2.setColor(AppTheme.INK);
        g2.setFont(AppTheme.FONT_BODY_BOLD);
        String text = question.getQuestionText();
        if (text.length() > 80) text = text.substring(0, 77) + "...";
        
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        int y = 40;
        
        // Simple word wrap
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (fm.stringWidth(line + word) < w - 24) {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            } else {
                g2.drawString(line.toString(), 12, y);
                line = new StringBuilder(word);
                y += lineHeight;
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), 12, y);
        }
        
        // Options preview
        y += lineHeight + 8;
        g2.setFont(AppTheme.FONT_SMALL);
        g2.setColor(AppTheme.MUTED);
        g2.drawString("A: " + shorten(question.getOptionA(), 25), 12, y);
        y += lineHeight;
        g2.drawString("B: " + shorten(question.getOptionB(), 25), 12, y);
        
        if (isTeacherMode) {
            // Show correct answer indicator
            g2.setColor(AppTheme.GREEN);
            g2.setFont(AppTheme.FONT_SMALL);
            g2.drawString("✓ Correct: " + question.getCorrectOption(), w - 80, h - 12);
        }
    }
    
    private Color getDifficultyColor() {
        switch (question.getDifficulty()) {
            case "EASY": return AppTheme.GREEN;
            case "MEDIUM": return AppTheme.AMBER;
            case "HARD": return AppTheme.CORAL;
            default: return AppTheme.TEAL;
        }
    }
    
    private String shorten(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}