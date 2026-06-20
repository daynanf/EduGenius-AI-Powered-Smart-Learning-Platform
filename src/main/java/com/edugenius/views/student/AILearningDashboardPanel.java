// FILE: src/main/java/com/edugenius/views/student/AILearningDashboardPanel.java
package com.edugenius.views.student;

import com.edugenius.config.AppTheme;
import com.edugenius.models.Course;
import com.edugenius.services.CourseService;
import com.edugenius.services.QuizService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.ParameterReceiver;
import com.edugenius.views.components.FeatureCard;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * AI Learning Dashboard - Central hub for all AI features
 * Shows 3 main options: Study Plan, AI Quiz, AI Tutor
 */
public class AILearningDashboardPanel extends JPanel implements ParameterReceiver {

    private Course currentCourse;
    private CourseService courseService;
    private QuizService quizService;
    private JLabel courseNameLabel;

    public AILearningDashboardPanel() {
        courseService = new CourseService();
        quizService = new QuizService();
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE);
    }

    @Override
    public void receiveParameters(Map<String, Object> params) {
        int courseId = (int) params.get("courseId");
        currentCourse = courseService.getCourseById(courseId);

        if (currentCourse != null) {
            initUI();
        }
    }

    private void initUI() {
        removeAll();

        // North: Navigation Bar with breadcrumb
        add(createNavBar(), BorderLayout.NORTH);

        // Center: Main Content
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(AppTheme.NAVY);
        navBar.setPreferredSize(new Dimension(0, 56));
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        // Left: Back button + Breadcrumb
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        leftPanel.setOpaque(false);

        JLabel backLabel = new JLabel("← Dashboard");
        backLabel.setFont(AppTheme.FONT_BODY);
        backLabel.setForeground(AppTheme.TEAL);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NavigationManager.getInstance().navigateTo("STUDENT_DASHBOARD");
            }
        });
        leftPanel.add(backLabel);

        JLabel separatorLabel = new JLabel("/");
        separatorLabel.setFont(AppTheme.FONT_BODY);
        separatorLabel.setForeground(AppTheme.MUTED);
        leftPanel.add(separatorLabel);

        courseNameLabel = new JLabel(currentCourse.getCourseName());
        courseNameLabel.setFont(AppTheme.FONT_BODY_BOLD);
        courseNameLabel.setForeground(AppTheme.WHITE);
        leftPanel.add(courseNameLabel);

        navBar.add(leftPanel, BorderLayout.WEST);

        // Right: Profile (simplified)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JLabel profileLabel = new JLabel(" Student");
        profileLabel.setFont(AppTheme.FONT_SMALL);
        profileLabel.setForeground(AppTheme.MUTED);
        rightPanel.add(profileLabel);

        navBar.add(rightPanel, BorderLayout.EAST);

        return navBar;
    }

    private JPanel createMainContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(AppTheme.SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(24, 32, 32, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Course Hero Banner
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(createHeroBanner(), gbc);

        // Section Label
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        JLabel sectionLabel = new JLabel("CHOOSE A LEARNING MODE");
        sectionLabel.setFont(AppTheme.FONT_LABEL);
        sectionLabel.setForeground(AppTheme.MUTED);
        content.add(sectionLabel, gbc);

        // 3 Feature Cards
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(createFeatureCards(), gbc);

        return content;
    }

    private JPanel createHeroBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(AppTheme.NAVY_MED);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Left side
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel courseCodeLabel = new JLabel(currentCourse.getCourseCode());
        courseCodeLabel.setFont(AppTheme.FONT_MUTED);
        courseCodeLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        leftPanel.add(courseCodeLabel, gbc);

        JLabel titleLabel = new JLabel(currentCourse.getCourseName());
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        leftPanel.add(titleLabel, gbc);

        banner.add(leftPanel, BorderLayout.WEST);

        return banner;
    }

    private JPanel createFeatureCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(AppTheme.SURFACE);

        // Card 1: Study Plan (Purple)
        FeatureCard studyPlanCard = new FeatureCard(
                "📚",
                "Study Plan",
                "AI-generated weekly roadmap tailored to your pace",
                AppTheme.PURPLE,
                () -> navigateToStudyPlan());

        // Card 2: AI Quiz (Teal)
        FeatureCard quizCard = new FeatureCard(
                "⚡",
                "AI Quiz Generator",
                "Practice any topic with smart AI-generated questions",
                AppTheme.TEAL,
                () -> navigateToQuiz());

        // Card 3: AI Tutor (Amber)
        FeatureCard tutorCard = new FeatureCard(
                "🤖",
                "AI Tutor",
                "24/7 CS assistant ready to explain any concept",
                AppTheme.AMBER,
                () -> navigateToTutor());

        cardsPanel.add(studyPlanCard);
        cardsPanel.add(quizCard);
        cardsPanel.add(tutorCard);

        return cardsPanel;
    }

    private void navigateToStudyPlan() {
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", currentCourse.getCourseId());
        params.put("courseName", currentCourse.getCourseName());
        NavigationManager.getInstance().navigateTo("STUDY_PLAN", params);
    }

    private void navigateToQuiz() {
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", currentCourse.getCourseId());
        params.put("courseName", currentCourse.getCourseName());
        params.put("mode", "STUDENT");
        NavigationManager.getInstance().navigateTo("QUIZ_STUDENT", params);
    }

    private void navigateToTutor() {
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", currentCourse.getCourseId());
        params.put("courseName", currentCourse.getCourseName());
        NavigationManager.getInstance().navigateTo("AI_TUTOR", params);
    }
}