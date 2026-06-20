// FILE: src/main/java/com/edugenius/views/components/FeatureCard.java
package com.edugenius.views.components;

import com.edugenius.config.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Feature card for AI Learning Dashboard
 * Used for Study Plan, AI Quiz, and AI Tutor options
 */
public class FeatureCard extends JPanel {
    
    private String icon;
    private String title;
    private String description;
    private Color accentColor;
    private Runnable onClick;
    private boolean isHovered = false;
    
    public FeatureCard(String icon, String title, String description, Color accentColor, Runnable onClick) {
        this.icon = icon;
        this.title = title;
        this.description = description;
        this.accentColor = accentColor;
        this.onClick = onClick;
        
        setOpaque(false);
        setPreferredSize(new Dimension(200, 220));
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
                if (onClick != null) {
                    onClick.run();
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw shadow
        if (isHovered) {
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(4, 6, width - 8, height - 8, 16, 16);
        }
        
        // Draw card background
        g2.setColor(AppTheme.WHITE);
        g2.fillRoundRect(0, 0, width, height, 16, 16);
        
        // Draw border on hover
        if (isHovered) {
            g2.setColor(accentColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, width - 3, height - 3, 16, 16);
        } else {
            g2.setColor(AppTheme.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, 16, 16);
        }
        
        // Draw icon circle
        int circleSize = 56;
        int circleX = (width - circleSize) / 2;
        int circleY = 24;
        
        g2.setColor(accentColor);
        g2.fillOval(circleX, circleY, circleSize, circleSize);
        
        // Draw icon
        g2.setColor(AppTheme.WHITE);
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        FontMetrics fm = g2.getFontMetrics();
        int iconX = circleX + (circleSize - fm.stringWidth(icon)) / 2;
        int iconY = circleY + (circleSize - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(icon, iconX, iconY);
        
        // Draw title
        g2.setColor(AppTheme.INK);
        g2.setFont(AppTheme.FONT_H3);
        fm = g2.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, circleY + circleSize + 20);
        
        // Draw description
        g2.setColor(AppTheme.MUTED);
        g2.setFont(AppTheme.FONT_SMALL);
        
        // Wrap description text
        String[] words = description.split(" ");
        StringBuilder line = new StringBuilder();
        int lineY = circleY + circleSize + 48;
        
        for (String word : words) {
            if (fm.stringWidth(line + word) < width - 32) {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            } else {
                g2.drawString(line.toString(), 16, lineY);
                line = new StringBuilder(word);
                lineY += 16;
            }
        }
        if (line.length() > 0) {
            g2.drawString(line.toString(), 16, lineY);
        }
        
        // Draw arrow link at bottom
        g2.setColor(accentColor);
        g2.setFont(AppTheme.FONT_BODY_BOLD);
        String linkText = "Get Started →";
        fm = g2.getFontMetrics();
        g2.drawString(linkText, (width - fm.stringWidth(linkText)) / 2, height - 20);
    }
}