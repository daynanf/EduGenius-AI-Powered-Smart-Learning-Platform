// FILE: src/main/java/com/edugenius/views/student/QuizPanel.java
package com.edugenius.views.student;

import com.edugenius.config.AppTheme;
import com.edugenius.models.QuizQuestion;
import com.edugenius.ai.QuizAIService;
import com.edugenius.services.AuthService;
import com.edugenius.services.QuizService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.ParameterReceiver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Quiz Panel - Student Quiz Taking Interface
 * Features: Timer, Question navigation, AI explanations, Score calculation
 */
public class QuizPanel extends JPanel implements ParameterReceiver {

    private QuizAIService aiService;
    private QuizService quizService;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int currentCourseId;
    private String currentCourseName;
    private String currentTopic;
    private Timer timer;
    private int timeRemaining = 60; // seconds per question
    private JLabel timerLabel;
    private JLabel questionCounterLabel;
    private JProgressBar progressBar;
    private JPanel questionPanel;
    private JButton nextButton;
    private JButton prevButton;
    private JButton submitButton;
    private ButtonGroup optionsGroup;
    private JRadioButton[] optionButtons;
    private JLabel questionTextLabel;
    private JPanel explanationPanel;
    private JLabel explanationLabel;
    private boolean quizCompleted = false;
    private int score = 0;

    // Quiz setup fields
    private JTextField topicField;
    private JComboBox<String> difficultyCombo;
    private JComboBox<Integer> questionCountCombo;
    private JPanel setupPanel;
    private JPanel quizPanel;
    private JPanel resultPanel;
    private CardLayout cardLayout;

    public QuizPanel() {
        aiService = QuizAIService.getInstance();
        quizService = new QuizService();
        setLayout(new CardLayout());
        setBackground(AppTheme.SURFACE);
        initSetupUI();
    }

    @Override
    public void receiveParameters(Map<String, Object> params) {
        this.currentCourseId = (int) params.get("courseId");
        this.currentCourseName = (String) params.get("courseName");
        // Show setup panel when coming from course
        showSetup();
    }

