// FILE: src/main/java/com/edugenius/config/AppTheme.java
package com.edugenius.config;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Central theme configuration for EduGenius
 * Contains all colors, fonts, and dimensions as per UI spec
 * Singleton pattern - all values are static final
 */
public final class AppTheme {
    
    // =============================================
    // AAU BRAND COLORS - Modern Dark Theme
    // =============================================
    public static final Color NAVY = new Color(13, 27, 42);       // #0D1B2A - Main background
    public static final Color NAVY_MED = new Color(26, 46, 69);     // #1A2E45 - Cards, panels
    public static final Color NAVY_LIGHT = new Color(35, 55, 75);      // Hover states
    
    public static final Color TEAL = new Color(0, 201, 167);    // #00C9A7 - Primary accent
    public static final Color TEAL_DARK = new Color(0, 120, 100);       // #007A66 - Hover on teal
    public static final Color TEAL_LIGHT = new Color(224, 251, 245);    // #E0FBF5 - Teal backgrounds
    
    // =============================================
    // SEMANTIC COLORS
    // =============================================
    public static final Color AMBER = new Color(255, 179, 71);          // #FFB347 - Warnings
    public static final Color AMBER_LIGHT = new Color(255, 244, 224);
    public static final Color PURPLE = new Color(124, 92, 191);         // #7C5CBF - AI features
    public static final Color PURPLE_LIGHT = new Color(240, 236, 255);
    public static final Color CORAL = new Color(255, 107, 107);         // #FF6B6B - Errors
    public static final Color CORAL_LIGHT = new Color(255, 240, 240);
    public static final Color GREEN = new Color(34, 197, 94);           // #22C55E - Success
    public static final Color GREEN_LIGHT = new Color(240, 253, 244);
    
    // =============================================
    // NEUTRAL COLORS
    // =============================================
    public static final Color INK = new Color(26, 26, 46);              // #1A1A2E - Body text
    public static final Color MUTED = new Color(100, 116, 139);         // #64748B - Secondary text
    public static final Color BORDER = new Color(226, 232, 240);        // #E2E8F0 - Borders
    public static final Color SURFACE = new Color(248, 250, 252);       // #F8FAFC - Light backgrounds
    public static final Color WHITE = Color.WHITE;
    
    // =============================================
    // TEXT COLORS (For dark backgrounds)
    // =============================================
    public static final Color TEXT_LIGHT = new Color(255, 255, 255);
    public static final Color TEXT_MUTED = new Color(180, 190, 200);
    
    // =============================================
    // TYPOGRAPHY - Font Constants
    // =============================================
    public static final Font FONT_HERO = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_H3 = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MUTED = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_CODE = new Font("JetBrains Mono", Font.PLAIN, 11);
    
    // =============================================
    // DIMENSION CONSTANTS
    // =============================================
    public static final int CARD_RADIUS = 16;
    public static final int BUTTON_RADIUS = 10;
    public static final int PANEL_RADIUS = 16;
    public static final int PILL_RADIUS = 20;
    
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 12;
    public static final int SPACING_LG = 16;
    public static final int SPACING_XL = 20;
    public static final int SPACING_XXL = 24;
    
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 44;
    public static final int NAVBAR_HEIGHT = 56;
    

    // =============================================
    // SHADOW CONFIGURATION
    // =============================================
    public static final float[] SHADOW_ALPHAS = {0.06f, 0.04f, 0.02f};
    public static final int[] SHADOW_OFFSETS_X = {0, 0, 0};
    public static final int[] SHADOW_OFFSETS_Y = {2, 4, 6};

    /**
     * Setup FlatLaf modern look and feel
     * Call this ONCE at application startup
     */
    public static void setupFlatLaf() {
        try {
            FlatLightLaf.setup();
            
            // Custom UI colors to match our palette
            UIManager.put("Button.arc", BUTTON_RADIUS);
            UIManager.put("Component.arc", BUTTON_RADIUS);
            UIManager.put("TextComponent.arc", BUTTON_RADIUS);
            
            UIManager.put("Button.background", TEAL);
            UIManager.put("Button.foreground", WHITE);
            UIManager.put("Button.font", FONT_BODY_BOLD);
            
            UIManager.put("Label.font", FONT_BODY);
            UIManager.put("TextField.font", FONT_BODY);
            UIManager.put("TextArea.font", FONT_BODY);
            UIManager.put("ComboBox.font", FONT_BODY);
            
            UIManager.put("Panel.background", SURFACE);
            
        } catch (Exception e) {
            System.err.println("[WARNING] FlatLaf setup failed: " + e.getMessage());
        }
    }
    
    /**
     * Apply global defaults to Swing components
     */
    public static void applyGlobalDefaults() {
        UIManager.put("Button.font", FONT_BODY_BOLD);
        UIManager.put("Label.font", FONT_BODY);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("TextArea.font", FONT_BODY);
        UIManager.put("PasswordField.font", FONT_BODY);
        UIManager.put("ComboBox.font", FONT_BODY);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.font", FONT_H3);
        UIManager.put("List.font", FONT_BODY);
        
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("TextField.background", WHITE);
        UIManager.put("TextField.foreground", INK);
        UIManager.put("TextArea.background", WHITE);
        UIManager.put("TextArea.foreground", INK);
        UIManager.put("ComboBox.background", WHITE);
        UIManager.put("ComboBox.foreground", INK);
        
        UIManager.put("TextField.selectionBackground", TEAL_LIGHT);
        UIManager.put("TextArea.selectionBackground", TEAL_LIGHT);
        UIManager.put("List.selectionBackground", TEAL_LIGHT);
    }
    
    public static Color getDifficultyColor(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY": return GREEN;
            case "MEDIUM": return AMBER;
            case "HARD": return CORAL;
            default: return TEAL;
        }
    }
    
    public static Color getGradeColor(double percentage) {
        if (percentage >= 90) return GREEN;
        if (percentage >= 80) return TEAL;
        if (percentage >= 70) return AMBER;
        if (percentage >= 60) return PURPLE;
        return CORAL;
    }
    
    public static String getLetterGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        return "F";
    }
}