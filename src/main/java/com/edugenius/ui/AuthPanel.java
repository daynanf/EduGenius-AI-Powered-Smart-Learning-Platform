// FILE: src/main/java/com/edugenius/ui/AuthPanel.java
package com.edugenius.ui;

import com.edugenius.database.DBConnection;
import com.edugenius.model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

public class AuthPanel extends JPanel {
    private final MainApplication mainFrame;
    private CardLayout authCardLayout;
    private JPanel cardsContainer;

    // AAU Brand Color Specs
    private final Color COLOR_NAVY_BASE = new Color(13, 27, 42);     // #0D1B2A
    private final Color COLOR_NAVY_MEDIUM = new Color(26, 46, 69);   // #1A2E45
    private final Color COLOR_TEAL_PRIMARY = new Color(0, 201, 167);  // #00C9A7

    // AAU Registration Identification Validation Expression
    private final Pattern AAU_ID_PATTERN = Pattern.compile("^(UGR|SGR|EMP)/[0-9]{4,5}/[0-9]{2}$");

    public AuthPanel(MainApplication mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        authCardLayout = new CardLayout();
        cardsContainer = new JPanel(authCardLayout);

        cardsContainer.add(buildLoginCard(), "LOGIN");
        cardsContainer.add(buildRegisterCard(), "REGISTER");

        add(cardsContainer, BorderLayout.CENTER);
        authCardLayout.show(cardsContainer, "LOGIN");
    }

    // --- SECURE CRYPTOGRAPHIC HASHING ENGINE ---
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Secure Engine Failure: SHA-256 algorithm missing!", e);
        }
    }

    // --- CARD PANEL 1: LOGIN COMPONENT DESIGN ---
    private JPanel buildLoginCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_NAVY_BASE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("EduGenius Gateway", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEAL_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel subLabel = new JLabel("AAU AI-Powered Learning Platform", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(new Color(180, 190, 200));
        gbc.gridy = 1;
        panel.add(subLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 2;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        panel.add(userLabel, gbc);

        JTextField txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(240, 32));
        gbc.gridx = 1;
        panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel, gbc);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setPreferredSize(new Dimension(240, 32));
        gbc.gridx = 1;
        panel.add(txtPass, gbc);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(COLOR_TEAL_PRIMARY);
        btnLogin.setForeground(COLOR_NAVY_BASE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 10, 15);
        panel.add(btnLogin, gbc);

        JButton btnGoRegister = new JButton("New student or staff? Create an account here");
        btnGoRegister.setBorderPainted(false);
        btnGoRegister.setContentAreaFilled(false);
        btnGoRegister.setForeground(COLOR_TEAL_PRIMARY);
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 15, 10, 15);
        panel.add(btnGoRegister, gbc);

        // Action routing listeners
        btnGoRegister.addActionListener(e -> authCardLayout.show(cardsContainer, "REGISTER"));
        btnLogin.addActionListener(e -> executeLoginFlow(txtUser.getText().trim(), new String(txtPass.getPassword())));

        return panel;
    }

    // --- CARD PANEL 2: REGISTRATION COMPONENT DESIGN ---
    private JPanel buildRegisterCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_NAVY_BASE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Institutional Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEAL_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setForeground(Color.WHITE);
        panel.add(nameLabel, gbc);
        JTextField txtName = new JTextField();
        gbc.gridx = 1; panel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        panel.add(userLabel, gbc);
        JTextField txtUser = new JTextField();
        gbc.gridx = 1; panel.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel, gbc);
        JTextField txtEmail = new JTextField();
        gbc.gridx = 1; panel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel roleLabel = new JLabel("System Role:");
        roleLabel.setForeground(Color.WHITE);
        panel.add(roleLabel, gbc);
        JComboBox<String> comboRole = new JComboBox<>(new String[]{"Student", "Instructor"});
        gbc.gridx = 1; panel.add(comboRole, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel idLabel = new JLabel("AAU ID (e.g. UGR/87321/16):");
        idLabel.setForeground(Color.WHITE);
        panel.add(idLabel, gbc);
        JTextField txtAAUID = new JTextField();
        gbc.gridx = 1; panel.add(txtAAUID, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        panel.add(passLabel, gbc);
        JPasswordField txtPass = new JPasswordField();
        gbc.gridx = 1; panel.add(txtPass, gbc);

        JButton btnRegister = new JButton("Register Profile");
        btnRegister.setBackground(COLOR_TEAL_PRIMARY);
        btnRegister.setForeground(COLOR_NAVY_BASE);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 15, 5, 15);
        panel.add(btnRegister, gbc);

        JButton btnGoLogin = new JButton("Already registered? Return to Login");
        btnGoLogin.setBorderPainted(false);
        btnGoLogin.setContentAreaFilled(false);
        btnGoLogin.setForeground(COLOR_TEAL_PRIMARY);
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 15, 10, 15);
        panel.add(btnGoLogin, gbc);

        btnGoLogin.addActionListener(e -> authCardLayout.show(cardsContainer, "LOGIN"));
        btnRegister.addActionListener(e -> executeRegistrationFlow(
                txtName.getText().trim(), txtUser.getText().trim(), txtEmail.getText().trim(),
                comboRole.getSelectedItem().toString(), txtAAUID.getText().trim(), new String(txtPass.getPassword())
        ));

        return panel;
    }

    // --- BACKEND ACTION 1: EXECUTE LOGIN PIPELINE ---
    private void executeLoginFlow(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be left blank!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT user_id, username, full_name, email, role, aau_student_id FROM Users WHERE username = ? AND password_hash = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Populate Session Context State
                    UserSession.initialize(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("aau_student_id")
                    );

                    JOptionPane.showMessageDialog(this, "Welcome back, " + UserSession.getInstance().getFullName() + "!", "Access Granted", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Route user to their respective system context dashboard
                    if (UserSession.getInstance().getRole().equalsIgnoreCase("Instructor")) {
                        mainFrame.switchPanel("INSTRUCTOR_HOME");
                    } else {
                        mainFrame.switchPanel("STUDENT_HOME");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Access Denied", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- BACKEND ACTION 2: EXECUTE REGISTRATION FLOW WITH VALIDATIONS ---
    private void executeRegistrationFlow(String name, String user, String email, String role, String aauId, String pass) {
        if (name.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All core textual fields are strictly mandatory!", "Validation Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Apply institutional ID format filtering rules if the profile registration is a student
        if (role.equalsIgnoreCase("Student")) {
            if (!AAU_ID_PATTERN.matcher(aauId).matches()) {
                JOptionPane.showMessageDialog(this, "Invalid AAU ID format!\nMust match schema: UGR/XXXXX/XX or PGR/XXXXX/XX", "Format Mismatch", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String sql = "INSERT INTO Users (username, password_hash, full_name, email, role, aau_student_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user);
            stmt.setString(2, hashPassword(pass));
            stmt.setString(3, name);
            stmt.setString(4, email);
            stmt.setString(5, role);
            stmt.setString(6, role.equalsIgnoreCase("Student") ? aauId : null);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account profile successfully configured! You can log in now.", "Success", JOptionPane.INFORMATION_MESSAGE);
            authCardLayout.show(cardsContainer, "LOGIN");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration Failure (Duplicate unique data constraint encountered):\n" + ex.getMessage(), "Database Rejection", JOptionPane.ERROR_MESSAGE);
        }
    }
}
