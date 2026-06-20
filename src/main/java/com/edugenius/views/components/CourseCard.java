// FILE: src/main/java/com/edugenius/views/components/CourseCard.java
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

/**
 * Modern course card widget for Student Dashboard
 * Overhauled to look premium using standard Swing controls and custom rounded painting
 */
public class CourseCard extends JPanel {
    
    private Course course;
    private int simulatedProgress; // Stable simulated score cached on construction
    private boolean isHovered = false;
    private Color accentColor;
    
    public CourseCard(Course course) {
        this.course = course;
        this.simulatedProgress = (int)(Math.random() * 30) + 60; // Stable simulated average score (60% to 90%)
        
        setLayout(new BorderLayout());
        setOpaque(false); // Enable transparency so rounded corners draw properly
        setPreferredSize(new Dimension(240, 170));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Resolve course accent color
        try {
            accentColor = Color.decode(course.getIconColor());
        } catch (Exception e) {
            accentColor = AppTheme.TEAL;
        }
        
        // 1. Central Details Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        
        // Empty margin padding so content sits nicely away from the rounded edges
        setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Row 0: Course Code (Left) and Course Initial Box (Right)
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        JPanel row0 = new JPanel(new BorderLayout());
        row0.setOpaque(false);
        
        JLabel codeLabel = new JLabel(course.getCourseCode());
        codeLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        codeLabel.setForeground(AppTheme.MUTED);
        row0.add(codeLabel, BorderLayout.WEST);
        
        // Clean squared initial badge
        JPanel initialBox = new JPanel(new GridBagLayout());
        initialBox.setBackground(accentColor);
        initialBox.setPreferredSize(new Dimension(28, 28));
        JLabel initialLabel = new JLabel(course.getCourseName().substring(0, 1).toUpperCase());
        initialLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        initialLabel.setForeground(Color.WHITE);
        initialBox.add(initialLabel);
        row0.add(initialBox, BorderLayout.EAST);
        
        contentPanel.add(row0, gbc);
        
        // Row 1: Course Name Text
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        JLabel nameLabel = new JLabel(course.getCourseName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(AppTheme.INK);
        // Truncate course name if it is excessively long to maintain card layout
        if (course.getCourseName().length() > 24) {
            nameLabel.setText(course.getCourseName().substring(0, 21) + "...");
        }
        contentPanel.add(nameLabel, gbc);
        
        // Row 2: Native JProgressBar
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(simulatedProgress);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setForeground(AppTheme.TEAL);
        progressBar.setBackground(new Color(226, 232, 240)); // light track background
        progressBar.setBorderPainted(false);
        contentPanel.add(progressBar, gbc);
        
        // Row 3: Bottom info (Score Average & Credit count)
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel row3 = new JPanel(new BorderLayout());
        row3.setOpaque(false);
        
        JLabel scoreLabel = new JLabel("Average: " + simulatedProgress + "%");
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        scoreLabel.setForeground(AppTheme.MUTED);
        row3.add(scoreLabel, BorderLayout.WEST);
        
        JLabel creditsLabel = new JLabel(course.getCredits() + " Credits");
        creditsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        creditsLabel.setForeground(AppTheme.MUTED);
        row3.add(creditsLabel, BorderLayout.EAST);
        
        contentPanel.add(row3, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // 3. Mouse Interaction (Hover visual effects and click navigation)
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
        
        int w = getWidth();
        int h = getHeight();
        
        // 1. Draw rounded white card background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, w, h, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
        
        // 2. Draw top accent bar (rounded at the top corners to match the card!)
        g2.setColor(accentColor);
        g2.fillRoundRect(0, 0, w, 8, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
        g2.fillRect(0, 4, w, 4); // blend bottom corners of the accent strip
        
        // 3. Draw rounded outline border
        g2.setColor(isHovered ? accentColor : AppTheme.BORDER);
        g2.setStroke(new BasicStroke(isHovered ? 2f : 1f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
        
        g2.dispose();
    }
}