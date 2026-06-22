// FILE: src/main/java/com/edugenius/views/student/AITutorPanel.java
package com.edugenius.views.student;

import com.edugenius.config.AppTheme;
import com.edugenius.ai.TutorAIService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.ParameterReceiver;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AITutorPanel extends JPanel implements ParameterReceiver {

    private TutorAIService aiService;
    private int currentCourseId;
    private String currentCourseName;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private List<String[]> conversationHistory;
    private JPanel suggestedPromptsPanel;
    private JPanel emptyStatePanel;
    private JPanel mainChatPanel;
    private CardLayout chatCardLayout;

    public AITutorPanel() {
        aiService = TutorAIService.getInstance();
        conversationHistory = new ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE);
        initUI();
    }

    @Override
    public void receiveParameters(Map<String, Object> params) {
        this.currentCourseId = (int) params.get("courseId");
        this.currentCourseName = (String) params.get("courseName");
        updateWelcomeMessage();
    }

    private void initUI() {
        // Top Navigation Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(AppTheme.NAVY);
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JButton backButton = new JButton("← Back");
        backButton.setFont(AppTheme.FONT_BODY);
        backButton.setForeground(AppTheme.TEAL);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> NavigationManager.getInstance().navigateTo("AI_LEARNING"));
        topBar.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(
                " AI Tutor - " + (currentCourseName != null ? currentCourseName : "CS Assistant"));
        titleLabel.setFont(AppTheme.FONT_H2);
        titleLabel.setForeground(AppTheme.WHITE);
        topBar.add(titleLabel, BorderLayout.CENTER);

        JButton clearButton = new JButton("New Chat");
        clearButton.setFont(AppTheme.FONT_BODY);
        clearButton.setForeground(AppTheme.TEAL);
        clearButton.setBorder(BorderFactory.createLineBorder(AppTheme.TEAL));
        clearButton.setBackground(AppTheme.NAVY);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearButton.addActionListener(e -> clearChat());
        topBar.add(clearButton, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Main content with CardLayout for empty state vs chat
        chatCardLayout = new CardLayout();
        JPanel mainContainer = new JPanel(chatCardLayout);
        mainContainer.setBackground(AppTheme.SURFACE);

        // // Empty state panel
        // emptyStatePanel = createEmptyStatePanel();
        // mainContainer.add(emptyStatePanel, "EMPTY");

        // Main chat panel
        mainChatPanel = createChatPanel();
        mainContainer.add(mainChatPanel, "CHAT");

        add(mainContainer, BorderLayout.CENTER);

        // Show empty state initially
        chatCardLayout.show(mainContainer, "EMPTY");
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.SURFACE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel robotEmoji = new JLabel("");
        robotEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        gbc.gridy = 0;
        panel.add(robotEmoji, gbc);

        JLabel welcomeLabel = new JLabel(
                "Hi! I'm your AI Tutor for " + (currentCourseName != null ? currentCourseName : "CS"));
        welcomeLabel.setFont(AppTheme.FONT_H2);
        welcomeLabel.setForeground(AppTheme.INK);
        gbc.gridy = 1;
        panel.add(welcomeLabel, gbc);

        JLabel askLabel = new JLabel("Ask me anything about Computer Science!");
        askLabel.setFont(AppTheme.FONT_BODY);
        askLabel.setForeground(AppTheme.MUTED);
        gbc.gridy = 2;
        panel.add(askLabel, gbc);

        // Suggested prompts grid
        JPanel promptsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        promptsGrid.setBackground(AppTheme.SURFACE);
        promptsGrid.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        String[] prompts = {
                "Explain Binary Search Trees with examples",
                "What is time complexity? Give examples",
                "Show me a LinkedList implementation in Java",
                "Quiz me on sorting algorithms"
        };

        for (String prompt : prompts) {
            JPanel promptCard = createPromptCard(prompt);
            promptsGrid.add(promptCard);
        }

        gbc.gridy = 3;
        panel.add(promptsGrid, gbc);

        return panel;
    }

    private JPanel createPromptCard(String promptText) {
        JPanel card = new JPanel(new BorderLayout()) {
            private boolean isHovered = false;
            {
                setOpaque(false);
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        isHovered = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        isHovered = false;
                        repaint();
                    }

                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        inputField.setText(promptText);
                        sendMessage();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(AppTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);

                g2.setColor(isHovered ? AppTheme.TEAL : AppTheme.BORDER);
                g2.setStroke(new BasicStroke(isHovered ? 2f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.dispose();
            }
        };
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel("");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        card.add(iconLabel, BorderLayout.NORTH);

        JLabel textLabel = new JLabel("<html><div style='width:180px;'>" + promptText + "</div></html>");
        textLabel.setFont(AppTheme.FONT_BODY);
        textLabel.setForeground(AppTheme.INK);
        card.add(textLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.SURFACE);

        // Chat area
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(AppTheme.SURFACE);

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.setBackground(AppTheme.SURFACE);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(chatScrollPane, BorderLayout.CENTER);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(AppTheme.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        inputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        inputField.setOpaque(false);
        inputField.setFont(AppTheme.FONT_BODY);
        inputField.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        inputField.addActionListener(e -> sendMessage());

        sendButton = new JButton("Send →") {
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

                int w = getWidth();
                int h = getHeight();

                g2.setColor(isHovered ? AppTheme.TEAL_DARK : AppTheme.TEAL);
                g2.fillRoundRect(0, 0, w, h, 16, 16);
                g2.setColor(AppTheme.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        sendButton.setFont(AppTheme.FONT_BODY_BOLD);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());

        // Add padding around the send button so it doesn't stretch to the very
        // top/bottom of the input panel
        JPanel sendWrapper = new JPanel(new BorderLayout());
        sendWrapper.setOpaque(false);
        sendWrapper.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        sendWrapper.add(sendButton, BorderLayout.CENTER);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendWrapper, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty())
            return;

        inputField.setText("");
        sendButton.setEnabled(false);

        // Switch to chat view if in empty state
        if (chatPanel.getComponentCount() == 0) {
            chatCardLayout.show((JPanel) getComponent(1), "CHAT");
        }

        // Add user message bubble
        addMessageBubble("user", message);

        // Add typing indicator
        JPanel typingIndicator = createTypingIndicator();
        chatPanel.add(typingIndicator);
        scrollToBottom();

        // Send to AI
        conversationHistory.add(new String[] { "user", message });

        aiService.sendMessage(conversationHistory, message, currentCourseName,
                response -> {
                    // Remove typing indicator
                    chatPanel.remove(typingIndicator);
                    // Add AI response
                    addMessageBubble("assistant", response);
                    conversationHistory.add(new String[] { "assistant", response });
                    sendButton.setEnabled(true);
                    scrollToBottom();
                },
                error -> {
                    chatPanel.remove(typingIndicator);
                    addMessageBubble("assistant", " " + error + "\n\nPlease check your API key or try again.");
                    sendButton.setEnabled(true);
                    scrollToBottom();
                });
    }

    private void addMessageBubble(String role, String content) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBackground(AppTheme.SURFACE);
        bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel bubbleContent = new JPanel();
        bubbleContent.setOpaque(false);

        if (role.equals("user")) {
            bubbleContent.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 8));

            JPanel textCard = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(AppTheme.TEAL);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.dispose();
                }
            };
            textCard.setOpaque(false);
            textCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            JTextArea textArea = new JTextArea(content);
            textArea.setFont(AppTheme.FONT_BODY);
            textArea.setForeground(AppTheme.WHITE);
            textArea.setOpaque(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);

            // Calculate preferred size
            int width = Math.min(400, content.length() * 6);
            textArea.setPreferredSize(new Dimension(width, textArea.getPreferredSize().height));

            textCard.add(textArea, BorderLayout.CENTER);
            bubbleContent.add(textCard);
        } else {
            bubbleContent.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

            JLabel avatarLabel = new JLabel("🤖");
            avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            bubbleContent.add(avatarLabel);

            JPanel textCard = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(AppTheme.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setColor(AppTheme.BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            };
            textCard.setOpaque(false);
            textCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            JTextArea textArea = new JTextArea(content);
            textArea.setFont(AppTheme.FONT_BODY);
            textArea.setForeground(AppTheme.INK);
            textArea.setOpaque(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);

            // Calculate preferred size
            int width = Math.min(500, content.length() * 5);
            textArea.setPreferredSize(new Dimension(width, textArea.getPreferredSize().height));

            textCard.add(textArea, BorderLayout.CENTER);
            bubbleContent.add(textCard);
        }

        bubble.add(bubbleContent, BorderLayout.CENTER);
        chatPanel.add(bubble);
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    private JPanel createTypingIndicator() {
        JPanel indicator = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        indicator.setBackground(AppTheme.SURFACE);
        indicator.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel avatarLabel = new JLabel("🤖");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        indicator.add(avatarLabel);

        JPanel dots = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        dots.setOpaque(false);
        dots.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        for (int i = 0; i < 3; i++) {
            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Dialog", Font.BOLD, 12));
            dot.setForeground(AppTheme.TEAL);
            dots.add(dot);
        }

        indicator.add(dots);

        // Animate dots
        Timer timer = new Timer(500, e -> {
            for (Component comp : dots.getComponents()) {
                JLabel dot = (JLabel) comp;
                if (dot.getText().equals("●")) {
                    dot.setText("○");
                } else {
                    dot.setText("●");
                }
            }
        });
        timer.start();

        // Store timer to stop it later
        indicator.putClientProperty("timer", timer);

        return indicator;
    }

    private void clearChat() {
        chatPanel.removeAll();
        conversationHistory.clear();
        chatPanel.revalidate();
        chatPanel.repaint();
        chatCardLayout.show((JPanel) getComponent(1), "EMPTY");
    }

    private void updateWelcomeMessage() {
        JLabel titleLabel = (JLabel) ((JPanel) ((BorderLayout) getLayout())
                .getLayoutComponent(BorderLayout.NORTH)).getComponent(1);
        titleLabel.setText("🤖 AI Tutor - " + (currentCourseName != null ? currentCourseName : "CS Assistant"));
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}