// FILE: src/main/java/com/edugenius/views/NavigationManager.java
package com.edugenius.views;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Navigation Manager - Singleton pattern
 * Controls screen transitions using CardLayout
 * Maintains navigation history for back button functionality
 */
public class NavigationManager {
    
    private static NavigationManager instance;
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private Stack<String> historyStack;
    private Map<String, JPanel> screenRegistry;
    
    private NavigationManager() {
        historyStack = new Stack<>();
        screenRegistry = new HashMap<>();
    }
    
    public static synchronized NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }
    
    public void initialize(JFrame frame, CardLayout layout, JPanel container) {
        this.mainFrame = frame;
        this.cardLayout = layout;
        this.mainContainer = container;
    }
    
    /**
     * Register a screen with the navigation system
     */
    public void registerScreen(String name, JPanel panel) {
        screenRegistry.put(name, panel);
        mainContainer.add(panel, name);
    }
    
    /**
     * Navigate to a screen by name
     */
    public void navigateTo(String screenName) {
        navigateTo(screenName, null);
    }
    
    /**
     * Navigate to a screen with parameters
     */
    public void navigateTo(String screenName, Map<String, Object> params) {
        if (screenRegistry.containsKey(screenName)) {
            historyStack.push(screenName);
            cardLayout.show(mainContainer, screenName);
            
            // Pass parameters to the screen if it implements ParameterReceiver
            JPanel panel = screenRegistry.get(screenName);
            if (panel instanceof ParameterReceiver) {
                if (params == null) {
                    params = new HashMap<>();
                }
                ((ParameterReceiver) panel).receiveParameters(params);
            }
            
            // Update frame title based on screen
            updateFrameTitle(screenName);
        } else {
            System.err.println("[ERROR] Screen not registered: " + screenName);
        }
    }
    
    /**
     * Go back to previous screen
     */
    public void goBack() {
        if (historyStack.size() > 1) {
            historyStack.pop(); // Remove current
            String previousScreen = historyStack.peek();
            cardLayout.show(mainContainer, previousScreen);
            
            // Also notify the screen we're going back to
            JPanel panel = screenRegistry.get(previousScreen);
            if (panel instanceof ParameterReceiver) {
                ((ParameterReceiver) panel).receiveParameters(new HashMap<>());
            }
            
            updateFrameTitle(previousScreen);
        }
    }
    
    /**
     * Get current screen name
     */
    public String getCurrentScreen() {
        return historyStack.isEmpty() ? null : historyStack.peek();
    }
    
    private void updateFrameTitle(String screenName) {
        if (mainFrame != null) {
            String title = "EduGenius - AAU CS Platform";
            switch (screenName) {
                case "WELCOME":
                    title = "Welcome | EduGenius";
                    break;
                case "LOGIN":
                    title = "Sign In | EduGenius";
                    break;
                case "STUDENT_DASHBOARD":
                    title = "Student Dashboard | EduGenius";
                    break;
                case "TEACHER_DASHBOARD":
                    title = "Teacher Dashboard | EduGenius";
                    break;
                case "AI_LEARNING":
                    title = "AI Learning | EduGenius";
                    break;
                case "AI_TUTOR":
                    title = "AI Tutor | EduGenius";
                    break;
                case "STUDY_PLAN":
                    title = "Study Plan | EduGenius";
                    break;
                case "QUIZ_STUDENT":
                    title = "Quiz | EduGenius";
                    break;
            }
            mainFrame.setTitle(title);
        }
    }
    
    /**
     * Clear navigation history (used after logout)
     */
    public void clearHistory() {
        historyStack.clear();
    }
}

/**
 * Interface for panels that receive parameters
 */