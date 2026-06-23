package com.edugenius.views.teacher;

import com.edugenius.config.AppTheme;
import com.edugenius.models.QuizQuestion;
import com.edugenius.ai.QuizAIService;
import com.edugenius.ai.StudyPlanAIService;
import com.edugenius.services.AuthService;
import com.edugenius.views.NavigationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class TeacherDashboardPanel extends JPanel {

    private QuizAIService aiService;
    private StudyPlanAIService studyPlanService;
    private JTabbedPane tabbedPane;
    private JTextArea promptArea;
    private JComboBox<String> difficultyCombo;
    private JComboBox<String> contentTypeCombo;
    private JTextField sizeTextField; 
    private JLabel sizeLabel;          
    private JPanel generatedQuestionsPanel; 
    private JButton generateButton;
    
    // Core Storage Banks for Saved History
    private List<List<QuizQuestion>> savedQuizzesLibrary = new ArrayList<>();
    private List<String> savedLessonsLibrary = new ArrayList<>();
    private JPanel libraryGridPanel;

    private JToggleButton quizToggle;
    private JToggleButton lessonToggle;
    private boolean isGeneratingQuiz = true; 
    
    // Storage for last generated content for export
    private List<QuizQuestion> lastGeneratedQuestions = new ArrayList<>();
    private String lastLessonText = "";

    private final String QUIZ_PLACEHOLDER = "Describe your specific quiz topic or paste lecture notes here...";
    private final String LESSON_PLACEHOLDER = "Outline the scope and learning objectives for the lesson plan...";
    private final String QUIZ_SIZE_PLACEHOLDER = "5";
    private final String LESSON_SIZE_PLACEHOLDER = "3";

    public TeacherDashboardPanel() {
        aiService = QuizAIService.getInstance();
        studyPlanService = StudyPlanAIService.getInstance();
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE);
        initUI();
    }

    private void initUI() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(AppTheme.NAVY);
        navBar.setPreferredSize(new Dimension(0, 50));
        navBar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel logoLabel = new JLabel(" EduGenius AI Workspace");
        logoLabel.setFont(AppTheme.FONT_H2);
        logoLabel.setForeground(AppTheme.WHITE);
        navBar.add(logoLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            AuthService.getInstance().logout();
            NavigationManager.getInstance().clearHistory();
            NavigationManager.getInstance().navigateTo("WELCOME");
        });
        navBar.add(logoutBtn, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(AppTheme.FONT_BODY);
        tabbedPane.addTab(" AI Generator", createGeneratorPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createGeneratorPanel() {
        JPanel workspacePanel = new JPanel(new GridBagLayout());
        workspacePanel.setBackground(AppTheme.SURFACE);
        workspacePanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        JPanel switcherPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        switcherPanel.setOpaque(false);
        quizToggle = new JToggleButton(" Quiz Generator", true);
        lessonToggle = new JToggleButton(" Lesson Planner", false);
        ButtonGroup bg = new ButtonGroup(); bg.add(quizToggle); bg.add(lessonToggle);

        quizToggle.addActionListener(e -> { isGeneratingQuiz = true; toggleModeUI(); });
        lessonToggle.addActionListener(e -> { isGeneratingQuiz = false; toggleModeUI(); });
        switcherPanel.add(quizToggle); switcherPanel.add(lessonToggle);

        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 12, 0);
        workspacePanel.add(switcherPanel, gbc);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints cGbc = new GridBagConstraints();
        cGbc.gridx = 0; cGbc.fill = GridBagConstraints.HORIZONTAL; cGbc.weightx = 1.0;

        promptArea = new JTextArea(3, 40);
        promptArea.setFont(AppTheme.FONT_BODY);
        promptArea.setLineWrap(true); promptArea.setWrapStyleWord(true);
        promptArea.setText(QUIZ_PLACEHOLDER);
        promptArea.setForeground(Color.GRAY);
        promptArea.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (promptArea.getText().equals(QUIZ_PLACEHOLDER) || promptArea.getText().equals(LESSON_PLACEHOLDER)) {
                    promptArea.setText(""); promptArea.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (promptArea.getText().trim().isEmpty()) {
                    promptArea.setText(isGeneratingQuiz ? QUIZ_PLACEHOLDER : LESSON_PLACEHOLDER);
                    promptArea.setForeground(Color.GRAY);
                }
            }
        });

        cGbc.gridy = 0; cGbc.insets = new Insets(0, 0, 8, 0);
        card.add(new JScrollPane(promptArea), cGbc);

        JPanel settingsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        settingsRow.setOpaque(false);
        difficultyCombo = new JComboBox<>(new String[]{"EASY", "MEDIUM", "HARD"});
        contentTypeCombo = new JComboBox<>();
        updateFormatTypes();
        
        sizeTextField = new JTextField(QUIZ_SIZE_PLACEHOLDER);
        sizeLabel = new JLabel("Question Count:");

        JPanel p3 = new JPanel(new BorderLayout()); p3.setOpaque(false);
        p3.add(sizeLabel, BorderLayout.NORTH); p3.add(sizeTextField, BorderLayout.CENTER);
        settingsRow.add(createFieldWrapper("Difficulty Level:", difficultyCombo));
        settingsRow.add(createFieldWrapper("Question/Plan Type:", contentTypeCombo));
        settingsRow.add(p3);

        cGbc.gridy = 1; card.add(settingsRow, cGbc);

        generateButton = new JButton(" Generate Content via AI Engine");
        generateButton.setFont(AppTheme.FONT_BODY_BOLD);
        generateButton.setBackground(AppTheme.TEAL);
        generateButton.setForeground(AppTheme.WHITE);
        generateButton.addActionListener(e -> runGenerationPipeline());

        cGbc.gridy = 2; cGbc.insets = new Insets(12, 0, 0, 0);
        card.add(generateButton, cGbc);

        gbc.gridy = 1; workspacePanel.add(card, gbc);

        generatedQuestionsPanel = new JPanel();
        generatedQuestionsPanel.setLayout(new BoxLayout(generatedQuestionsPanel, BoxLayout.Y_AXIS));
        generatedQuestionsPanel.setBackground(AppTheme.SURFACE);
        JScrollPane scroll = new JScrollPane(generatedQuestionsPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        gbc.gridy = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        workspacePanel.add(scroll, gbc);

        return workspacePanel;
    }

    private void toggleModeUI() {
        generatedQuestionsPanel.removeAll();
        promptArea.setText(isGeneratingQuiz ? QUIZ_PLACEHOLDER : LESSON_PLACEHOLDER);
        promptArea.setForeground(Color.GRAY);
        sizeLabel.setText(isGeneratingQuiz ? "Question Count:" : "Target Weeks:");
        sizeTextField.setText(isGeneratingQuiz ? QUIZ_SIZE_PLACEHOLDER : LESSON_SIZE_PLACEHOLDER);
        updateFormatTypes();
    }

    private void updateFormatTypes() {
        if (contentTypeCombo == null) return;
        contentTypeCombo.removeAllItems();
        if (isGeneratingQuiz) {
            contentTypeCombo.addItem("Multiple Choice (MCQ)");
            contentTypeCombo.addItem("True / False");
            contentTypeCombo.addItem("Short Answers");
        } else {
            contentTypeCombo.addItem("Day-by-Day Lesson Track");
            contentTypeCombo.addItem("Weekly Syllabus Plan Outline");
            contentTypeCombo.addItem("Lecture Breakdown Blueprint");
        }
    }

    private JPanel createFieldWrapper(String title, JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 2)); wrapper.setOpaque(false);
        JLabel lbl = new JLabel(title); lbl.setFont(AppTheme.FONT_SMALL);
        wrapper.add(lbl, BorderLayout.NORTH); wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private void runGenerationPipeline() {
        String input = promptArea.getText().trim();
        if (input.isEmpty() || input.equals(QUIZ_PLACEHOLDER) || input.equals(LESSON_PLACEHOLDER)) return;

        generateButton.setText(" Building AI Material Content Elements...");
        generateButton.setEnabled(false);

        if (isGeneratingQuiz) {
            int total = 5;
            try { total = Integer.parseInt(sizeTextField.getText().trim()); } catch(Exception e){}
            aiService.generateQuiz(input + " [Format Requirement Type: " + contentTypeCombo.getSelectedItem() + "]", 
                (String)difficultyCombo.getSelectedItem(), total,
                questions -> { displayQuiz(questions); resetGenButton(); },
                err -> resetGenButton()
            );
        } else {
            studyPlanService.generateStudyPlan(input + " [Type: " + contentTypeCombo.getSelectedItem() + ", Duration: " + sizeTextField.getText() + "]",
                "Educational Course",
                lessonText -> { displayLesson(lessonText); resetGenButton(); },
                err -> { JOptionPane.showMessageDialog(this, "Error generating lesson: " + err); resetGenButton(); }
            );
        }
    }

    private void resetGenButton() { generateButton.setText(" Generate Content via AI Engine"); generateButton.setEnabled(true); }

    private void displayQuiz(List<QuizQuestion> questions) {
        lastGeneratedQuestions = new ArrayList<>(questions);
        generatedQuestionsPanel.removeAll();
        JPanel container = new JPanel(); container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(AppTheme.SURFACE);

        for (QuizQuestion q : questions) {
            container.add(new SafeReflectionCard(q));
            container.add(Box.createVerticalStrut(10));
        }
        generatedQuestionsPanel.add(container);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        actionRow.setBackground(AppTheme.SURFACE);
        
        JButton saveBtn = new JButton("Save Quiz");
        saveBtn.addActionListener(e -> {
            savedQuizzesLibrary.add(new ArrayList<>(questions));
            refreshLibraryUI();
            JOptionPane.showMessageDialog(this, "Quiz Cataloged into Library Locker Successfully!");
        });

        JButton pdfExportBtn = new JButton(" Export to PDF");
        pdfExportBtn.setBackground(AppTheme.NAVY);
        pdfExportBtn.setForeground(AppTheme.WHITE);
        pdfExportBtn.addActionListener(e -> saveQuizAsText(lastGeneratedQuestions));

        //actionRow.add(saveBtn);
        actionRow.add(pdfExportBtn);
        generatedQuestionsPanel.add(actionRow);
        generatedQuestionsPanel.revalidate(); generatedQuestionsPanel.repaint();
    }

    private void displayLesson(String body) {
        lastLessonText = body;
        generatedQuestionsPanel.removeAll();
        
        JTextArea txt = new JTextArea(body);
        txt.setFont(AppTheme.FONT_BODY);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBackground(AppTheme.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 225), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        int availableWidth = Math.max(400, generatedQuestionsPanel.getWidth() - 32);
        txt.setSize(new Dimension(availableWidth, Short.MAX_VALUE));
        int preferredHeight = txt.getPreferredSize().height;
        txt.setPreferredSize(new Dimension(availableWidth, preferredHeight));

        generatedQuestionsPanel.add(txt);
        generatedQuestionsPanel.add(Box.createVerticalStrut(10));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        actionRow.setBackground(AppTheme.SURFACE);
        JButton pdfExportBtn = new JButton(" Export to PDF");
        pdfExportBtn.setBackground(AppTheme.NAVY);
        pdfExportBtn.setForeground(AppTheme.WHITE);
        pdfExportBtn.addActionListener(e -> saveLessonAsText(lastLessonText));
        actionRow.add(pdfExportBtn);
        generatedQuestionsPanel.add(actionRow);
        generatedQuestionsPanel.revalidate();
        generatedQuestionsPanel.repaint();
    }

    private void refreshLibraryUI() {
        if (libraryGridPanel == null) return;
        libraryGridPanel.removeAll();

        for (int i = 0; i < savedQuizzesLibrary.size(); i++) {
            List<QuizQuestion> quiz = savedQuizzesLibrary.get(i);
            JPanel item = new JPanel(new BorderLayout()); item.setBorder(BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER));
            item.add(new JLabel(" Historical AI Quiz #" + (i + 1) + " (" + quiz.size() + " Questions Loaded)"), BorderLayout.WEST);
            JButton reloadBtn = new JButton("Reload Layout View");
            reloadBtn.addActionListener(e -> { displayQuiz(quiz); tabbedPane.setSelectedIndex(0); });
            item.add(reloadBtn, BorderLayout.EAST); libraryGridPanel.add(item);
        }
        for (int j = 0; j < savedLessonsLibrary.size(); j++) {
            String lesson = savedLessonsLibrary.get(j);
            JPanel item = new JPanel(new BorderLayout()); item.setBorder(BorderFactory.createMatteBorder(0,0,1,0,AppTheme.BORDER));
            item.add(new JLabel(" Historical AI Lesson Blueprint #" + (j + 1)), BorderLayout.WEST);
            JButton reloadBtn = new JButton("Reload Text View");
            reloadBtn.addActionListener(e -> { displayLesson(lesson); tabbedPane.setSelectedIndex(0); });
            item.add(reloadBtn, BorderLayout.EAST); libraryGridPanel.add(item);
        }
        libraryGridPanel.revalidate(); libraryGridPanel.repaint();
    }

    private JPanel createContentLibraryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout()); mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        libraryGridPanel = new JPanel(); libraryGridPanel.setLayout(new BoxLayout(libraryGridPanel, BoxLayout.Y_AXIS));
        refreshLibraryUI();
        mainPanel.add(new JScrollPane(libraryGridPanel), BorderLayout.CENTER);
        return mainPanel;
    }

    private void saveQuizAsText(List<QuizQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quiz content to save.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("quiz.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                for (int i = 0; i < questions.size(); i++) {
                    QuizQuestion q = questions.get(i);
                    pw.println("Question " + (i + 1) + ": " + q.getQuestionText());
                    pw.println();
                    
                    // Extract and write options
                    List<String> options = extractOptionsFromQuestion(q);
                    if (!options.isEmpty()) {
                        char letter = 'A';
                        for (String option : options) {
                            if (option != null && !option.trim().isEmpty()) {
                                pw.println(letter + ") " + option);
                                letter++;
                            }
                        }
                        pw.println();
                    }
                    
                    pw.println("Correct Answer: " + (q.getCorrectOption() != null ? q.getCorrectOption() : "N/A"));
                    pw.println();
                    pw.println("---\n");
                }
                JOptionPane.showMessageDialog(this, "Quiz saved successfully to: " + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage());
            }
        }
    }
    
    private List<String> extractOptionsFromQuestion(QuizQuestion question) {
        List<String> options = new ArrayList<>();
        
        try {
            // Scan via Methods
            for (Method m : question.getClass().getMethods()) {
                m.setAccessible(true);
                String name = m.getName().toLowerCase();
                
                if (name.contains("option") || name.equals("getchoices") || name.contains("getanswers")) {
                    Object res = m.invoke(question);
                    if (res instanceof String[]) {
                        Collections.addAll(options, (String[]) res);
                    } else if (res instanceof List) {
                        for (Object obj : (List<?>) res) {
                            if (obj != null) options.add(obj.toString());
                        }
                    } else if (res instanceof String && name.matches(".*[a-d1-4]$")) {
                        options.add((String) res);
                    }
                }
            }
            
            // Fallback: Scan via Fields
            if (options.isEmpty()) {
                for (Field f : question.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    String name = f.getName().toLowerCase();
                    
                    if (name.contains("option") || name.contains("choice")) {
                        Object val = f.get(question);
                        if (val instanceof String[]) {
                            for (String s : (String[]) val) {
                                if (s != null && !options.contains(s)) options.add(s);
                            }
                        } else if (val instanceof List) {
                            for (Object obj : (List<?>) val) {
                                if (obj != null && !options.contains(obj.toString())) options.add(obj.toString());
                            }
                        } else if (val instanceof String && (name.matches(".*[a-d1-4]$") || name.contains("one") || name.contains("two"))) {
                            String optStr = (String) val;
                            if (!options.contains(optStr)) options.add(optStr);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            // Failover protection
        }
        
        return options;
    }

    private void saveLessonAsText(String lessonText) {
        if (lessonText == null || lessonText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No lesson content to save.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("lesson-plan.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                pw.print(lessonText);
                JOptionPane.showMessageDialog(this, "Lesson plan saved successfully to: " + fc.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage());
            }
        }
    }

    // --- COMPILER-SAFE CARD RENDERER WITH PROPER ALIGNMENT ---
    private class SafeReflectionCard extends JPanel {
        public SafeReflectionCard(QuizQuestion question) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 220, 225), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
            ));

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Question label with proper alignment
            JLabel qLabel = new JLabel("<html><body style='width: 600px;'><b>Question: </b>" + question.getQuestionText() + "</body></html>");
            qLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(qLabel);
            contentPanel.add(Box.createVerticalStrut(8));

            List<String> collectedOptions = new ArrayList<>();
            String answer = "";
            String explanation = "";

            try {
                // 1. Scan via Methods - prioritize proper getters
                for (Method m : question.getClass().getMethods()) {
                    m.setAccessible(true);
                    String name = m.getName().toLowerCase();
                    
                    if (name.contains("option") || name.equals("getchoices") || name.contains("getanswers")) {
                        Object res = m.invoke(question);
                        if (res instanceof String[]) {
                            Collections.addAll(collectedOptions, (String[]) res);
                        } else if (res instanceof List) {
                            for (Object obj : (List<?>) res) { if (obj != null) collectedOptions.add(obj.toString()); }
                        } else if (res instanceof String && name.matches(".*[a-d1-4]$")) {
                            collectedOptions.add((String) res);
                        }
                    } else if ((name.equals("getcorrectoption") || name.equals("getanswer")) && answer.isEmpty()) {
                        Object res = m.invoke(question);
                        if (res != null) answer = res.toString();
                    } else if (name.contains("explanation") || name.contains("desc")) {
                        Object res = m.invoke(question);
                        if (res != null) explanation = res.toString();
                    }
                }

                // 2. Fallback Scan directly via Raw Fields
                for (Field f : question.getClass().getDeclaredFields()) {
                    f.setAccessible(true);
                    String name = f.getName().toLowerCase();
                    
                    if (name.contains("option") || name.contains("choice")) {
                        Object val = f.get(question);
                        if (val instanceof String[]) {
                            for (String s : (String[]) val) { if (s != null && !collectedOptions.contains(s)) collectedOptions.add(s); }
                        } else if (val instanceof List) {
                            for (Object obj : (List<?>) val) {
                                if (obj != null && !collectedOptions.contains(obj.toString())) collectedOptions.add(obj.toString());
                            }
                        } else if (val instanceof String && (name.matches(".*[a-d1-4]$") || name.contains("one") || name.contains("two"))) {
                            String optStr = (String) val;
                            if (!collectedOptions.contains(optStr)) collectedOptions.add(optStr);
                        }
                    }
                    
                    if (answer.isEmpty() && (name.contains("answer") || name.contains("correct"))) {
                        Object val = f.get(question);
                        if (val != null) answer = val.toString();
                    }
                    if (explanation.isEmpty() && name.contains("explanation")) {
                        Object val = f.get(question);
                        if (val != null) explanation = val.toString();
                    }
                }
            } catch (Exception ex) {
                // Failover protection block
            }

            // Options rendering with proper alignment
            if (!collectedOptions.isEmpty()) {
                char letter = 'A';
                for (String text : collectedOptions) {
                    if (text != null && !text.trim().isEmpty()) {
                        JLabel optLabel = new JLabel("<html><body style='width: 550px;'><b>" + letter + ":</b> " + text + "</body></html>");
                        optLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        optLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        contentPanel.add(optLabel);
                        contentPanel.add(Box.createVerticalStrut(5));
                        letter++;
                    }
                }
                contentPanel.add(Box.createVerticalStrut(4));
            }

            // Correct answer with proper alignment
            if (!answer.isEmpty()) {
                JLabel ansLabel = new JLabel("<html><body style='width: 550px; color: #155724; background-color: #D4EDDA; padding: 6px; border-radius: 4px;'><b>✅ Correct Answer:</b> " + answer + "</body></html>");
                ansLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                ansLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                ansLabel.setOpaque(true);
                contentPanel.add(ansLabel);
                contentPanel.add(Box.createVerticalStrut(6));
            }

            // Explanation with proper alignment
            if (!explanation.isEmpty()) {
                JLabel expLabel = new JLabel("<html><body style='width: 550px; color: #1E3A8A; background-color: #F0F9FF; padding: 6px; border-radius: 4px;'><b>💡 Explanation:</b> " + explanation + "</body></html>");
                expLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                expLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                expLabel.setOpaque(true);
                contentPanel.add(expLabel);
            }

            add(contentPanel, BorderLayout.CENTER);
        }
    }
}