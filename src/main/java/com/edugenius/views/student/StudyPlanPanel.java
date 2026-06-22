// FILE: src/main/java/com/edugenius/views/student/StudyPlanPanel.java
package com.edugenius.views.student;

import com.edugenius.config.AppTheme;
import com.edugenius.ai.StudyPlanAIService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.ParameterReceiver;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class StudyPlanPanel extends JPanel implements ParameterReceiver {

    private StudyPlanAIService aiService;
    private int currentCourseId;
    private String currentCourseName;

    // UI refs updated after plan generation
    private JTextField promptField;
    private JPanel outputBody;
    private JButton generateButton;
    private JLabel courseMetaValue;          // updated when params arrive
    private JLabel durationMetaValue;        // updated dynamically from prompt field
    private String lastPlanText = "";

    public StudyPlanPanel() {
        aiService = StudyPlanAIService.getInstance();
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(LAVENDER_BG);
        initUI();
    }

    @Override
    public void receiveParameters(Map<String, Object> params) {
        this.currentCourseId = (int) params.get("courseId");
        this.currentCourseName = (String) params.get("courseName");
        // Update course meta card
        if (courseMetaValue != null)
            courseMetaValue.setText(currentCourseName);
        // Repaint prompt field so placeholder reflects the course name
        if (promptField != null)
            promptField.repaint();
    }

    // ─── Top bar ──────────────────────────────────────────────────────────────

    private void initUI() {
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.NAVY);
        bar.setBorder(new EmptyBorder(14, 20, 14, 20));

        JButton backBtn = new JButton("← Back");
        backBtn.setFont(AppTheme.FONT_BODY);
        backBtn.setForeground(AppTheme.TEAL);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> NavigationManager.getInstance().navigateTo("AI_LEARNING"));
        bar.add(backBtn, BorderLayout.WEST);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);

        // Icon circle
        JPanel iconCircle = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PURPLE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconCircle.setPreferredSize(new Dimension(28, 28));
        iconCircle.setOpaque(false);
        iconCircle.setLayout(new GridBagLayout());
        // FIX 2: plain text instead of emoji (emoji render as boxes on most Java/Windows setups)
        JLabel iconLbl = new JLabel("SP");
        iconLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 10));
        iconLbl.setForeground(Color.WHITE);
        iconCircle.add(iconLbl);
        titleRow.add(iconCircle);

        JLabel titleLabel = new JLabel("AI Study Plan Generator");
        titleLabel.setFont(AppTheme.FONT_H2);
        titleLabel.setForeground(AppTheme.WHITE);
        titleRow.add(titleLabel);

        bar.add(titleRow, BorderLayout.CENTER);

        // AI badge — FIX 2: no emoji
        JLabel badge = new JLabel("* Powered by AI") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = new Color(AppTheme.PURPLE.getRed(), AppTheme.PURPLE.getGreen(),
                        AppTheme.PURPLE.getBlue(), 50);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 11));
        badge.setForeground(new Color(0xCECBF6));
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        badge.setOpaque(false);
        bar.add(badge, BorderLayout.EAST);

        return bar;
    }

    // ─── Main content ─────────────────────────────────────────────────────────

    private static final Color LAVENDER_BG = new Color(0xF4F3FF);

    private JPanel buildCenterPanel() {
        JPanel center = new JPanel();
        center.setLayout(new BorderLayout(0, 16));
        center.setOpaque(true);
        center.setBackground(LAVENDER_BG);
        center.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel inputCard = buildInputCard();
        JPanel outputCard = buildOutputCard();

        // FIX: wrap input card so it doesn't stretch vertically (BorderLayout.NORTH
        // keeps it at its preferred height), and let the output card take ALL
        // remaining space (BorderLayout.CENTER) so the lavender never shows through
        // as leftover white space below/around the cards.
        center.add(inputCard, BorderLayout.NORTH);
        center.add(outputCard, BorderLayout.CENTER);

        return center;
    }

    // ─── Input card ───────────────────────────────────────────────────────────

    private JPanel buildInputCard() {
        JPanel card = createRoundedCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setOpaque(false); // rounded card paints its own white background

        // Section label
        JLabel sectionLbl = new JLabel("YOUR GOAL");
        sectionLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 10));
        sectionLbl.setForeground(new Color(0x888780));
        sectionLbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        card.add(sectionLbl, BorderLayout.NORTH);

        // Input + button row
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        promptField = new JTextField() {
            private final String placeholder = "e.g. I want to master Object Oriented Programming in 2 weeks";
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.dispose();
                super.paintComponent(g);
                // Draw placeholder when field is empty and not focused
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D pg = (Graphics2D) g.create();
                    pg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    pg.setColor(new Color(0xAAAAAA));
                    pg.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    FontMetrics fm = pg.getFontMetrics();
                    pg.drawString(placeholder, ins.left, ins.top + fm.getAscent());
                    pg.dispose();
                }
            }
        };
        promptField.setOpaque(false);
        promptField.setFont(AppTheme.FONT_BODY);
        promptField.setBorder(new EmptyBorder(10, 12, 10, 12));
        // Start empty — placeholder guides the user
        promptField.setText("");
        promptField.addActionListener(e -> generateStudyPlan());
        promptField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateDuration(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateDuration(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateDuration(); }
        });
        row.add(promptField, BorderLayout.CENTER);

        generateButton = buildPrimaryButton("Generate plan");
        generateButton.setPreferredSize(new Dimension(160, 42));
        generateButton.addActionListener(e -> generateStudyPlan());
        row.add(generateButton, BorderLayout.EAST);

        card.add(row, BorderLayout.CENTER);

        // Meta row
        card.add(buildMetaRow(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildMetaRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 0, 0));

        // FIX 1: build Course card manually so we can hold a ref to the value label
        JPanel courseCard = new JPanel(new BorderLayout(0, 3));
        courseCard.setBackground(Color.WHITE);
        courseCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        JLabel courseLbl = new JLabel("COURSE");
        courseLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 10));
        courseLbl.setForeground(new Color(0x888780));
        courseMetaValue = new JLabel(currentCourseName != null ? currentCourseName : "—");
        courseMetaValue.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 12));
        courseMetaValue.setForeground(AppTheme.INK);
        courseCard.add(courseLbl, BorderLayout.NORTH);
        courseCard.add(courseMetaValue, BorderLayout.CENTER);

        row.add(courseCard);

        // Duration card — built manually so durationMetaValue can be updated dynamically
        JPanel durationCard = new JPanel(new BorderLayout(0, 3));
        durationCard.setBackground(Color.WHITE);
        durationCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        JLabel durationLbl = new JLabel("DURATION");
        durationLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 10));
        durationLbl.setForeground(new Color(0x888780));
        durationMetaValue = new JLabel("2 – 4 weeks");
        durationMetaValue.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 12));
        durationMetaValue.setForeground(AppTheme.INK);
        durationCard.add(durationLbl, BorderLayout.NORTH);
        durationCard.add(durationMetaValue, BorderLayout.CENTER);

        row.add(durationCard);
        row.add(buildMetaCard("Model", "Groq · LLaMA 3"));
        return row;
    }

    private JPanel buildMetaCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 10));
        lbl.setForeground(new Color(0x888780));
        JLabel val = new JLabel(value);
        val.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 12));
        val.setForeground(AppTheme.INK);
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ─── Output card ──────────────────────────────────────────────────────────

    private JPanel buildOutputCard() {
        JPanel wrapper = createRoundedCard();
        wrapper.setLayout(new BorderLayout());
        wrapper.setOpaque(false); // rounded card paints its own white background

        // Output card header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                new EmptyBorder(12, 16, 12, 12)));

        JPanel titleSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleSide.setOpaque(false);
        // FIX 2: no emoji — use a small drawn square indicator instead
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PURPLE);
                g2.fillRoundRect(0, 4, 10, 10, 4, 4);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(10, 18));
        dot.setOpaque(false);
        JLabel outputTitle = new JLabel("Your study plan");
        outputTitle.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 13));
        outputTitle.setForeground(AppTheme.INK);
        titleSide.add(dot);
        titleSide.add(outputTitle);
        header.add(titleSide, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        actions.add(buildIconButton("Copy", "Copy plan", () -> {
            if (!lastPlanText.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(lastPlanText), null);
            }
        }));
        actions.add(buildIconButton("Save", "Save as text", this::savePlanAsText));
        header.add(actions, BorderLayout.EAST);

        wrapper.add(header, BorderLayout.NORTH);

        // Body — swap between empty state and content
        outputBody = new JPanel();
        outputBody.setOpaque(false);
        outputBody.setLayout(new BorderLayout());
        showEmptyState();

        JScrollPane scroll = new JScrollPane(outputBody);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    // ─── Output states ────────────────────────────────────────────────────────

    private void showEmptyState() {
        outputBody.removeAll();
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
        empty.setBorder(new EmptyBorder(48, 0, 48, 0));

        // FIX 2: drawn clipboard icon — no emoji
        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xEEEDFE));
                g2.fillOval(0, 0, 44, 44);
                g2.setColor(new Color(0x7F77DD));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(14, 10, 16, 22, 3, 3);
                g2.drawLine(18, 10, 18, 7); g2.drawLine(26, 10, 26, 7);
                g2.drawLine(18, 7, 26, 7);
                g2.drawLine(17, 17, 27, 17);
                g2.drawLine(17, 21, 27, 21);
                g2.drawLine(17, 25, 23, 25);
                g2.dispose();
            }
        };
        iconBox.setPreferredSize(new Dimension(44, 44));
        iconBox.setMaximumSize(new Dimension(44, 44));
        iconBox.setOpaque(false);
        iconBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("<html><div style='text-align:center;width:220px;'>"
                + "Describe your learning goal above and click <b>Generate plan</b> "
                + "to get your personalized roadmap</div></html>");
        msg.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 13));
        msg.setForeground(new Color(0x888780));
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);

        empty.add(Box.createVerticalGlue());
        empty.add(iconBox);
        empty.add(Box.createVerticalStrut(12));
        empty.add(msg);
        empty.add(Box.createVerticalGlue());

        outputBody.add(empty, BorderLayout.CENTER);
        outputBody.revalidate();
        outputBody.repaint();
    }

    private void showLoadingState() {
        outputBody.removeAll();
        JPanel loading = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 16));
        loading.setOpaque(false);
        loading.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Animated dots via timer
        JLabel dots = new JLabel("●");
        dots.setForeground(AppTheme.PURPLE);
        dots.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 18));
        JLabel msg = new JLabel("Generating your plan…");
        msg.setFont(AppTheme.FONT_BODY);
        msg.setForeground(new Color(0x888780));

        loading.add(dots);
        loading.add(msg);

        Timer dotTimer = new Timer(400, null);
        dotTimer.addActionListener(e -> {
            String t = dots.getText();
            dots.setText(t.length() >= 3 ? "●" : t + "●");
        });
        dotTimer.start();

        outputBody.add(loading, BorderLayout.NORTH);
        outputBody.putClientProperty("dotTimer", dotTimer);
        outputBody.revalidate();
        outputBody.repaint();
    }

    private void showPlanContent(String planText) {
        // Stop any running dot timer
        Object timer = outputBody.getClientProperty("dotTimer");
        if (timer instanceof Timer) ((Timer) timer).stop();

        lastPlanText = planText;
        outputBody.removeAll();

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Parse and render week blocks
        String[] lines = planText.split("\n");
        JPanel currentWeekPanel = null;
        StringBuilder dayBuffer = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Detect week heading (lines with ## or containing "Week")
            if (trimmed.startsWith("##") || (trimmed.toLowerCase().contains("week") && trimmed.length() < 60)) {
                if (currentWeekPanel != null) {
                    content.add(currentWeekPanel);
                    content.add(Box.createVerticalStrut(12));
                }
                currentWeekPanel = buildWeekBlock(trimmed.replace("#", "").trim());
            } else if (currentWeekPanel != null) {
                // Day row — look for day patterns: "**Mon**", "- Day 1:", etc.
                String dayName = extractDayName(trimmed);
                if (dayName != null) {
                    String task = trimmed.replaceAll("^[\\-\\*]?\\s*\\**(Mon|Tue|Wed|Thu|Fri|Sat|Sun|Day\\s*\\d+)\\**:?\\s*", "").trim();
                    String hrs = extractHours(task);
                    task = task.replaceAll("\\(\\d+(\\.\\d+)?\\s*h(ours?)?\\)", "").trim();
                    currentWeekPanel.add(buildDayRow(dayName, task, hrs));
                } else {
                    // Generic content line — render as plain text row
                    currentWeekPanel.add(buildTextRow(trimmed));
                }
            } else {
                // Before any week heading — top-level text
                content.add(buildTextRow(trimmed));
                content.add(Box.createVerticalStrut(4));
            }
        }

        // Add last week block
        if (currentWeekPanel != null) {
            content.add(currentWeekPanel);
        }

        // If nothing structured was detected, fall back to a readable text area
        if (content.getComponentCount() == 0) {
            content.add(buildFallbackTextArea(planText));
        }

        content.add(Box.createVerticalStrut(16));
        outputBody.add(content, BorderLayout.NORTH);
        outputBody.revalidate();
        outputBody.repaint();
    }

    // ─── Week / day renderers ─────────────────────────────────────────────────

    private JPanel buildWeekBlock(String title) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Week title badge
        JPanel titleBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        titleBadge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        titleBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBadge.setBackground(new Color(0xEEEDFE));
        titleBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCECBF6), 1, true),
                new EmptyBorder(0, 4, 0, 4)));

        // FIX 2: no emoji in week badge
        JLabel titleLbl = new JLabel("  " + title);
        titleLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 13));
        titleLbl.setForeground(new Color(0x3C3489));
        titleBadge.add(titleLbl);
        block.add(titleBadge);
        block.add(Box.createVerticalStrut(2));

        return block;
    }

    private JPanel buildDayRow(String dayName, String task, String hrs) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(AppTheme.BORDER.getRed(),
                        AppTheme.BORDER.getGreen(), AppTheme.BORDER.getBlue(), 80)),
                new EmptyBorder(6, 10, 6, 10)));

        JLabel dayLbl = new JLabel(dayName);
        dayLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 12));
        dayLbl.setForeground(new Color(0x5F5E5A));
        dayLbl.setPreferredSize(new Dimension(72, 20));
        row.add(dayLbl, BorderLayout.WEST);

        JLabel taskLbl = new JLabel(task);
        taskLbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 12));
        taskLbl.setForeground(AppTheme.INK);
        row.add(taskLbl, BorderLayout.CENTER);

        if (hrs != null && !hrs.isEmpty()) {
            JLabel hrsBadge = new JLabel(hrs) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0xEEEDFE));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            hrsBadge.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.BOLD, 11));
            hrsBadge.setForeground(new Color(0x3C3489));
            hrsBadge.setBorder(new EmptyBorder(2, 8, 2, 8));
            hrsBadge.setOpaque(false);
            row.add(hrsBadge, BorderLayout.EAST);
        }

        return row;
    }

    private JPanel buildTextRow(String text) {
        // Strip markdown bold/italic markers for display
        String clean = text.replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                           .replaceAll("\\*(.+?)\\*", "$1")
                           .replaceAll("^[-•]\\s*", "• ");
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(2, 10, 2, 10));
        JLabel lbl = new JLabel(clean);
        lbl.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 12));
        lbl.setForeground(new Color(0x5F5E5A));
        row.add(lbl, BorderLayout.WEST);
        return row;
    }

    private JTextArea buildFallbackTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setFont(AppTheme.FONT_BODY);
        area.setForeground(AppTheme.INK);
        area.setOpaque(false);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 8, 8, 8));
        return area;
    }

    // ─── Parsing helpers ──────────────────────────────────────────────────────

    private String extractDayName(String line) {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun",
                         "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String lower = line.toLowerCase();
        for (String d : days) {
            if (lower.startsWith(d.toLowerCase()) || lower.startsWith("**" + d.toLowerCase())) {
                return d.length() > 3 ? d.substring(0, 3) : d;
            }
        }
        if (line.toLowerCase().matches("^\\*?\\*?day\\s*\\d+.*")) {
            return line.replaceAll("(?i)^\\*?\\*?(day\\s*\\d+).*", "$1");
        }
        return null;
    }

    private String extractHours(String text) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\\((\\d+(\\.\\d+)?)\\s*h(ours?)?\\)")
                .matcher(text);
        return m.find() ? m.group(1) + "h" : null;
    }

    // ─── Dynamic duration ─────────────────────────────────────────────────────

    private void updateDuration() {
        if (durationMetaValue == null) return;
        String detected = extractDuration(promptField.getText());
        durationMetaValue.setText(detected);
    }

    private String extractDuration(String text) {
        if (text == null || text.isBlank()) return "2 – 4 weeks";
        String t = text.toLowerCase();
        // Days
        java.util.regex.Matcher dayM = java.util.regex.Pattern
                .compile("(\\d+)\\s*day").matcher(t);
        if (dayM.find()) return dayM.group(1) + " day" + (Integer.parseInt(dayM.group(1)) > 1 ? "s" : "");
        if (t.contains("a day") || t.contains("one day")) return "1 day";
        // Weeks
        java.util.regex.Matcher weekM = java.util.regex.Pattern
                .compile("(\\d+)\\s*week").matcher(t);
        if (weekM.find()) return weekM.group(1) + " week" + (Integer.parseInt(weekM.group(1)) > 1 ? "s" : "");
        if (t.contains("a week") || t.contains("one week")) return "1 week";
        // Months
        java.util.regex.Matcher monthM = java.util.regex.Pattern
                .compile("(\\d+)\\s*month").matcher(t);
        if (monthM.find()) return monthM.group(1) + " month" + (Integer.parseInt(monthM.group(1)) > 1 ? "s" : "");
        if (t.contains("a month") || t.contains("one month")) return "1 month";
        return "2 – 4 weeks"; // default
    }

    // ─── Action helpers ───────────────────────────────────────────────────────

    private void savePlanAsText() {
        if (lastPlanText.isEmpty()) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("study-plan.txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
                pw.print(lastPlanText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not save file: " + ex.getMessage());
            }
        }
    }

    // ─── Generate ─────────────────────────────────────────────────────────────

    private void generateStudyPlan() {
        String prompt = promptField.getText().trim();
        if (prompt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe your learning goal!", "Missing goal",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        generateButton.setEnabled(false);
        generateButton.setText("Generating...");
        showLoadingState();

        aiService.generateStudyPlan(prompt, currentCourseName,
            plan -> {
                showPlanContent(plan);
                generateButton.setEnabled(true);
                generateButton.setText("Regenerate");
            },
            error -> {
                showPlanContent("Error generating study plan:\n\n" + error + "\n\nPlease try again.");
                generateButton.setEnabled(true);
                generateButton.setText("Generate plan");
            }
        );
    }

    // ─── UI component factories ───────────────────────────────────────────────

    private JPanel createRoundedCard() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        // FIX: card paints its own white background manually, so it must NOT be
        // opaque — otherwise Swing's default opaque-fill (parent LAF default,
        // typically white) paints first and can bleed through the rounded
        // corners / edges, and more importantly, leaving it opaque was masking
        // the lavender page background anywhere this card didn't fully cover.
        card.setOpaque(false);
        return card;
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setBorderPainted(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? (hovered ? AppTheme.PURPLE.darker() : AppTheme.PURPLE)
                                        : new Color(0xAFA9EC));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.BUTTON_RADIUS, AppTheme.BUTTON_RADIUS);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btn.setFont(AppTheme.FONT_BODY_BOLD);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildIconButton(String icon, String tooltip, Runnable action) {
        JButton btn = new JButton(icon) {
            private boolean hovered = false;
            {
                setContentAreaFilled(false);
                setFocusPainted(false);
                setToolTipText(tooltip);
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(AppTheme.SURFACE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(new Color(0x5F5E5A));
                g2.setFont(new Font(AppTheme.FONT_BODY.getName(), Font.PLAIN, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(60, 30));
        btn.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER, 1, true));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }
}