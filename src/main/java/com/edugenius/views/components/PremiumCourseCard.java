// FILE: src/main/java/com/edugenius/views/components/PremiumCourseCard.java
package com.edugenius.views.components;

import com.edugenius.config.AppTheme;
import com.edugenius.models.Course;
import com.edugenius.views.NavigationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class PremiumCourseCard extends JPanel {
    
    private Course course;
    private boolean isHovered = false;
    
    public PremiumCourseCard(Course course) {
        this.course = course;
        setOpaque(false);
        setPreferredSize(new Dimension(280, 180));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                Map<String, Object> params = new HashMap<>();
                params.put("courseId", course.getCourseId());
                params.put("courseName", course.getCourseName());
                params.put("courseCode", course.getCourseCode());
                NavigationManager.getInstance().navigateTo("AI_LEARNING", params);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Shadow effect on hover
        if (isHovered) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(4, 6, width - 8, height - 8, 16, 16);
        }
        
        // Card background with gradient option
        g2.setColor(AppTheme.WHITE);
        g2.fillRoundRect(0, 0, width, height, 16, 16);
        
        // Border on hover
        if (isHovered) {
            g2.setColor(AppTheme.TEAL);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, width - 3, height - 3, 16, 16);
        } else {
            g2.setColor(AppTheme.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16);
        }
        
        // Top accent bar
        Color accentColor;
        try {
            accentColor = Color.decode(course.getIconColor());
        } catch (Exception e) {
            accentColor = AppTheme.TEAL;
        }
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, width, 6, 16, 16);
        g2.fillRect(0, 3, width, 3);
        
        // Course icon circle
        int circleSize = 50;
        int circleX = width - circleSize - 20;
        int circleY = 20;
        
        g2.setColor(accentColor);
        g2.fillOval(circleX, circleY, circleSize, circleSize);
        
        // Course initial
        g2.setColor(AppTheme.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        String initial = course.getCourseName().substring(0, 1);
        FontMetrics fm = g2.getFontMetrics();
        int textX = circleX + (circleSize - fm.stringWidth(initial)) / 2;
        int textY = circleY + (circleSize - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initial, textX, textY);
        
        // Course code badge
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(16, 20, 60, 24, 12, 12);
        g2.setColor(AppTheme.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.drawString(course.getCourseCode(), 20, 36);
        
        // Course name
        g2.setColor(AppTheme.INK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        String courseName = course.getCourseName();
        if (courseName.length() > 22) {
            courseName = courseName.substring(0, 19) + "...";
        }
        g2.drawString(courseName, 16, 70);
        
        // Credits badge
        g2.setColor(AppTheme.MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString(course.getCredits() + " Credits", 16, 95);
        
        // Progress section
        int progressY = height - 40;
        
        // Progress label
        g2.setColor(AppTheme.MUTED);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString("Course Progress", 16, progressY - 10);
        
        // Progress bar background
        g2.setColor(AppTheme.BORDER);
        g2.fillRoundRect(16, progressY, width - 32, 6, 3, 3);
        
        // Progress bar (sample data)
        int progress = (int)(Math.random() * 100);
        g2.setColor(accentColor);
        g2.fillRoundRect(16, progressY, (int)((width - 32) * progress / 100.0), 6, 3, 3);
        
        // Progress percentage
        g2.setColor(accentColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        String progressText = progress + "%";
        int progressTextX = width - 16 - fm.stringWidth(progressText);
        g2.drawString(progressText, progressTextX, progressY - 5);
        
        // Click hint on hover
        if (isHovered) {
            g2.setColor(AppTheme.TEAL);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String hint = "Click to continue →";
            fm = g2.getFontMetrics();
            g2.drawString(hint, width - fm.stringWidth(hint) - 16, height - 12);
        }
        
        g2.dispose();
    }
}