// FILE: src/main/java/com/edugenius/views/auth/LoginPanel.java
package com.edugenius.views.auth;

import com.edugenius.config.AppTheme;
import com.edugenius.models.User;
import com.edugenius.services.AuthService;
import com.edugenius.views.NavigationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 * Login Screen
 * Clean, centered card layout with AAU ID and password fields
 */
public class LoginPanel extends JPanel {
    
    private AuthService authService;
    private JTextField aauIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    
    public LoginPanel() {
        authService = AuthService.getInstance();
        setLayout(new GridBagLayout());
        setBackground(AppTheme.NAVY);
        initUI();
    }
    
    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Main Login Card
        JPanel card = createLoginCard();
        card.setPreferredSize(new Dimension(420, 500));
        add(card, gbc);
    }
    
    private JPanel createLoginCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(40, 40, 40, 40)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);
        
        // Logo
        JLabel logoLabel = new JLabel("EduGenius");
        logoLabel.setFont(AppTheme.FONT_HERO);
        logoLabel.setForeground(AppTheme.TEAL);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 20, 0);
        card.add(logoLabel, gbc);
        
        // Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.MUTED);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in with your AAU credentials");
        subtitleLabel.setFont(AppTheme.FONT_MUTED);
        subtitleLabel.setForeground(AppTheme.TEAL);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 32, 0);
        card.add(subtitleLabel, gbc);
        
        // AAU ID Field
        JLabel aauLabel = new JLabel("AAU ID");
        aauLabel.setFont(AppTheme.FONT_LABEL);
        aauLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(aauLabel, gbc);
        
        aauIdField = new JTextField();
        aauIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aauIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        aauIdField.setToolTipText("Example: UGR/1234/15");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(aauIdField, gbc);
        
        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 4, 0);
        card.add(passLabel, gbc);
        
        passwordField = new JPasswordField();
        passwordField.setFont(AppTheme.FONT_BODY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(passwordField, gbc);
        
        // Forgot Password Link
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setOpaque(false);
        
        JLabel forgotLabel = new JLabel("Forgot password?");
        forgotLabel.setFont(AppTheme.FONT_SMALL);
        forgotLabel.setForeground(AppTheme.TEAL_DARK);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginPanel.this,
                    "Please contact your instructor to reset your password.",
                    "Reset Password",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        forgotPanel.add(forgotLabel);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 24, 0);
        card.add(forgotPanel, gbc);
        
        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(AppTheme.FONT_SMALL);
        errorLabel.setForeground(AppTheme.CORAL);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 16, 0);
        card.add(errorLabel, gbc);
        
        // Login Button
        loginButton = new JButton("Sign In");
        loginButton.setFont(AppTheme.FONT_BODY_BOLD);
        loginButton.setBackground(AppTheme.TEAL);
        loginButton.setForeground(AppTheme.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(loginButton, gbc);
        
        // Back to Welcome Link
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);
        
        JLabel backLabel = new JLabel("← Back to Welcome");
        backLabel.setFont(AppTheme.FONT_SMALL);
        backLabel.setForeground(AppTheme.MUTED);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                NavigationManager.getInstance().navigateTo("WELCOME");
            }
        });
        backPanel.add(backLabel);
        
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(backPanel, gbc);
        
        // Enable Enter key to login
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
            KeyStroke.getKeyStroke("ENTER"), "login");
        getActionMap().put("login", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        return card;
    }
    
    private void handleLogin() {
        String aauId = aauIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (aauId.isEmpty()) {
            errorLabel.setText("Please enter your AAU ID");
            return;
        }
        
        if (password.isEmpty()) {
            errorLabel.setText("Please enter your password");
            return;
        }
        
        // Show loading
        loginButton.setText("Signing in...");
        loginButton.setEnabled(false);
        errorLabel.setText(" ");
        
        // Perform login in background
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(aauId, password);
            }
            
            @Override
            protected void done() {
                loginButton.setText("Sign In");
                loginButton.setEnabled(true);
                
                try {
                    User user = get();
                    // Login successful - navigate to appropriate dashboard
                    if (user.getRole().equals("STUDENT")) {
                        NavigationManager.getInstance().navigateTo("STUDENT_DASHBOARD");
                    } else if (user.getRole().equals("TEACHER")) {
                        NavigationManager.getInstance().navigateTo("TEACHER_DASHBOARD");
                    } else {
                        NavigationManager.getInstance().navigateTo("WELCOME");
                    }
                } catch (Exception e) {
                    errorLabel.setText("Invalid AAU ID or password");
                    passwordField.setText("");
                }
            }
        };
        worker.execute();
    }
}