// FILE: src/main/java/com/edugenius/views/student/StudyPlanPanel.java
package com.edugenius.views.student;

import com.edugenius.config.AppTheme;
import com.edugenius.ai.StudyPlanAIService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.ParameterReceiver;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StudyPlanPanel extends JPanel implements ParameterReceiver {
    
    private StudyPlanAIService aiService;
    private int currentCourseId;
    private String currentCourseName;
    private JTextArea planTextArea;
    private JTextField promptField;
    private JPanel loadingPanel;
    
    public StudyPlanPanel() {
        aiService = StudyPlanAIService.getInstance();
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE);
        initUI();
    }
    
    @Override
    public void receiveParameters(Map<String, Object> params) {
        this.currentCourseId = (int) params.get("courseId");
        this.currentCourseName = (String) params.get("courseName");
    }
    
    private void initUI() {
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppTheme.NAVY);
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(AppTheme.FONT_BODY);
        backButton.setForeground(AppTheme.TEAL);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> NavigationManager.getInstance().navigateTo("AI_LEARNING"));
        topBar.add(backButton, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("📚 AI Study Plan Generator");
        titleLabel.setFont(AppTheme.FONT_H2);
        titleLabel.setForeground(AppTheme.WHITE);
        topBar.add(titleLabel, BorderLayout.CENTER);
        
        add(topBar, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(AppTheme.SURFACE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        
        // Prompt card (Rounded using Graphics2D)
        JPanel promptCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.dispose();
            }
        };
        promptCard.setOpaque(false);
        promptCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel promptLabel = new JLabel("Describe your learning goal:");
        promptLabel.setFont(AppTheme.FONT_H3);
        promptLabel.setForeground(AppTheme.INK);
        promptCard.add(promptLabel, BorderLayout.NORTH);
        
        // Rounded promptField using Graphics2D
        promptField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        promptField.setOpaque(false);
        promptField.setFont(AppTheme.FONT_BODY);
        promptField.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        promptField.setText("I want to master " + (currentCourseName != null ? currentCourseName : "this course") + " in 2 weeks");
        
        // Wrap prompt field in a container to give it some padding above and below
        JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setOpaque(false);
        fieldWrapper.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        fieldWrapper.add(promptField, BorderLayout.CENTER);
        promptCard.add(fieldWrapper, BorderLayout.CENTER);
        
        // Rounded generateButton using Graphics2D
        JButton generateButton = new JButton("✨ Generate Study Plan") {
            private boolean isHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                    @ Override
                    public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? AppTheme.PURPLE.darker() : AppTheme.PURPLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.setColor(AppTheme.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        generateButton.setFont(AppTheme.FONT_BODY_BOLD);
        generateButton.setPreferredSize(new Dimension(0, 44));
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.addActionListener(e -> generateStudyPlan());
        promptCard.add(generateButton, BorderLayout.SOUTH);
        
        gbc.gridy = 0;
        centerPanel.add(promptCard, gbc);
        
        // Plan display area (Rounded wrapper container)
        JPanel planContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.dispose();
            }
        };
        planContainer.setOpaque(false);
        planContainer.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        planTextArea = new JTextArea();
        planTextArea.setFont(AppTheme.FONT_BODY);
        planTextArea.setForeground(AppTheme.INK);
        planTextArea.setOpaque(false);
        planTextArea.setEditable(false);
        planTextArea.setLineWrap(true);
        planTextArea.setWrapStyleWord(true);
        planTextArea.setBorder(null);
        
        JScrollPane scrollPane = new JScrollPane(planTextArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        planContainer.add(scrollPane, BorderLayout.CENTER);
        
        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(planContainer, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Sample plan
        planTextArea.setText("Click 'Generate Study Plan' to get your personalized AI-generated learning roadmap!");
    }
    
    private void generateStudyPlan() {
        String prompt = promptField.getText().trim();
        if (prompt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe your learning goal!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        planTextArea.setText("🤖 AI is generating your personalized study plan...\n\nThis may take a few seconds...");
        
        aiService.generateStudyPlan(prompt, currentCourseName,
            plan -> {
                planTextArea.setText(plan);
            },
            error -> {
                planTextArea.setText("Error generating study plan: " + error + "\n\nPlease try again.");
            }
        );
    }
}