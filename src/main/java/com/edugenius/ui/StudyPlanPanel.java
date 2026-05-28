// FILE: src/main/java/com/edugenius/ui/StudyPlanPanel.java
package com.edugenius.ui;

import com.edugenius.ai.AIService;
import com.edugenius.database.DBConnection;
import com.edugenius.model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudyPlanPanel extends JPanel {
    private JTextArea txtPlanDisplay;
    private JButton btnGenerateNewPlan;
    
    private final Color COLOR_NAVY_BASE = new Color(13, 27, 42);
    private final Color COLOR_NAVY_MEDIUM = new Color(26, 46, 69);
    private final Color COLOR_TEAL_PRIMARY = new Color(0, 201, 167);

    public StudyPlanPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_NAVY_BASE);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("🗓️ Personalized AI Study Roadmap");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        btnGenerateNewPlan = new JButton("Generate Custom Weekly Plan ⚡");
        btnGenerateNewPlan.setBackground(COLOR_TEAL_PRIMARY);
        btnGenerateNewPlan.setForeground(COLOR_NAVY_BASE);
        btnGenerateNewPlan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerPanel.add(btnGenerateNewPlan, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Markdown Display Text Area
        txtPlanDisplay = new JTextArea("\n No study plan generated yet. Take a few quizzes or click above to build your roadmap!");
        txtPlanDisplay.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtPlanDisplay.setForeground(Color.WHITE);
        txtPlanDisplay.setBackground(COLOR_NAVY_MEDIUM);
        txtPlanDisplay.setEditable(false);
        txtPlanDisplay.setLineWrap(true);
        txtPlanDisplay.setWrapStyleWord(true);
        txtPlanDisplay.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(txtPlanDisplay);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(36, 59, 85), 1));
        add(scrollPane, BorderLayout.CENTER);

        // Action Bindings
        btnGenerateNewPlan.addActionListener(e -> executeBuildPlanFlow());
        
        // Attempt to pre-load latest cached roadmap from database
        loadLatestPlanFromDatabase();
    }

    private void loadLatestPlanFromDatabase() {
        String sql = "SELECT weekly_plan_markdown FROM AIStudyRecommendations WHERE student_id = ? ORDER BY generated_at DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    txtPlanDisplay.setText(rs.getString("weekly_plan_markdown"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void executeBuildPlanFlow() {
        btnGenerateNewPlan.setEnabled(false);
        txtPlanDisplay.setText("\n 🧠 LLaMA 3.3 is analyzing your topic profile and generating your weekly study guide...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                // Pre-seed sample weak fields context based on what subjects we have
                String weakTopicsSummary = "Java Exception Handling, Memory Allocation in Data Structures, and AXIS configurations in Networks";
                String planMarkdown = AIService.generateStudyPlan(weakTopicsSummary);
                
                // Save it back into the database
                String insertSql = "INSERT INTO AIStudyRecommendations (student_id, course_id, weak_topics, weekly_plan_markdown) VALUES (?, ?, ?, ?)";
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setInt(1, UserSession.getInstance().getUserId());
                    stmt.setInt(2, 1); // Mock course link placeholder
                    stmt.setString(3, weakTopicsSummary);
                    stmt.setString(4, planMarkdown);
                    stmt.executeUpdate();
                }
                return planMarkdown;
            }

            @Override
            protected void done() {
                try {
                    String resultPlan = get();
                    txtPlanDisplay.setText(resultPlan);
                } catch (Exception ex) {
                    txtPlanDisplay.setText("Failed to generate plan: " + ex.getMessage());
                } finally {
                    btnGenerateNewPlan.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}