    private void initSetupUI() {
        setupPanel = new JPanel(new GridBagLayout());
        setupPanel.setBackground(AppTheme.SURFACE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Main setup card (Rounded using Graphics2D)
        JPanel card = new JPanel(new GridBagLayout()) {
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
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
        card.setPreferredSize(new Dimension(500, 400));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(0, 0, 16, 0);

        // Title
        JLabel titleLabel = new JLabel("🎯 AI Quiz Generator");
        titleLabel.setFont(AppTheme.FONT_H1);
        titleLabel.setForeground(AppTheme.TEAL);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardGbc.gridy = 0;
        card.add(titleLabel, cardGbc);

        // Course info
        JLabel courseLabel = new JLabel("Course: " + currentCourseName);
        courseLabel.setFont(AppTheme.FONT_BODY);
        courseLabel.setForeground(AppTheme.MUTED);
        courseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardGbc.gridy = 1;
        card.add(courseLabel, cardGbc);

        // Topic field
        JLabel topicLabel = new JLabel("Enter Topic");
        topicLabel.setFont(AppTheme.FONT_LABEL);
        topicLabel.setForeground(AppTheme.MUTED);
        cardGbc.gridy = 2;
        card.add(topicLabel, cardGbc);

        // Topic field rounded using Graphics2D
        topicField = new JTextField() {
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
        topicField.setOpaque(false);
        topicField.setFont(AppTheme.FONT_BODY);
        topicField.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        topicField.setToolTipText("e.g., Binary Trees, Recursion, Sorting Algorithms");
        cardGbc.gridy = 3;
        card.add(topicField, cardGbc);

        // Difficulty selector
        JLabel diffLabel = new JLabel("Difficulty Level");
        diffLabel.setFont(AppTheme.FONT_LABEL);
        diffLabel.setForeground(AppTheme.MUTED);
        cardGbc.gridy = 4;
        card.add(diffLabel, cardGbc);

        difficultyCombo = new JComboBox<>(new String[] { "EASY", "MEDIUM", "HARD" });
        difficultyCombo.setFont(AppTheme.FONT_BODY);
        cardGbc.gridy = 5;
        card.add(difficultyCombo, cardGbc);

        // Question count
        JLabel countLabel = new JLabel("Number of Questions");
        countLabel.setFont(AppTheme.FONT_LABEL);
        countLabel.setForeground(AppTheme.MUTED);
        cardGbc.gridy = 6;
        card.add(countLabel, cardGbc);

        questionCountCombo = new JComboBox<>(new Integer[] { 5, 10, 15 });
        questionCountCombo.setFont(AppTheme.FONT_BODY);
        cardGbc.gridy = 7;
        card.add(questionCountCombo, cardGbc);

        // Generate button rounded using Graphics2D
        JButton generateButton = new JButton("🚀 Generate Quiz with AI") {
            private boolean isHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? AppTheme.TEAL_DARK : AppTheme.TEAL);
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
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.addActionListener(e -> generateQuiz());
        cardGbc.gridy = 8;
        cardGbc.insets = new Insets(20, 0, 0, 0);
        card.add(generateButton, cardGbc);

        // Back button
        JButton backButton = new JButton("← Back to Dashboard");
        backButton.setFont(AppTheme.FONT_BODY);
        backButton.setForeground(AppTheme.MUTED);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> NavigationManager.getInstance().navigateTo("AI_LEARNING"));
        cardGbc.gridy = 9;
        cardGbc.insets = new Insets(10, 0, 0, 0);
        card.add(backButton, cardGbc);

        // Center the card
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        setupPanel.add(card, gbc);

        add(setupPanel, "SETUP");

        // Initialize quiz panel
        initQuizUI();
        initResultUI();

        cardLayout = (CardLayout) getLayout();
    }

    private void initQuizUI() {
        quizPanel = new JPanel(new BorderLayout());
        quizPanel.setBackground(AppTheme.SURFACE);

        // Top bar with timer and progress
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppTheme.NAVY);
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        questionCounterLabel = new JLabel("Question 1 / ?");
        questionCounterLabel.setFont(AppTheme.FONT_BODY_BOLD);
        questionCounterLabel.setForeground(AppTheme.WHITE);
        topBar.add(questionCounterLabel, BorderLayout.WEST);

        timerLabel = new JLabel("⏱️ --:--");
        timerLabel.setFont(AppTheme.FONT_H3);
        timerLabel.setForeground(AppTheme.WHITE);
        topBar.add(timerLabel, BorderLayout.EAST);

        quizPanel.add(topBar, BorderLayout.NORTH);

        // Center - Question panel (rounded using Graphics2D)
        questionPanel = new JPanel(new GridBagLayout()) {
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
        questionPanel.setOpaque(false);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));

        // Wrap center in a padded container so the rounded card has room to breathe
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        centerWrapper.add(questionPanel, BorderLayout.CENTER);
        quizPanel.add(centerWrapper, BorderLayout.CENTER);

        // Bottom - Navigation buttons
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 16));
        bottomBar.setBackground(AppTheme.SURFACE);

        prevButton = new JButton("← Previous") {
            private boolean isHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? new Color(245, 247, 250) : AppTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

                g2.setFont(getFont());
                g2.setColor(AppTheme.INK);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        prevButton.setFont(AppTheme.FONT_BODY_BOLD);
        prevButton.setPreferredSize(new Dimension(120, 36));
        prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevButton.addActionListener(e -> previousQuestion());

        nextButton = new JButton("Next →") {
            private boolean isHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? AppTheme.TEAL_DARK : AppTheme.TEAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

                g2.setFont(getFont());
                g2.setColor(AppTheme.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        nextButton.setFont(AppTheme.FONT_BODY_BOLD);
        nextButton.setPreferredSize(new Dimension(120, 36));
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e -> nextQuestion());

        submitButton = new JButton("✓ Submit Quiz") {
            private boolean isHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? AppTheme.GREEN.darker() : AppTheme.GREEN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

                g2.setFont(getFont());
                g2.setColor(AppTheme.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        submitButton.setFont(AppTheme.FONT_BODY_BOLD);
        submitButton.setPreferredSize(new Dimension(140, 36));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> submitQuiz());
        submitButton.setVisible(false);

        bottomBar.add(prevButton);
        bottomBar.add(nextButton);
        bottomBar.add(submitButton);

        quizPanel.add(bottomBar, BorderLayout.SOUTH);

        add(quizPanel, "QUIZ");
    }

    private void initResultUI() {
        resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setBackground(AppTheme.SURFACE);

        JPanel card = new JPanel(new GridBagLayout()) {
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
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(48, 48, 48, 48));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 16, 0);

        JLabel resultTitle = new JLabel("📊 Quiz Results");
        resultTitle.setFont(AppTheme.FONT_H1);
        resultTitle.setForeground(AppTheme.TEAL);
        gbc.gridy = 0;
        card.add(resultTitle, gbc);

        JLabel scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        gbc.gridy = 1;
        card.add(scoreLabel, gbc);

        JButton doneButton = new JButton("🏠 Back to Dashboard") {
            private boolean isHovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isHovered ? AppTheme.TEAL_DARK : AppTheme.TEAL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);

                g2.setFont(getFont());
                g2.setColor(AppTheme.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        doneButton.setFont(AppTheme.FONT_BODY_BOLD);
        doneButton.setPreferredSize(new Dimension(200, 44));
        doneButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        doneButton.addActionListener(e -> NavigationManager.getInstance().navigateTo("STUDENT_DASHBOARD"));
        gbc.gridy = 2;
        card.add(doneButton, gbc);

        gbc.gridy = 0;
        resultPanel.add(card, gbc);

        add(resultPanel, "RESULT");
    }

    private void generateQuiz() {
        String topic = topicField.getText().trim();
        if (topic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a topic!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentTopic = topic;
        String difficulty = (String) difficultyCombo.getSelectedItem();
        int questionCount = (int) questionCountCombo.getSelectedItem();

        // Show loading
        JDialog loadingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generating Quiz", true);
        loadingDialog.setLayout(new BorderLayout());
        JLabel loadingLabel = new JLabel("🧠 AI is crafting your quiz...", SwingConstants.CENTER);
        loadingLabel.setFont(AppTheme.FONT_H2);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);
        loadingDialog.setSize(400, 200);
        loadingDialog.setLocationRelativeTo(this);

        // Start generation in background
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                aiService.generateQuiz(topic, difficulty, questionCount,
                        questionsList -> {
                            loadingDialog.dispose();
                            startQuiz(questionsList);
                        },
                        error -> {
                            loadingDialog.dispose();
                            JOptionPane.showMessageDialog(QuizPanel.this,
                                    "Error generating quiz: " + error, "Error", JOptionPane.ERROR_MESSAGE);
                        });
                return null;
            }
        };
        worker.execute();

        loadingDialog.setVisible(true);
    }

    private void startQuiz(List<QuizQuestion> generatedQuestions) {
        this.questions = generatedQuestions;
        this.currentQuestionIndex = 0;
        this.score = 0;
        this.quizCompleted = false;

        // Set timer (30 seconds per question)
        timeRemaining = 30 * questions.size();
        startTimer();

        displayQuestion(0);
        cardLayout.show(this, "QUIZ");
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining <= 0) {
                    timer.cancel();
                    SwingUtilities.invokeLater(() -> submitQuiz());
                } else {
                    timeRemaining--;
                    SwingUtilities.invokeLater(() -> updateTimerDisplay());
                }
            }
        }, 0, 1000);
    }

    private void updateTimerDisplay() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("⏱️ %02d:%02d", minutes, seconds));

        if (timeRemaining < 30) {
            timerLabel.setForeground(AppTheme.CORAL);
        } else if (timeRemaining < 60) {
            timerLabel.setForeground(AppTheme.AMBER);
        } else {
            timerLabel.setForeground(AppTheme.WHITE);
        }
    }

    private void displayQuestion(int index) {
        questionPanel.removeAll();

        QuizQuestion q = questions.get(index);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 20, 0);

        // Question number and difficulty badge
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel qNumLabel = new JLabel("Question " + (index + 1) + " of " + questions.size());
        qNumLabel.setFont(AppTheme.FONT_BODY_BOLD);
        qNumLabel.setForeground(AppTheme.TEAL);
        headerPanel.add(qNumLabel, BorderLayout.WEST);

        JLabel diffBadge = new JLabel(q.getDifficulty());
        diffBadge.setFont(AppTheme.FONT_SMALL);
        diffBadge.setForeground(getDifficultyColor(q.getDifficulty()));
        diffBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getDifficultyColor(q.getDifficulty())),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        headerPanel.add(diffBadge, BorderLayout.EAST);

        gbc.gridy = 0;
        questionPanel.add(headerPanel, gbc);

        // Question text
        questionTextLabel = new JLabel("<html><div style='width:500px;'>" + q.getQuestionText() + "</div></html>");
        questionTextLabel.setFont(AppTheme.FONT_H3);
        questionTextLabel.setForeground(AppTheme.INK);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        questionPanel.add(questionTextLabel, gbc);

        // Options
        optionsGroup = new ButtonGroup();
        optionButtons = new JRadioButton[4];
        String[] options = { q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD() };
        char[] letters = { 'A', 'B', 'C', 'D' };

        for (int j = 0; j < 4; j++) {
            JRadioButton option = new JRadioButton(letters[j] + ". " + options[j]);
            option.setFont(AppTheme.FONT_BODY);
            option.setBackground(AppTheme.WHITE);
            option.setCursor(new Cursor(Cursor.HAND_CURSOR));
            final int x = j;
            option.addActionListener(e -> {
                q.setStudentAnswer(String.valueOf(letters[x]));
                updateNavigationButtons();
                // Show AI explanation after answering
                SwingUtilities.invokeLater(() -> {
                    showAIExplanation(q, currentQuestionIndex);
                });
            });

            optionButtons[j] = option;
            optionsGroup.add(option);

            gbc.gridy = 3 + j;
            gbc.insets = new Insets(0, 20, 10, 0);
            questionPanel.add(option, gbc);
        }

        // Pre-select if answered
        if (q.getStudentAnswer() != null) {
            int answerIndex = q.getStudentAnswer().charAt(0) - 'A';
            if (answerIndex >= 0 && answerIndex < 4) {
                optionButtons[answerIndex].setSelected(true);
            }
        }

        questionCounterLabel.setText("Question " + (index + 1) + " / " + questions.size());

        updateNavigationButtons();

        questionPanel.revalidate();
        questionPanel.repaint();
    }

    private void updateNavigationButtons() {
        prevButton.setEnabled(currentQuestionIndex > 0);

        if (currentQuestionIndex == questions.size() - 1) {
            nextButton.setVisible(false);
            submitButton.setVisible(true);
        } else {
            nextButton.setVisible(true);
            submitButton.setVisible(false);
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        }
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        }
    }

    private void submitQuiz() {
        if (timer != null) {
            timer.cancel();
        }

        // Calculate score
        for (QuizQuestion q : questions) {
            if (q.isCorrect()) {
                score++;
            }
        }

        int percentage = (score * 100) / questions.size();
        String grade = getGrade(percentage);

        // Show results
        cardLayout.show(this, "RESULT");
        JPanel resultCard = (JPanel) resultPanel.getComponent(0);
        JLabel scoreLabel = (JLabel) resultCard.getComponent(1);
        scoreLabel.setText(score + " / " + questions.size() + " (" + percentage + "% - Grade " + grade + ")");

        // Save to database
        saveQuizResults(percentage, grade);
    }

    private void saveQuizResults(int percentage, String grade) {
        try {
            int userId = AuthService.getInstance().getCurrentUser().getUserId();
            int sessionId = quizService.createQuizSession(userId, currentCourseId,
                    currentTopic, (String) difficultyCombo.getSelectedItem(),
                    questions.size(), "MCQ");

            for (QuizQuestion q : questions) {
                quizService.saveQuizAnswer(sessionId, q.getQuestionNumber(),
                        q.getQuestionText(), q.getOptionA(), q.getOptionB(),
                        q.getOptionC(), q.getOptionD(), q.getCorrectOption(),
                        q.getStudentAnswer(), q.isCorrect(), q.getDifficulty());
            }

            quizService.completeQuizSession(sessionId, score, percentage, grade,
                    30 * questions.size() - timeRemaining);
        } catch (Exception e) {
            System.err.println("Error saving quiz: " + e.getMessage());
        }
    }

    private Color getDifficultyColor(String difficulty) {
        switch (difficulty) {
            case "EASY":
                return AppTheme.GREEN;
            case "MEDIUM":
                return AppTheme.AMBER;
            case "HARD":
                return AppTheme.CORAL;
            default:
                return AppTheme.TEAL;
        }
    }

    private String getGrade(int percentage) {
        if (percentage >= 90)
            return "A";
        if (percentage >= 80)
            return "B";
        if (percentage >= 70)
            return "C";
        if (percentage >= 60)
            return "D";
        return "F";
    }

    private void showSetup() {
        cardLayout.show(this, "SETUP");
    }

    // Add this method to show AI explanation after answering
    private void showAIExplanation(QuizQuestion q, int questionIndex) {
        // Create explanation dialog
        JDialog explainDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "AI Explanation", true);
        explainDialog.setLayout(new BorderLayout());
        explainDialog.setSize(500, 300);
        explainDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(AppTheme.WHITE);

        // Header
        JLabel headerLabel = new JLabel(q.isCorrect() ? "✅ CORRECT! Great job!" : "❌ INCORRECT - Let's learn!");
        headerLabel.setFont(AppTheme.FONT_H3);
        headerLabel.setForeground(q.isCorrect() ? AppTheme.GREEN : AppTheme.CORAL);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(headerLabel, BorderLayout.NORTH);

        // Loading label
        JLabel loadingLabel = new JLabel("🤖 AI is generating explanation...");
        loadingLabel.setFont(AppTheme.FONT_BODY);
        loadingLabel.setForeground(AppTheme.MUTED);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(loadingLabel, BorderLayout.CENTER);

        explainDialog.add(contentPanel);

        // Show dialog immediately with loading
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                aiService.getAnswerExplanation(
                        q.getQuestionText(),
                        q.getCorrectAnswerText(),
                        q.getStudentAnswer(),
                        q.isCorrect(),
                        explanation -> {
                            // Update dialog with explanation
                            loadingLabel.setText(
                                    "<html><div style='width:400px; padding:10px;'>" + explanation + "</div></html>");
                            loadingLabel.setFont(AppTheme.FONT_BODY);
                            loadingLabel.setForeground(AppTheme.INK);

                            // Add close button
                            JButton closeButton = new JButton("Continue →");
                            closeButton.setFont(AppTheme.FONT_BODY_BOLD);
                            closeButton.setBackground(AppTheme.TEAL);
                            closeButton.setForeground(AppTheme.WHITE);
                            closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                            closeButton.addActionListener(e -> explainDialog.dispose());

                            JPanel buttonPanel = new JPanel();
                            buttonPanel.setBackground(AppTheme.WHITE);
                            buttonPanel.add(closeButton);
                            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

                            contentPanel.revalidate();
                            contentPanel.repaint();
                        },
                        error -> {
                            loadingLabel.setText("Could not load AI explanation. Click continue to proceed.");
                            loadingLabel.setForeground(AppTheme.CORAL);

                            JButton closeButton = new JButton("Continue →");
                            closeButton.addActionListener(e -> explainDialog.dispose());
                            JPanel buttonPanel = new JPanel();
                            buttonPanel.add(closeButton);
                            contentPanel.add(buttonPanel, BorderLayout.SOUTH);

                            contentPanel.revalidate();
                        });
                return null;
            }
        };
        worker.execute();

        explainDialog.setVisible(true);
    }
}