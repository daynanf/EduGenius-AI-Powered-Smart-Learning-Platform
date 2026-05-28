// FILE: src/main/java/com/edugenius/ui/StudentDashboard.java
package com.edugenius.ui;

import com.edugenius.database.DBConnection;
import com.edugenius.model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends JPanel {
    private final MainApplication mainFrame;
    private JPanel mainContentPanel;
    private CardLayout contentCardLayout;

    // Design System Brand Configs
    private final Color COLOR_NAVY_BASE = new Color(13, 27, 42);     // #0D1B2A
    private final Color COLOR_NAVY_MEDIUM = new Color(26, 46, 69);   // #1A2E45
    private final Color COLOR_TEAL_PRIMARY = new Color(0, 201, 167);  // #00C9A7

    public StudentDashboard(MainApplication mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(COLOR_NAVY_BASE);

        // 1. LEFT SIDEBAR NAVIGATION PANEL
        add(buildSidebarNavigation(), BorderLayout.WEST);

        // 2. RIGHT INNER DYNAMIC VIEWS WORKSPACE
        contentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);
        mainContentPanel.setBackground(COLOR_NAVY_BASE);

        // Add modular dynamic workflow panels
        mainContentPanel.add(buildCatalogHomePane(), "CATALOG_HOME");
        mainContentPanel.add(new StudyPlanPanel(), "STUDY_PLAN");

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel buildSidebarNavigation() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_NAVY_MEDIUM);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(36, 59, 85)));

        // Profile Heading Card
        sidebar.add(Box.createVerticalStrut(20));
        JLabel lblUser = new JLabel(UserSession.getInstance() != null ? UserSession.getInstance().getFullName() : "Student Student");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUser.setForeground(Color.WHITE);
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblUser);

        JLabel lblId = new JLabel(UserSession.getInstance() != null ? UserSession.getInstance().getAauStudentId() : "UGR/00000/00");
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblId.setForeground(COLOR_TEAL_PRIMARY);
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblId);

        sidebar.add(Box.createVerticalStrut(40));

        // Navigation Menu Buttons
        JButton btnHome = createNavButton("📚 My Academic Catalog");
        JButton btnPlan = createNavButton("🗓️ AI Study Planner");
btnPlan.addActionListener(e -> contentCardLayout.show(mainContentPanel, "STUDY_PLAN"));

sidebar.add(btnHome);
sidebar.add(Box.createVerticalStrut(10));
sidebar.add(btnPlan); // Inject into layout stack
        JButton btnLogout = createNavButton("🚪 Logout Session");

        btnHome.addActionListener(e -> contentCardLayout.show(mainContentPanel, "CATALOG_HOME"));
        btnLogout.addActionListener(e -> {
            UserSession.clear();
            mainFrame.switchPanel("SPLASH");
        });

        sidebar.add(btnHome);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(210, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(COLOR_NAVY_BASE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return btn;
    }

    // --- SUB-VIEW: BILINGUAL COURSE SELECTION CATALOGUE ---
    public JPanel buildCatalogHomePane() {
        JPanel pane = new JPanel(new BorderLayout());
        pane.setBackground(COLOR_NAVY_BASE);
        pane.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel header = new JLabel("Your Academic Dashboard Workspace");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(Color.WHITE);
        pane.add(header, BorderLayout.NORTH);

        // Core grid mapping out course selection options
        JPanel grid = new JPanel(new GridLayout(0, 2, 20, 20));
        grid.setBackground(COLOR_NAVY_BASE);

        // Load courses directly from database records
        List<CourseData> targetCourses = fetchEnrolledCourses();

        for (CourseData course : targetCourses) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(COLOR_NAVY_MEDIUM);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(36, 59, 85), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            JLabel codeLbl = new JLabel(course.code);
            codeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            codeLbl.setForeground(COLOR_TEAL_PRIMARY);

            JLabel titleEn = new JLabel(course.nameEn);
            titleEn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            titleEn.setForeground(Color.WHITE);

            // Localized Language Subtitle
            JLabel titleAm = new JLabel(course.nameAm);
            titleAm.setFont(new Font("Nyala", Font.PLAIN, 15));
            titleAm.setForeground(new Color(150, 165, 180));

            JPanel textGroup = new JPanel(new GridLayout(3, 1, 3, 3));
            textGroup.setOpaque(false);
            textGroup.add(codeLbl);
            textGroup.add(titleEn);
            textGroup.add(titleAm);

            JButton btnLaunchAI = new JButton("Launch AI Quiz Engine ⚡");
            btnLaunchAI.setBackground(COLOR_TEAL_PRIMARY);
            btnLaunchAI.setForeground(COLOR_NAVY_BASE);
            btnLaunchAI.setFont(new Font("Segoe UI", Font.BOLD, 12));

            // Wire button click to construct and show the active dynamic test frame
            btnLaunchAI.addActionListener(e -> {
                AIQuizPanel quizPanel = new AIQuizPanel(course.id, course.nameEn);
                mainContentPanel.add(quizPanel, "ACTIVE_QUIZ_" + course.id);
                contentCardLayout.show(mainContentPanel, "ACTIVE_QUIZ_" + course.id);
            });

            card.add(textGroup, BorderLayout.CENTER);
            card.add(btnLaunchAI, BorderLayout.SOUTH);
            grid.add(card);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        pane.add(scroll, BorderLayout.CENTER);

        return pane;
    }

    private List<CourseData> fetchEnrolledCourses() {
        List<CourseData> list = new ArrayList<>();
        String sql = "SELECT course_id, course_code, course_name_en, course_name_am FROM Courses";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new CourseData(
                    rs.getInt("course_id"),
                    rs.getString("course_code"),
                    rs.getString("course_name_en"),
                    rs.getString("course_name_am")
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private static class CourseData {
        int id; String code; String nameEn; String nameAm;
        CourseData(int id, String c, String en, String am) {
            this.id = id; this.code = c; this.nameEn = en; this.nameAm = am;
        }
    }
}