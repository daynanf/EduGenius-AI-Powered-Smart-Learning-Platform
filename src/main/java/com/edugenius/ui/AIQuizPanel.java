// FILE: src/main/java/com/edugenius/ui/AIQuizPanel.java
package com.edugenius.ui;

import com.edugenius.ai.AIService;
import com.edugenius.model.AIQuestion;
import com.edugenius.model.AIReviewResponse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AIQuizPanel extends JPanel {
    private final int courseId;
    private final String courseName;
    
    private CardLayout layout;
    private JPanel cardContainer;
    
    private List<AIQuestion> activeQuizQuestions;
    private int currentQuestionIndex = 0;

    private final Color COLOR_NAVY_BASE = new Color(13, 27, 42);
    private final Color COLOR_NAVY_MEDIUM = new Color(26, 46, 69);
    private final Color COLOR_TEAL_PRIMARY = new Color(0, 201, 167);

    public AIQuizPanel(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
        
        layout = new CardLayout();
        cardContainer = new JPanel(layout);
        cardContainer.setBackground(COLOR_NAVY_BASE);
        
        setLayout(new BorderLayout());
        add(cardContainer, BorderLayout.CENTER);

        // Boot system into asynchronous initialization worker state
        showLoadingStateView();
        fetchQuizQuestionsFromAI();
    }

    private void showLoadingStateView() {
        JPanel loadingPanel = new JPanel(new GridBagLayout());
        loadingPanel.setBackground(COLOR_NAVY_BASE);
        
        JLabel lblMsg = new JLabel("🤖 LLaMA 3.3 is compiling your customized assessment...");
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMsg.setForeground(COLOR_TEAL_PRIMARY);
        
        loadingPanel.add(lblMsg);
        cardContainer.add(loadingPanel, "LOADING");
        layout.show(cardContainer, "LOADING");
    }

    // --- ASYNCHRONOUS SWINGWORKER PROCESSING NODE ---
    private void fetchQuizQuestionsFromAI() {
        SwingWorker<List<AIQuestion>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AIQuestion> doInBackground() throws Exception {
                // Request dynamic quiz extraction from Groq API engine 
                return AIService.generateQuiz(courseName, "Intermediate");
            }

            @Override
            protected void done() {
                try {
                    activeQuizQuestions = get();
                    buildQuizQuestionCards();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AIQuizPanel.this, 
                        "AI Core Timeout or Key Configuration Error:\n" + e.getMessage(), 
                        "Transmission Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void buildQuizQuestionCards() {
        if (activeQuizQuestions == null || activeQuizQuestions.isEmpty()) return;

        for (int i = 0; i < activeQuizQuestions.size(); i++) {
            JPanel qCard = createQuestionUIInstance(activeQuizQuestions.get(i), i + 1, activeQuizQuestions.size());
            cardContainer.add(qCard, "QUESTION_" + i);
        }
        
        layout.show(cardContainer, "QUESTION_0");
    }

    private JPanel createQuestionUIInstance(AIQuestion question, int iteration, int total) {
        JPanel pane = new JPanel(new BorderLayout());
        pane.setBackground(COLOR_NAVY_BASE);
        pane.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Question Progress Tracker Header
        JLabel header = new JLabel("Question " + iteration + " of " + total);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(COLOR_TEAL_PRIMARY);
        pane.add(header, BorderLayout.NORTH);

        // Body Content Grouping Container
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.add(Box.createVerticalStrut(15));

        JTextArea qText = new JTextArea(question.getQuestionText());
        qText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        qText.setForeground(Color.WHITE);
        qText.setWrapStyleWord(true);
        qText.setLineWrap(true);
        qText.setEditable(false);
        qText.setOpaque(false);
        qText.setMaximumSize(new Dimension(800, 100));
        body.add(qText);
        body.add(Box.createVerticalStrut(25));

        // Options Radio Selection Elements Setup
        ButtonGroup group = new ButtonGroup();
        String[] prefixes = {"A", "B", "C", "D"};
        List<String> rawOptions = question.getOptions();

        for (int i = 0; i < 4; i++) {
            if (i >= rawOptions.size()) break;
            
            final String currentPrefix = prefixes[i];
            JRadioButton radio = new JRadioButton(currentPrefix + ". " + rawOptions.get(i));
            radio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            radio.setForeground(Color.WHITE);
            radio.setOpaque(false);
            radio.setMaximumSize(new Dimension(800, 40));
            
            group.add(radio);
            body.add(radio);
            body.add(Box.createVerticalStrut(10));

            // Real-Time Evaluation Listener Binder
            radio.addActionListener(e -> executeAsyncFeedbackEngine(question, currentPrefix));
        }

        pane.add(body, BorderLayout.CENTER);
        return pane;
    }

    // --- SECONDARY ASYNCHRONOUS ENGINE: DYNAMIC SUBMISSION REVIEWS ---
    private void executeAsyncFeedbackEngine(AIQuestion targetQuestion, String selectedAnswerIndex) {
        // Overlay a quick glass blocking panel to restrict double input selections
        GlassPaneLoader.showStatusDots(this, "Analyzing response vectors...");

        SwingWorker<AIReviewResponse, Void> reviewWorker = new SwingWorker<>() {
            @Override
            protected AIReviewResponse doInBackground() throws Exception {
                return AIService.reviewAnswer(
                    targetQuestion.getQuestionText(), 
                    selectedAnswerIndex, 
                    targetQuestion.getCorrectOption()
                );
            }

            @Override
            protected void done() {
                GlassPaneLoader.hideStatusDots();
                try {
                    AIReviewResponse feedback = get();
                    
                    String alertHeading = feedback.isCorrect() ? "🎯 MAGNIFICENT WORK!" : "❌ COGNITIVE GAP ENCOUNTERED";
                    String outMsg = String.format("%s\n\nExplanation:\n%s\n\nStudy Correction Tip:\n%s", 
                        alertHeading, feedback.getExplanation(), feedback.getCorrectionTip());

                    JOptionPane.showMessageDialog(AIQuizPanel.this, outMsg, "AI Context Evaluator", 
                        feedback.isCorrect() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

                    // Sequence dynamic step progression parameters
                    currentQuestionIndex++;
                    if (currentQuestionIndex < activeQuizQuestions.size()) {
                        layout.show(cardContainer, "QUESTION_" + currentQuestionIndex);
                    } else {
                        JOptionPane.showMessageDialog(AIQuizPanel.this, 
                            "Assessment Loop Terminated! Performance analytics cached inside the directory logs.", 
                            "Quiz Complete", JOptionPane.INFORMATION_MESSAGE);
                        
                        // Clear views and drop back out to student catalogue directory
                        Container parent = AIQuizPanel.this.getParent();
                        if (parent instanceof JPanel) {
                            ((CardLayout) parent.getLayout()).show(parent, "CATALOG_HOME");
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        reviewWorker.execute();
    }

    // Secondary UI Status Modal Wrapper
    private static class GlassPaneLoader {
        private static JDialog modal;
        public static void showStatusDots(Component parent, String message) {
            Window win = SwingUtilities.getWindowAncestor(parent);
            modal = new JDialog(win, Dialog.ModalityType.APPLICATION_MODAL);
            modal.setUndecorated(true);
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(new Color(26, 46, 69));
            p.setBorder(BorderFactory.createLineBorder(new Color(0, 201, 167), 1));
            JLabel lbl = new JLabel(message);
            lbl.setForeground(Color.WHITE);
            p.add(lbl);
            modal.add(p);
            modal.setSize(280, 60);
            modal.setLocationRelativeTo(win);
            SwingUtilities.invokeLater(() -> modal.setVisible(true));
        }
        public static void hideStatusDots() {
            if (modal != null) { modal.dispose(); }
        }
    }
}