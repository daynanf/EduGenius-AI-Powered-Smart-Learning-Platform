// FILE: src/main/java/com/edugenius/views/student/StudentDashboardPanel.java
package com.edugenius.views.student;

import com.edugenius.config.AppTheme;
import com.edugenius.models.Student;
import com.edugenius.models.User;
import com.edugenius.models.Course;
import com.edugenius.services.AuthService;
import com.edugenius.services.CourseService;
import com.edugenius.views.NavigationManager;
import com.edugenius.views.ParameterReceiver;
import com.edugenius.views.components.CourseCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * Student Dashboard - Main hub for students
 */
public class StudentDashboardPanel extends JPanel implements ParameterReceiver {
    
    private Student currentStudent;
    private CourseService courseService;
    private JPanel courseGridPanel;
    private JComboBox<String> yearSemesterCombo;
    
    // Labels for dynamic updating
    private JLabel greetingLabel;
    private JLabel welcomeNameLabel;
    private JLabel navNameLabel;
    private boolean dataLoaded = false;
    
    public StudentDashboardPanel() {
        courseService = new CourseService();
        setLayout(new BorderLayout());
        setBackground(AppTheme.SURFACE);
        initUI();
        // Don't load data in constructor - wait for receiveParameters
    }
    
    @Override
    public void receiveParameters(Map<String, Object> params) {
        System.out.println("[DEBUG] StudentDashboardPanel: receiveParameters called!");
        // Load student data every time this screen is shown
        loadStudentData();
        loadCourses();
    }
    
