// FILE: src/main/java/com/edugenius/views/MainWindow.java
package com.edugenius.views;

import com.edugenius.config.AppConfig;
import com.edugenius.config.AppTheme;
import com.edugenius.views.auth.WelcomePanel;
import com.edugenius.views.auth.LoginPanel;
import com.edugenius.views.student.StudentDashboardPanel;
import com.edugenius.views.student.StudyPlanPanel;
import com.edugenius.views.teacher.TeacherDashboardPanel;
import com.edugenius.views.student.AILearningDashboardPanel;
import com.edugenius.views.student.AITutorPanel;
import com.edugenius.views.student.QuizPanel;



import javax.swing.*;
import java.awt.*;

/**
 * Main application window with CardLayout
 * Single JFrame - never open multiple JFrames
 * All screens are swapped using CardLayout
 */
public class MainWindow extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private AppConfig config;
    private NavigationManager navManager;
    
    public MainWindow() {
        config = AppConfig.getInstance();
        navManager = NavigationManager.getInstance();
        initWindow();
        initUI();
        registerScreens();
        showWelcomeScreen();
    }
    
    private void initWindow() {
        setTitle(config.getAppName() + " - AAU CS Smart Learning Platform");
        setSize(config.getWindowWidth(), config.getWindowHeight());
        setMinimumSize(new Dimension(
            config.getWindowMinWidth(),
            config.getWindowMinHeight()
        ));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setBackground(AppTheme.NAVY);
        
        // Set window icon (optional - will add later)
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/app-icon.png")
            ));
        } catch (Exception e) {
            // No icon yet, that's fine
        }
    }
    
    private void initUI() {
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(AppTheme.NAVY);
        
        // Initialize NavigationManager with this window
        navManager.initialize(this, cardLayout, mainContainer);
        
        add(mainContainer);
    }
    
    private void registerScreens() {
        // Register all screens with NavigationManager
        navManager.registerScreen("WELCOME", new WelcomePanel());
        navManager.registerScreen("LOGIN", new LoginPanel());
        navManager.registerScreen("STUDENT_DASHBOARD", new StudentDashboardPanel());
        navManager.registerScreen("AI_LEARNING", new AILearningDashboardPanel());
        navManager.registerScreen("QUIZ_STUDENT", new QuizPanel());
        navManager.registerScreen("STUDY_PLAN", new StudyPlanPanel());
        navManager.registerScreen("AI_TUTOR", new AITutorPanel());
        navManager.registerScreen("TEACHER_DASHBOARD", new TeacherDashboardPanel());
        // TODO: Add more screens as we build them
        // navManager.registerScreen("STUDENT_DASHBOARD", new StudentDashboardPanel());
        // navManager.registerScreen("TEACHER_DASHBOARD", new TeacherDashboardPanel());
        // navManager.registerScreen("AI_LEARNING", new AILearningDashboardPanel());
        // navManager.registerScreen("AI_TUTOR", new AITutorPanel());
        // navManager.registerScreen("STUDY_PLAN", new StudyPlanPanel());
        // navManager.registerScreen("QUIZ_STUDENT", new QuizPanel());
        // navManager.registerScreen("HISTORY", new HistoryPanel());
    }
    
    private void showWelcomeScreen() {
        navManager.navigateTo("STUDENT_DASHBOARD");
    }
    
    /**
     * Navigate to a specific screen
     * @param screenName The registered screen name
     */
    public void navigateTo(String screenName) {
        navManager.navigateTo(screenName);
    }
    
    /**
     * Register a screen panel with CardLayout
     * @param name Unique identifier for the screen
     * @param panel The JPanel to add
     */
    public void registerScreen(String name, JPanel panel) {
        navManager.registerScreen(name, panel);
    }
    
    public CardLayout getCardLayout() {
        return cardLayout;
    }
    
    public JPanel getMainContainer() {
        return mainContainer;
    }
}