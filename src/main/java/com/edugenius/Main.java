// FILE: src/main/java/com/edugenius/Main.java
package com.edugenius;

import com.edugenius.config.AppConfig;
import com.edugenius.config.AppTheme;
import com.edugenius.db.DatabaseManager;
import com.edugenius.views.MainWindow;

import javax.swing.*;

public class Main {
    
    public static void main(String[] args) {
        // Setup modern FlatLaf theme FIRST
        AppTheme.setupFlatLaf();
        
        // Apply EduGenius theme colors
        AppTheme.applyGlobalDefaults();
        
        // Load configuration
        AppConfig config = AppConfig.getInstance();
        System.out.println("[INFO] " + config.getAppName() + " v" + config.getAppVersion() + " starting...");
        
        // Test database connection
        DatabaseManager dbManager = DatabaseManager.getInstance();
        boolean dbConnected = dbManager.testConnection();
        
        if (!dbConnected) {
            System.err.println("[WARNING] Database connection failed. Some features may not work.");
            showDatabaseWarning();
        }
        
        config.validateConfig();
        
        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow mainWindow = new MainWindow();
                mainWindow.setVisible(true);
                System.out.println("[INFO] Application started successfully!");
            } catch (Exception e) {
                System.err.println("[FATAL] Failed to start application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to start EduGenius:\n" + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
    
    private static void showDatabaseWarning() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                " Database Connection Failed!\n\n" +
                "Please check:\n" +
                "1. MySQL server is running\n" +
                "2. Database exists\n" +
                "3. Credentials in config.properties are correct\n\n" +
                "The application will start but some features may not work.",
                "Database Warning",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }
}