// FILE: src/main/java/com/edugenius/views/auth/WelcomePanel.java
package com.edugenius.views.auth;

import com.edugenius.config.AppTheme;
import com.edugenius.services.AuthService;
import com.edugenius.utils.SecurityUtils;
import com.edugenius.utils.ValidationUtils;
import com.edugenius.views.NavigationManager;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;

import java.util.HashMap;
import java.util.Map;

/**
 * Welcome and Sign-up Screen
 * Left: Hero section with app info
 * Right: Sign-up form with conditional fields for Student/Teacher
 */
public class WelcomePanel extends JPanel {
    
    private AuthService authService;
    
    // Form fields
    private JTextField aauIdField;
    private JTextField fullNameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    
    // Student-specific fields
    private JPanel studentPanel;
    private JComboBox<String> yearCombo;
    private JComboBox<String> semesterCombo;
    
    // Teacher-specific fields
    private JPanel teacherPanel;
    private JTextField subjectField;
    private JLabel teacherLabel;
    
    private JButton signupButton;
    private JLabel errorLabel;
    
    public WelcomePanel() {
        authService = AuthService.getInstance();
        setLayout(new GridBagLayout());
        setBackground(AppTheme.NAVY);  // FIXED: NAVY_BASE instead of NAVY
        initUI();
    }
    
    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        // Left Panel - Hero Section
        JPanel leftPanel = createHeroPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.5;
        add(leftPanel, gbc);
        
