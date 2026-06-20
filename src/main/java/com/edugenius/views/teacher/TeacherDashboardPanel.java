// FILE: src/main/java/com/edugenius/views/teacher/TeacherDashboardPanel.java
package com.edugenius.views.teacher;

import com.edugenius.config.AppTheme;
import com.edugenius.models.QuizQuestion;
import com.edugenius.ai.QuizAIService;
import com.edugenius.services.AuthService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.components.QuestionCard;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeacherDashboardPanel extends JPanel {
    
    private QuizAIService aiService;
    private JTextArea promptArea;
    private JComboBox<String> difficultyCombo;
    private JComboBox<Integer> questionCountCombo;
    private JPanel generatedQuestionsPanel;
    private List<QuizQuestion> generatedQuestions;
    private JButton generateButton;
    private String teacherName = "Teacher";
    
    public TeacherDashboardPanel() {
        aiService = QuizAIService.getInstance();
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE);
        
        try {
            if (AuthService.getInstance().getCurrentUser() != null) {
                teacherName = AuthService.getInstance().getCurrentUser().getFullName().split(" ")[0];
            }
        } catch (Exception e) {
            teacherName = "Teacher";
        }
        
        initUI();
    }
    
    private void initUI() {
        // Top Navigation Bar
        JPanel topBar = createNavBar();
        add(topBar, BorderLayout.NORTH);
        
        // Main Content - Only AI Quiz Creator
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }
    
    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(AppTheme.NAVY);
        navBar.setPreferredSize(new Dimension(0, 56));
        navBar.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        
        // Logo
        JLabel logoLabel = new JLabel("📚 EduGenius - Teacher Portal");
        logoLabel.setFont(AppTheme.FONT_H2);
        logoLabel.setForeground(AppTheme.WHITE);
        navBar.add(logoLabel, BorderLayout.WEST);
        
        // Center title
        JLabel titleLabel = new JLabel("AI Quiz Generator");
        titleLabel.setFont(AppTheme.FONT_BODY);
        titleLabel.setForeground(AppTheme.TEAL);
        navBar.add(titleLabel, BorderLayout.CENTER);
        
        // Right - Teacher Info & Logout
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        profilePanel.setOpaque(false);
        
        JLabel teacherNameLabel = new JLabel("👨‍🏫 " + teacherName);
        teacherNameLabel.setFont(AppTheme.FONT_BODY);
        teacherNameLabel.setForeground(AppTheme.WHITE);
        profilePanel.add(teacherNameLabel);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(AppTheme.FONT_SMALL);
        logoutBtn.setBackground(AppTheme.NAVY_LIGHT);
        logoutBtn.setForeground(AppTheme.WHITE);
        logoutBtn.setBorder(BorderFactory.createLineBorder(AppTheme.MUTED));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            AuthService.getInstance().logout();
            NavigationManager.getInstance().clearHistory();
            NavigationManager.getInstance().navigateTo("WELCOME");
        });
        profilePanel.add(logoutBtn);
        
        navBar.add(profilePanel, BorderLayout.EAST);
        
        return navBar;
    }
    
    private JPanel createMainContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 32, 32));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 16, 0);
        
        // Welcome Banner
        JPanel banner = createWelcomeBanner();
        gbc.gridy = 0;
        panel.add(banner, gbc);
        
        // AI Generator Card
        JPanel generatorCard = createGeneratorCard();
        gbc.gridy = 1;
        panel.add(generatorCard, gbc);
        
        // Generated Questions Panel
        generatedQuestionsPanel = new JPanel();
        generatedQuestionsPanel.setLayout(new BoxLayout(generatedQuestionsPanel, BoxLayout.Y_AXIS));
        generatedQuestionsPanel.setBackground(AppTheme.SURFACE);
        
        JScrollPane scrollPane = new JScrollPane(generatedQuestionsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(AppTheme.SURFACE);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        gbc.gridy = 2;
        gbc.weighty = 1;
        panel.add(scrollPane, gbc);
        
        return panel;
    }
    
    private JPanel createWelcomeBanner() {
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(AppTheme.NAVY_MED);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        
        JLabel welcomeLabel = new JLabel("🎓 Welcome back, " + teacherName + "!");
        welcomeLabel.setFont(AppTheme.FONT_H2);
        welcomeLabel.setForeground(AppTheme.WHITE);
        banner.add(welcomeLabel, BorderLayout.WEST);
        
        return banner;
    }
    
    private JPanel createGeneratorCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel titleLabel = new JLabel("✨ AI Quiz Generator");
        titleLabel.setFont(AppTheme.FONT_H3);
        titleLabel.setForeground(AppTheme.PURPLE);
        header.add(titleLabel, BorderLayout.WEST);
        
        JLabel powerLabel = new JLabel("Powered by Groq AI");
        powerLabel.setFont(AppTheme.FONT_SMALL);
        powerLabel.setForeground(AppTheme.MUTED);
        header.add(powerLabel, BorderLayout.EAST);
        
        gbc.gridy = 0;
        card.add(header, gbc);
        
        // Prompt Area
        promptArea = new JTextArea();
        promptArea.setFont(AppTheme.FONT_BODY);
        promptArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        promptArea.setRows(4);
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setText("Generate a Java OOP quiz for 2nd year CS students covering inheritance and polymorphism");
        
        JScrollPane promptScroll = new JScrollPane(promptArea);
        promptScroll.setBorder(null);
        gbc.gridy = 1;
        card.add(promptScroll, gbc);
        
        // Settings Row
        JPanel settingsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        settingsPanel.setOpaque(false);
        
        difficultyCombo = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        questionCountCombo = new JComboBox<>(new Integer[]{5, 10, 15});
        
        settingsPanel.add(createSettingPanel("Difficulty", difficultyCombo));
        settingsPanel.add(createSettingPanel("Questions", questionCountCombo));
        
        gbc.gridy = 2;
        card.add(settingsPanel, gbc);
        
        // Generate Button
        generateButton = new JButton("⚡ Generate Quiz with AI");
        generateButton.setFont(AppTheme.FONT_BODY_BOLD);
        generateButton.setBackground(AppTheme.TEAL);
        generateButton.setForeground(AppTheme.WHITE);
        generateButton.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.addActionListener(e -> generateQuiz());
        
        gbc.gridy = 3;
        gbc.insets = new Insets(16, 0, 0, 0);
        card.add(generateButton, gbc);
        
        return card;
    }
    
    private JPanel createSettingPanel(String label, JComboBox<?> combo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_SMALL);
        lbl.setForeground(AppTheme.MUTED);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        return panel;
    }
    
    private void generateQuiz() {
        String prompt = promptArea.getText().trim();
        if (prompt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a prompt!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String difficulty = (String) difficultyCombo.getSelectedItem();
        int count = (int) questionCountCombo.getSelectedItem();
        
        generateButton.setText("🤖 AI is generating...");
        generateButton.setEnabled(false);
        
        aiService.generateQuiz(prompt, difficulty, count,
            questions -> {
                generatedQuestions = questions;
                displayGeneratedQuestions(questions);
                generateButton.setText("⚡ Generate Quiz with AI");
                generateButton.setEnabled(true);
            },
            error -> {
                JOptionPane.showMessageDialog(this, "Error: " + error, "Generation Failed", JOptionPane.ERROR_MESSAGE);
                generateButton.setText("⚡ Generate Quiz with AI");
                generateButton.setEnabled(true);
            }
        );
    }
    
    private void displayGeneratedQuestions(List<QuizQuestion> questions) {
        generatedQuestionsPanel.removeAll();
        
        JLabel headerLabel = new JLabel("Generated Questions (" + questions.size() + ")");
        headerLabel.setFont(AppTheme.FONT_H3);
        headerLabel.setForeground(AppTheme.INK);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        generatedQuestionsPanel.add(headerLabel);
        
        JPanel cardsPanel = new JPanel(new GridLayout(0, 2, 16, 16));
        cardsPanel.setBackground(AppTheme.SURFACE);
        
        for (QuizQuestion q : questions) {
            QuestionCard card = new QuestionCard(q, true);
            cardsPanel.add(card);
        }
        
        generatedQuestionsPanel.add(cardsPanel);
        
        // Add Assign button
        JButton assignButton = new JButton("📤 Assign to Class");
        assignButton.setFont(AppTheme.FONT_BODY_BOLD);
        assignButton.setBackground(AppTheme.TEAL);
        assignButton.setForeground(AppTheme.WHITE);
        assignButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        assignButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        assignButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Quiz assigned to class!\n" + questions.size() + " questions sent to enrolled students.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(AppTheme.SURFACE);
        buttonPanel.add(assignButton);
        generatedQuestionsPanel.add(buttonPanel);
        
        generatedQuestionsPanel.revalidate();
        generatedQuestionsPanel.repaint();
    }
}