    private void initUI() {
        // North: Navigation Bar
        add(createNavBar(), BorderLayout.NORTH);
        
        // Center: Scrollable Content Canvas
        JScrollPane scrollPane = new JScrollPane(createMainContent());
        scrollPane.setBorder(null);
        scrollPane.setBackground(AppTheme.SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createNavBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(AppTheme.NAVY);
        navBar.setPreferredSize(new Dimension(0, 64));
        
        navBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 15)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)
        ));
        
        // Left: Brand Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("EduGenius");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(AppTheme.TEAL);
        logoPanel.add(logoLabel);
        
        JPanel logoContainer = new JPanel(new GridBagLayout());
        logoContainer.setOpaque(false);
        logoContainer.add(logoPanel);
        navBar.add(logoContainer, BorderLayout.WEST);
        
        // Right: Logout Button
        JPanel rightActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightActionsPanel.setOpaque(false);
        
        // Student name display in navbar
        navNameLabel = new JLabel("Student");
        navNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        navNameLabel.setForeground(AppTheme.WHITE);
        rightActionsPanel.add(navNameLabel);
        
        // Custom Styled Logout Button
        JButton logoutBtn = new JButton("Logout") {
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
                
                if (isHovered) {
                    g2.setColor(AppTheme.TEAL_DARK);
                    g2.fillRoundRect(0, 0, w, h, 18, 18);
                    g2.setColor(AppTheme.WHITE);
                } else {
                    g2.setColor(new Color(0, 0, 0, 0));
                    g2.fillRoundRect(0, 0, w, h, 18, 18);
                    g2.setColor(AppTheme.TEAL);
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.drawRoundRect(1, 1, w - 2, h - 2, 18, 18);
                }
                
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn.addActionListener(e -> {
            AuthService.getInstance().logout();
            NavigationManager.getInstance().clearHistory();
            NavigationManager.getInstance().navigateTo("WELCOME");
        });
        
        JPanel btnWrapper = new JPanel(new GridBagLayout());
        btnWrapper.setOpaque(false);
        btnWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        btnWrapper.add(logoutBtn);
        rightActionsPanel.add(btnWrapper);
        
        navBar.add(rightActionsPanel, BorderLayout.EAST);
        
        return navBar;
    }
    
    private JPanel createMainContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(AppTheme.SURFACE);
        content.setBorder(BorderFactory.createEmptyBorder(28, 32, 32, 32));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        // Welcome Banner Panel
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 28, 0);
        content.add(createWelcomeBannerPanel(), gbc);
        
        // Section Header with Semester Selector
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        content.add(createSectionHeader(), gbc);
        
        // Course Grid Panel
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        courseGridPanel = new JPanel(new GridLayout(0, 1, 20, 20));
        courseGridPanel.setSize(250,100);
        courseGridPanel.setBackground(AppTheme.SURFACE);
        content.add(courseGridPanel, gbc);
        
        return content;
    }
    
    private JPanel createWelcomeBannerPanel() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(AppTheme.NAVY_MED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                
                g2.setColor(new Color(255, 255, 255, 12));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, AppTheme.CARD_RADIUS, AppTheme.CARD_RADIUS);
                
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        
        // Left side: Text info
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        greetingLabel = new JLabel("Good morning,");
        greetingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        greetingLabel.setForeground(new Color(224, 251, 245, 180));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        textPanel.add(greetingLabel, gbc);
        
        welcomeNameLabel = new JLabel("Student!");
        welcomeNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        welcomeNameLabel.setForeground(AppTheme.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        textPanel.add(welcomeNameLabel, gbc);
        
        JLabel tagLabel = new JLabel("Welcome back to your learning hub. Let's master your courses today.");
        tagLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagLabel.setForeground(new Color(255, 255, 255, 150));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(tagLabel, gbc);
        
        banner.add(textPanel, BorderLayout.WEST);
        
        return banner;
    }
    
    private JPanel createSectionHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AppTheme.SURFACE);
        
        // Left accent indicator strip and title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titlePanel.setOpaque(false);
        
        JPanel indicator = new JPanel();
        indicator.setBackground(AppTheme.TEAL);
        indicator.setPreferredSize(new Dimension(4, 20));
        titlePanel.add(indicator);
        
        JLabel titleLabel = new JLabel("Your Courses");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(AppTheme.INK);
        titlePanel.add(titleLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        
        // Semester JComboBox dropdown selector
        yearSemesterCombo = new JComboBox<>(new String[]{
            "Year 1, Semester 1", "Year 1, Semester 2",
            "Year 2, Semester 1", "Year 2, Semester 2",
            "Year 3, Semester 1", "Year 3, Semester 2",
            "Year 4, Semester 1", "Year 4, Semester 2"
        });
        yearSemesterCombo.setFont(AppTheme.FONT_BODY);
        yearSemesterCombo.setBackground(AppTheme.WHITE);
        yearSemesterCombo.addActionListener(e -> loadCourses());
        
        JPanel comboWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        comboWrapper.setOpaque(false);
        comboWrapper.add(yearSemesterCombo);
        
        header.add(comboWrapper, BorderLayout.EAST);
        
        return header;
    }
    
    private String getGreeting() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour < 12) return "Good morning,";
        if (hour < 17) return "Good afternoon,";
        return "Good evening,";
    }
    
    private void loadStudentData() {
        try {
            System.out.println("[DEBUG] StudentDashboardPanel: Loading student data...");
            
            // Get current user from AuthService
            User user = AuthService.getInstance().getCurrentUser();
            System.out.println("[DEBUG] Current user from AuthService: " + user);
            
            if (user instanceof Student) {
                currentStudent = (Student) user;
                System.out.println("[DEBUG] Student found: " + currentStudent.getFullName());
                
                String fullName = currentStudent.getFullName();
                if (fullName != null && !fullName.isEmpty()) {
                    String firstName = fullName.split(" ")[0];
                    greetingLabel.setText(getGreeting());
                    welcomeNameLabel.setText(firstName + "!");
                    
                    // Update navbar name
                    if (navNameLabel != null) {
                        navNameLabel.setText(firstName);
                    }
                }
                
                // Set combo box based on student's year/semester
                int year = currentStudent.getYearOfStudy();
                int semester = currentStudent.getSemester();
                int index = (year - 1) * 2 + (semester - 1);
                if (index >= 0 && index < yearSemesterCombo.getItemCount()) {
                    yearSemesterCombo.setSelectedIndex(index);
                }
            } else {
                System.out.println("[DEBUG] Current user is not a Student or is null");
                greetingLabel.setText(getGreeting());
                welcomeNameLabel.setText("Student!");
                if (navNameLabel != null) {
                    navNameLabel.setText("Student");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Loading student data: " + e.getMessage());
            e.printStackTrace();
            greetingLabel.setText(getGreeting());
            welcomeNameLabel.setText("Student!");
            if (navNameLabel != null) {
                navNameLabel.setText("Student");
            }
        }
    }
    
    private void loadCourses() {
        int selectedIndex = yearSemesterCombo.getSelectedIndex();
        int year = selectedIndex / 2 + 1;
        int semester = selectedIndex % 2 + 1;
        
        System.out.println("[DEBUG] Loading courses for Year: " + year + ", Semester: " + semester);
        
        SwingWorker<List<Course>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Course> doInBackground() {
                return courseService.getCoursesByYearAndSemester(year, semester);
            }
            
            @Override
            protected void done() {
                try {
                    List<Course> courses = get();
                    System.out.println("[DEBUG] Found " + courses.size() + " courses");
                    displayCourses(courses);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDashboardPanel.this,
                        "Failed to load courses: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void displayCourses(List<Course> courses) {
        courseGridPanel.removeAll();
        
        if (courses.isEmpty()) {
            JLabel emptyLabel = new JLabel("No courses available for this semester");
            emptyLabel.setFont(AppTheme.FONT_BODY);
            emptyLabel.setForeground(AppTheme.MUTED);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            courseGridPanel.setLayout(new BorderLayout());
            courseGridPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            courseGridPanel.setLayout(new GridLayout(0, 1, 20, 20));
            courseGridPanel.setSize(250,100);
            for (Course course : courses) {
                CourseCard card = new CourseCard(course);
                courseGridPanel.add(card);
            }
        }
        
        courseGridPanel.revalidate();
        courseGridPanel.repaint();
    }
    
}  