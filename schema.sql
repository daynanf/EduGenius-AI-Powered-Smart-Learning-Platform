-- =============================================
-- EduGenius Database Schema
-- AAU CS Smart Learning Platform
-- MySQL 8.0
-- =============================================

-- Drop database if exists (for clean setup)
DROP DATABASE IF EXISTS edugenius_db_new;
CREATE DATABASE edugenius_db_new;
USE edugenius_db_new;

-- =============================================
-- 1. USERS TABLE (Core authentication)
-- =============================================
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    aau_id VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(200) UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    role ENUM('STUDENT', 'TEACHER', 'ADMIN') NOT NULL,
    profile_image VARCHAR(300),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    is_active BOOLEAN DEFAULT TRUE
);

-- =============================================
-- 2. STUDENTS TABLE
-- =============================================
CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    year_of_study INT DEFAULT 1,
    semester INT DEFAULT 1,
    department VARCHAR(100) DEFAULT 'Computer Science',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =============================================
-- 3. TEACHERS TABLE
-- =============================================
CREATE TABLE teachers (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    staff_id VARCHAR(20) UNIQUE,
    subject_area VARCHAR(200),
    department VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =============================================
-- 4. COURSES TABLE
-- =============================================
CREATE TABLE courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    year_level INT NOT NULL,
    semester INT NOT NULL,
    credits INT DEFAULT 3,
    icon_color VARCHAR(7) DEFAULT '#00C9A7',
    is_active BOOLEAN DEFAULT TRUE
);

-- =============================================
-- 5. ENROLLMENTS TABLE
-- =============================================
CREATE TABLE enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    course_id INT NOT NULL,
    enrolled_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    progress_percent DECIMAL(5,2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (user_id, course_id)
);

-- =============================================
-- 6. QUIZ_SESSIONS TABLE
-- =============================================
CREATE TABLE quiz_sessions (
    session_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    course_id INT,
    topic VARCHAR(200) NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    question_type ENUM('MCQ', 'SHORT_ANSWER') DEFAULT 'MCQ',
    total_questions INT DEFAULT 5,
    correct_answers INT DEFAULT 0,
    score_percent DECIMAL(5,2) DEFAULT 0.00,
    grade VARCHAR(2),
    time_taken_sec INT,
    session_status ENUM('IN_PROGRESS', 'COMPLETED', 'ABANDONED') DEFAULT 'IN_PROGRESS',
    started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL
);

-- =============================================
-- 7. QUIZ_ANSWERS TABLE
-- =============================================
CREATE TABLE quiz_answers (
    answer_id INT PRIMARY KEY AUTO_INCREMENT,
    session_id INT NOT NULL,
    question_no INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500),
    option_b VARCHAR(500),
    option_c VARCHAR(500),
    option_d VARCHAR(500),
    correct_option VARCHAR(1),
    student_answer VARCHAR(500),
    is_correct BOOLEAN,
    ai_explanation TEXT,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD'),
    FOREIGN KEY (session_id) REFERENCES quiz_sessions(session_id) ON DELETE CASCADE
);

-- =============================================
-- 8. AI_TUTOR_SESSIONS TABLE
-- =============================================
CREATE TABLE ai_tutor_sessions (
    chat_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    course_id INT,
    session_title VARCHAR(200),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL
);

-- =============================================
-- 9. AI_TUTOR_MESSAGES TABLE
-- =============================================
CREATE TABLE ai_tutor_messages (
    message_id INT PRIMARY KEY AUTO_INCREMENT,
    chat_id INT NOT NULL,
    role ENUM('USER', 'ASSISTANT') NOT NULL,
    content TEXT NOT NULL,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (chat_id) REFERENCES ai_tutor_sessions(chat_id) ON DELETE CASCADE
);

-- =============================================
-- 10. STUDY_PLANS TABLE
-- =============================================
CREATE TABLE study_plans (
    plan_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    course_id INT,
    plan_title VARCHAR(200),
    prompt_used TEXT,
    plan_content TEXT,
    duration_weeks INT DEFAULT 4,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL
);

-- =============================================
-- 11. STUDY_PLAN_TASKS TABLE
-- =============================================
CREATE TABLE study_plan_tasks (
    task_id INT PRIMARY KEY AUTO_INCREMENT,
    plan_id INT NOT NULL,
    week_number INT NOT NULL,
    day_label VARCHAR(20),
    topic_name VARCHAR(200),
    subtopics TEXT,
    estimated_hours DECIMAL(3,1),
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (plan_id) REFERENCES study_plans(plan_id) ON DELETE CASCADE
);

-- =============================================
-- 12. WEAK_TOPICS TABLE (Auto-detected)
-- =============================================
CREATE TABLE weak_topics (
    weak_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    course_id INT,
    topic_name VARCHAR(200) NOT NULL,
    avg_accuracy DECIMAL(5,2),
    attempt_count INT DEFAULT 0,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE SET NULL,
    UNIQUE KEY unique_user_topic (user_id, topic_name)
);

-- =============================================
-- 13. ACHIEVEMENTS TABLE
-- =============================================
CREATE TABLE achievements (
    ach_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    badge_type VARCHAR(50),
    badge_label VARCHAR(100),
    earned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- =============================================
-- 14. SYSTEM_CONFIG TABLE
-- =============================================
CREATE TABLE system_config (
    config_key VARCHAR(50) PRIMARY KEY,
    config_value TEXT,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- STORED PROCEDURES
-- =============================================

DELIMITER $$

-- Get student's weak topics (accuracy below 60%)
CREATE PROCEDURE sp_get_student_weak_topics(IN p_user_id INT)
BEGIN
    SELECT 
        topic_name,
        avg_accuracy,
        attempt_count,
        last_updated
    FROM weak_topics
    WHERE user_id = p_user_id AND avg_accuracy < 60
    ORDER BY avg_accuracy ASC;
END$$

-- Update or insert weak topic
CREATE PROCEDURE sp_update_weak_topic(
    IN p_user_id INT, 
    IN p_topic VARCHAR(200), 
    IN p_course_id INT, 
    IN p_is_correct BOOLEAN
)
BEGIN
    DECLARE current_accuracy DECIMAL(5,2);
    DECLARE current_count INT;
    
    -- Check if topic exists
    SELECT avg_accuracy, attempt_count INTO current_accuracy, current_count
    FROM weak_topics
    WHERE user_id = p_user_id AND topic_name = p_topic;
    
    IF current_count IS NULL THEN
        -- Insert new weak topic
        INSERT INTO weak_topics (user_id, course_id, topic_name, avg_accuracy, attempt_count)
        VALUES (p_user_id, p_course_id, p_topic, IF(p_is_correct, 100, 0), 1);
    ELSE
        -- Update existing
        SET current_accuracy = (current_accuracy * current_count + IF(p_is_correct, 100, 0)) / (current_count + 1);
        UPDATE weak_topics
        SET avg_accuracy = current_accuracy,
            attempt_count = current_count + 1,
            last_updated = CURRENT_TIMESTAMP
        WHERE user_id = p_user_id AND topic_name = p_topic;
    END IF;
END$$

DELIMITER ;

-- Insert sample system config
INSERT INTO system_config (config_key, config_value) VALUES
('app_version', '1.0.0'),
('default_quiz_questions', '10'),
('default_difficulty', 'MEDIUM');

-- =============================================
-- VERIFY INSTALLATION
-- =============================================
SELECT 'Database created successfully!' AS Status;
SELECT COUNT(*) AS TotalCourses FROM courses;