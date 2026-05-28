// FILE: src/main/java/com/edugenius/ui/InstructorDashboard.java
package com.edugenius.ui;

import com.edugenius.database.DBConnection;
import com.edugenius.model.UserSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InstructorDashboard extends JPanel {
    private final MainApplication mainFrame;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    private final Color COLOR_NAVY_BASE = new Color(13, 27, 42);
    private final Color COLOR_NAVY_MEDIUM = new Color(26, 46, 69);
    private final Color COLOR_TEAL_PRIMARY = new Color(0, 201, 167);

    public InstructorDashboard(MainApplication mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(COLOR_NAVY_BASE);

        // Teacher Sidebar Navigation Header Pane
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_NAVY_MEDIUM);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(36, 59, 85)));

        sidebar.add(Box.createVerticalStrut(20));
        JLabel lblUser = new JLabel("Instructor Workspace");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUser.setForeground(Color.WHITE);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblUser);

        sidebar.add(Box.createVerticalStrut(40));
        JButton btnLogout = new JButton("🚪 Logout Session");
        btnLogout.setMaximumSize(new Dimension(210, 40));
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setBackground(COLOR_NAVY_BASE);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.addActionListener(e -> {
            UserSession.clear();
            mainFrame.switchPanel("SPLASH");
        });
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // Main Workspace Dashboard Contents Panel
        JPanel mainWorkspace = new JPanel(new BorderLayout());
        mainWorkspace.setOpaque(false);
        mainWorkspace.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel lblHeading = new JLabel("Instructor Portal — Roster & Course Metrics");
        lblHeading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeading.setForeground(Color.WHITE);
        mainWorkspace.add(lblHeading, BorderLayout.NORTH);

        // Student Roster Display Table Model Config
        String[] columnNames = {"User ID", "Full Name", "Email Address", "Registered AAU ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        studentTable = new JTable(tableModel);
        studentTable.setBackground(COLOR_NAVY_MEDIUM);
        studentTable.setForeground(Color.WHITE);
        studentTable.setFillsViewportHeight(true);
        studentTable.setRowHeight(25);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane tableScroll = new JScrollPane(studentTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(36, 59, 85), 1));
        mainWorkspace.add(tableScroll, BorderLayout.CENTER);

        add(mainWorkspace, BorderLayout.CENTER);

        // Pull active student data catalog entries
        refreshStudentTableRoster();
    }

    private void refreshStudentTableRoster() {
        tableModel.setRowCount(0);
        String sql = "SELECT user_id, full_name, email, aau_student_id FROM Users WHERE role = 'Student'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("aau_student_id")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}