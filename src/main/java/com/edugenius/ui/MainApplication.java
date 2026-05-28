// FILE: src/main/java/com/edugenius/ui/MainApplication.java
package com.edugenius.ui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel containerPanel;
    private AuthPanel authPanel;
    private JPanel instructorPanel;

    public MainApplication() {
        setTitle("EduGenius — Smart Learning Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700); 
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // Core View Registries
        authPanel = new AuthPanel(this);
        instructorPanel = createPlaceholderPanel("Instructor Panel (Will be built out next!)");

        containerPanel.add(authPanel, "SPLASH");
        containerPanel.add(instructorPanel, "INSTRUCTOR_HOME");

        add(containerPanel);
        cardLayout.show(containerPanel, "SPLASH");
    }

    public void switchPanel(String cardName) {
        if (cardName.equalsIgnoreCase("STUDENT_HOME")) {
            StudentDashboard studentDashboard = new StudentDashboard(this);
            containerPanel.add(studentDashboard, "STUDENT_HOME");
        } else if (cardName.equalsIgnoreCase("INSTRUCTOR_HOME")) {
            InstructorDashboard instructorDashboard = new InstructorDashboard(this);
            containerPanel.add(instructorDashboard, "INSTRUCTOR_HOME");
        }
        cardLayout.show(containerPanel, cardName);
    }

    private JPanel createPlaceholderPanel(String debugText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(13, 27, 42)); 
        JLabel label = new JLabel(debugText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(new Color(0, 201, 167)); 
        panel.add(label);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8); 
            UIManager.put("Component.arc", 8);
            new MainApplication().setVisible(true);
        });
    }
}