        // Right Panel - Signup Card
        JPanel rightPanel = createSignupCard();
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        add(rightPanel, gbc);
    }
    
    private JPanel createHeroPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.NAVY_MED);  
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Logo
        JLabel logoLabel = new JLabel("EduGenius");
        logoLabel.setFont(AppTheme.FONT_HERO);
        logoLabel.setForeground(AppTheme.TEAL);  // FIXED: TEXT_LIGHT instead of WHITE
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 40, 0);
        panel.add(logoLabel, gbc);
        
        // Hero Title
        JLabel heroTitle = new JLabel("Study Smarter,");
        heroTitle.setFont(AppTheme.FONT_H1);
        heroTitle.setForeground(AppTheme.TEXT_LIGHT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        panel.add(heroTitle, gbc);
        
        JLabel heroTitle2 = new JLabel("Not Harder");
        heroTitle2.setFont(AppTheme.FONT_H1);
        heroTitle2.setForeground(AppTheme.TEAL);  // FIXED: TEAL_PRIMARY instead of TEAL
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(heroTitle2, gbc);
        
        // Subtitle
        JLabel subtitle = new JLabel("AI-powered learning for AAU CS students");
        subtitle.setFont(AppTheme.FONT_BODY);
        subtitle.setForeground(AppTheme.TEXT_MUTED);  // FIXED: TEXT_MUTED instead of MUTED
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 30, 0);
        panel.add(subtitle, gbc);
        
        // Feature list
        String[] features = {
            "> AI Quiz Generation — practice any topic instantly",
            "> Smart Study Plans — personalized weekly roadmap",
            "> AI Tutor — your 24/7 CS study assistant",
            "> Progress Analytics — know exactly where you stand"
        };
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            featureLabel.setForeground(AppTheme.TEXT_LIGHT);
            gbc.gridy++;
            gbc.insets = new Insets(0, 0, 20, 0);
            panel.add(featureLabel, gbc);
        }
        
        // Powered by badge
        JPanel bottomSpacer = new JPanel();
        bottomSpacer.setOpaque(false);
        gbc.gridy++;
        gbc.weighty = 1;
        panel.add(bottomSpacer, gbc);
        
        JLabel poweredLabel = new JLabel("Powered by Groq AI · LLaMA 3.3");
        poweredLabel.setFont(AppTheme.FONT_SMALL);
        poweredLabel.setForeground(AppTheme.TEAL);
        poweredLabel.setHorizontalAlignment(JLabel.CENTER);
        poweredLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.TEAL, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(poweredLabel, gbc);
        
        return panel;
    }
    
    private JPanel createSignupCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(32, 32, 32, 32)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        
        // Title
        JLabel titleLabel = new JLabel("Create your account");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.INK);
        gbc.gridy = 0;
        card.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Use your AAU Student or Staff ID");
        subtitleLabel.setFont(AppTheme.FONT_MUTED);
        subtitleLabel.setForeground(AppTheme.TEAL);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 15, 24, 0);
        card.add(subtitleLabel, gbc);
        
        // AAU ID Field
        JLabel aauLabel = new JLabel("AAU ID");
        aauLabel.setFont(AppTheme.FONT_LABEL);
        aauLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 8, 4, 0);
        card.add(aauLabel, gbc);
        
        aauIdField = new JTextField();
        aauIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aauIdField.setBorder(createFocusableRoundedBorder(8, 10));
        aauIdField.setToolTipText("Format: UGR/1234/15, SGR/1234/15, or EMP/1234/15");
        addFocusHighlight(aauIdField);        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(aauIdField, gbc);
        
        // Full Name Field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(AppTheme.FONT_LABEL);
        nameLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 8, 4, 0);
        card.add(nameLabel, gbc);
        
        fullNameField = new JTextField();
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullNameField.setBorder(createFocusableRoundedBorder(8, 10));
        addFocusHighlight(fullNameField);        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(fullNameField, gbc);
        
        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(AppTheme.FONT_LABEL);
        passLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 8, 4, 0);
        card.add(passLabel, gbc);
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(createFocusableRoundedBorder(8, 10));
        addFocusHighlight(passwordField);        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(passwordField, gbc);
        
        // Role Dropdown
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(AppTheme.FONT_LABEL);
        roleLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 8, 4, 0);
        card.add(roleLabel, gbc);
        
        roleCombo = new JComboBox<>(new String[]{"Student", "Teacher"});
        roleCombo.setFont(AppTheme.FONT_BODY);
        roleCombo.setBorder(createFocusableRoundedBorder(8, 10));
        roleCombo.addActionListener(e -> toggleConditionalFields());
        addFocusHighlight(roleCombo);        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(roleCombo, gbc);
        
        // Conditional Fields Container
        JPanel conditionalContainer = new JPanel(new CardLayout());
        conditionalContainer.setOpaque(false);
        
        // Student Panel
        studentPanel = createStudentPanel();
        conditionalContainer.add(studentPanel, "STUDENT");
        
        // Teacher Panel 
        teacherPanel = createTeacherPanel();
        conditionalContainer.add(teacherPanel, "TEACHER");
        
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(conditionalContainer, gbc);
        
        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.CORAL);
        gbc.gridy = 11;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(errorLabel, gbc);
        
        // Signup Button
        signupButton = new JButton("Create Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        signupButton.setFont(AppTheme.FONT_BODY_BOLD);
        signupButton.setBackground(AppTheme.TEAL);
        signupButton.setForeground(AppTheme.WHITE);
        signupButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        signupButton.setFocusPainted(false);
        signupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupButton.setContentAreaFilled(false);
        signupButton.addActionListener(e -> handleSignup());
        gbc.gridy = 12;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(signupButton, gbc);
        
        // Login Link
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginLinkPanel.setOpaque(false);
        
        JLabel haveAccountLabel = new JLabel("Already have an account?");
        haveAccountLabel.setFont(AppTheme.FONT_MUTED);
        haveAccountLabel.setForeground(AppTheme.MUTED);
        loginLinkPanel.add(haveAccountLabel);
        
        JLabel signInLabel = new JLabel("Sign In");
        signInLabel.setFont(AppTheme.FONT_BODY_BOLD);
        signInLabel.setForeground(AppTheme.TEAL);
        signInLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NavigationManager.getInstance().navigateTo("LOGIN");
            }
        });
        loginLinkPanel.add(signInLabel);
        
        gbc.gridy = 13;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(loginLinkPanel, gbc);
        
        // Initially show student fields
        toggleConditionalFields();
        
        return card;
    }
    
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 45));
        yearCombo = new JComboBox<>(new String[]{"Year 1", "Year 2", "Year 3", "Year 4",});
        yearCombo.setFont(AppTheme.FONT_BODY);
        semesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2"});
        semesterCombo.setFont(AppTheme.FONT_BODY);
        yearCombo.setBorder(createFocusableRoundedBorder(4, 8));
        semesterCombo.setBorder(createFocusableRoundedBorder(4, 8));
        addFocusHighlight(yearCombo);
        addFocusHighlight(semesterCombo);        panel.add(yearCombo);
        panel.add(semesterCombo);
        
        return panel;
    }
    
    private JPanel createTeacherPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Label for the course field
        teacherLabel = new JLabel("What course do you teach?");
        teacherLabel.setFont(AppTheme.FONT_SMALL);
        teacherLabel.setForeground(AppTheme.MUTED);
        panel.add(teacherLabel, BorderLayout.NORTH);
        
        subjectField = new JTextField();
        subjectField.setFont(AppTheme.FONT_BODY);
        subjectField.setBorder(createFocusableRoundedBorder(8, 10));
        subjectField.setToolTipText("e.g., Data Structures, Java Programming, Database Systems");
        addFocusHighlight(subjectField);        
        panel.add(subjectField, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void toggleConditionalFields() {
        boolean isStudent = roleCombo.getSelectedItem().equals("Student");
        CardLayout cl = (CardLayout) ((JPanel) studentPanel.getParent()).getLayout();
        cl.show((JPanel) studentPanel.getParent(), isStudent ? "STUDENT" : "TEACHER");
    }
    
    private void handleSignup() {
        String aauId = aauIdField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleCombo.getSelectedItem().toString().toUpperCase();
        
        // Validation
        String idError = ValidationUtils.getAauIdErrorMessage(aauId);
        if (idError != null) {
            errorLabel.setText(idError);
            return;
        }
        
        String nameError = ValidationUtils.getFullNameErrorMessage(fullName);
        if (nameError != null) {
            errorLabel.setText(nameError);
            return;
        }
        
        if (!SecurityUtils.isPasswordStrong(password)) {
            errorLabel.setText("Password must be at least 8 characters");
            return;
        }
        
        // Additional fields based on role
        Map<String, Object> extraFields = new HashMap<>();
        if (role.equals("STUDENT")) {
            int year = yearCombo.getSelectedIndex() + 1;
            int semester = semesterCombo.getSelectedIndex() + 1;
            extraFields.put("year", year);
            extraFields.put("semester", semester);
            extraFields.put("department", "Computer Science");
        } else {
            String subject = subjectField.getText().trim();
            if (subject.isEmpty()) {
                errorLabel.setText("Please enter the course you teach");
                return;
            }
            extraFields.put("subject_area", subject);
            extraFields.put("department", "Computer Science");
        }
        
        // Show loading
        signupButton.setText("Creating account...");
        signupButton.setEnabled(false);
        errorLabel.setText(" ");
        
        // Perform signup in background
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    authService.register(aauId, fullName, password, role, extraFields);
                    return true;
                } catch (Exception e) {
                    errorLabel.setText(e.getMessage());
                    return false;
                }
            }
            
            @Override
            protected void done() {
                signupButton.setText("Create Account");
                signupButton.setEnabled(true);
                
                try {
                    if (get()) {
                        // Success - navigate to appropriate dashboard
                        String userRole = authService.getCurrentUser().getRole();
                        if (userRole.equals("STUDENT")) {
                            NavigationManager.getInstance().navigateTo("STUDENT_DASHBOARD");
                        } else {
                            NavigationManager.getInstance().navigateTo("TEACHER_DASHBOARD");
                        }
                    }
                } catch (Exception e) {
                    errorLabel.setText("Registration failed: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private Border createRoundedBorder(int vPadding, int hPadding) {
        return new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(vPadding, hPadding, vPadding, hPadding);
            }
        };
    }
    
    private Border createFocusableRoundedBorder(int vPadding, int hPadding) {
        return new AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.hasFocus() ? AppTheme.TEAL : AppTheme.BORDER);
                g2.setStroke(new BasicStroke(c.hasFocus() ? 2 : 1));
                g2.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(vPadding, hPadding, vPadding, hPadding);
            }
        };
    }
    
    private void addFocusHighlight(JComponent component) {
        component.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                component.repaint();
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                component.repaint();
            }
        });
    }
}