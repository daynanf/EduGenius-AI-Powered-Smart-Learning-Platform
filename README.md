# 🎓 EduGenius - AAU CS Smart Learning Platform

Complete Project Architecture & Documentation

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [System Architecture](#system-architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Core Features](#core-features)
- [Design Patterns](#design-patterns)
- [AI Integration](#ai-integration)
- [User Flow](#user-flow)
- [Setup Guide](#setup-guide)

---

## 🎯 Project Overview

EduGenius is an AI-powered desktop learning platform built specifically for Addis Ababa University Computer Science students. It leverages the Groq AI API (LLaMA 3.3 70B) to provide intelligent tutoring, quiz generation, and personalized study planning.

### Key Highlights

- 🎓 **Tailored for AAU CS Students** - Uses AAU student ID format (UGR/XXXX/XX)
- 🤖 **AI-Powered Learning** - Quiz generation, tutoring, and study plans
- 📚 **Course Management** - Organized by Year and Semester (1-5)
- 👨‍🏫 **Dual Role System** - Separate dashboards for Students and Teachers
- 💾 **MySQL Database** - 14 tables with complete data persistence

---

## 🏗️ System Architecture

### MVC (Model-View-Controller) Pattern

The application follows the MVC architectural pattern for clean separation of concerns:

```
┌─────────────────────────────────────────────────────────────┐
│                      USER INTERFACE                         │
├─────────────────────────────────────────────────────────────┤
│  Views Layer (Swing Panels)                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  Welcome     │  │  Login       │  │  Dashboard   │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  Controllers & Services Layer                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │ AuthService  │  │ QuizService  │  │  AIService   │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  Models & Data Layer                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  User        │  │  Course      │  │  QuizSession │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
├─────────────────────────────────────────────────────────────┤
│  Database Layer (MySQL)                                    │
│  ┌──────────────────────────────────────────────────┐     │
│  │  14 Tables + Stored Procedures                   │     │
│  └──────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

### Frontend (Desktop GUI)

| Component | Technology | Purpose |
|-----------|-----------|---------|
| UI Framework | Java Swing | Desktop GUI |
| Look & Feel | FlatLaf 3.4 | Modern UI styling |
| Charts | JFreeChart 1.5.4 | Progress visualization |
| Fonts | Segoe UI | Clean typography |

### Backend

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Language | Java 17+ | Core logic |
| Build Tool | Maven | Dependency management |
| Database | MySQL 8.0 | Data persistence |
| AI API | Groq (LLaMA 3.3 70B) | AI features |

### Key Dependencies

```xml
<dependencies>
    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.3.0</version>
    </dependency>

    <!-- JSON Parsing -->
    <dependency>
        <groupId>org.json</groupId>
        <artifactId>json</artifactId>
        <version>20240303</version>
    </dependency>

    <!-- Modern UI -->
    <dependency>
        <groupId>com.formdev</groupId>
        <artifactId>flatlaf</artifactId>
        <version>3.4</version>
    </dependency>

    <!-- Charts -->
    <dependency>
        <groupId>org.jfree</groupId>
        <artifactId>jfreechart</artifactId>
        <version>1.5.4</version>
    </dependency>
</dependencies>
```

---

## 📁 Project Structure

```
src/main/java/com/edugenius/
│
├── Main.java                              # Application Entry Point
│
├── config/
│   └── AppTheme.java                      # Colors, Fonts, UI Constants
│
├── db/
│   └── DatabaseManager.java              # Singleton DB Connection
│
├── models/
│   ├── User.java                         # Abstract Base User
│   ├── Student.java                      # Student extends User
│   ├── Teacher.java                      # Teacher extends User
│   ├── Course.java                       # Course Model
│   ├── QuizSession.java                  # Quiz Session Model
│   ├── QuizAnswer.java                   # Answer Model
│   └── QuizQuestion.java                 # AI Generated Question
│
├── services/
│   ├── AuthService.java                  # Login/Register Logic
│   ├── CourseService.java                # Course Operations
│   ├── QuizService.java                  # Quiz Operations
│   └── HistoryService.java               # History Tracking
│
├── ai/
│   ├── AIService.java                    # Base Groq API Caller
│   ├── QuizAIService.java                # Quiz Generation
│   ├── TutorAIService.java               # AI Tutor Chat
│   └── StudyPlanAIService.java           # Study Plan Generation
│
├── views/
│   ├── MainWindow.java                   # Main JFrame with CardLayout
│   ├── NavigationManager.java            # Screen Navigation
│   │
│   ├── auth/
│   │   ├── WelcomePanel.java             # Sign-up Screen
│   │   └── LoginPanel.java               # Login Screen
│   │
│   ├── student/
│   │   ├── StudentDashboardPanel.java    # Course Grid
│   │   ├── AILearningDashboardPanel.java # Feature Hub
│   │   ├── QuizPanel.java                # Quiz Taking UI
│   │   ├── AITutorPanel.java             # Chat Interface
│   │   └── StudyPlanPanel.java           # Study Plan Viewer
│   │
│   ├── teacher/
│   │   └── TeacherDashboardPanel.java    # Quiz Creator
│   │
│   └── components/
│       ├── CourseCard.java               # Course Widget
│       ├── QuestionCard.java             # Question Widget
│       └── FeatureCard.java              # Feature Card
│
└── utils/
    ├── SecurityUtils.java                # SHA-256 Hashing
    └── ValidationUtils.java              # AAU ID Validation
```

---

## 🗄️ Database Schema

### 14 Tables with Relationships

```sql
-- Core User Tables
users              -- Authentication & user info
students           -- Student-specific data
teachers           -- Teacher-specific data

-- Course Management
courses            -- Course catalog
enrollments        -- Student-course enrollment

-- Quiz System
quiz_sessions      -- Quiz attempts
quiz_answers       -- Individual answers

-- AI Features
ai_tutor_sessions  -- Chat sessions
ai_tutor_messages  -- Chat messages
study_plans        -- Generated plans
study_plan_tasks   -- Plan tasks

-- Analytics
weak_topics        -- Detected weak areas
achievements       -- Earned badges

-- System
system_config      -- App settings
```

### Key Relationships

```
users (1) ──┬── (1) students
            ├── (1) teachers
            ├── (∞) quiz_sessions
            ├── (∞) ai_tutor_sessions
            ├── (∞) study_plans
            ├── (∞) weak_topics
            └── (∞) achievements

courses (1) ───┬── (∞) enrollments
               ├── (∞) quiz_sessions
               ├── (∞) ai_tutor_sessions
               └── (∞) study_plans
```

---

## 🎨 Design Patterns Implemented

| Pattern | Location | Purpose |
|---------|----------|---------|
| Singleton | DatabaseManager, AuthService, NavigationManager | Single instance for DB, Auth, Navigation |
| MVC | views, services, models | Separation of concerns |
| Factory | QuizAIService | Creates questions dynamically |
| Observer | Progress tracking | Observes quiz completions |
| Strategy | Quiz Services | Different AI prompt strategies |
| Template | AIService | Base AI chat with overrides |
| Command | NavigationManager | Screen navigation commands |
| Inheritance | User → Student/Teacher | Reuse and extend functionality |
| Polymorphism | getDashboardType() | Different dashboards per role |

---

## 🤖 AI Integration

### Groq API Setup

```java
// API Configuration
API URL: https://api.groq.com/openai/v1/chat/completions
Model: llama-3.3-70b-versatile
Free Tier: Yes (No credit card required)
Rate Limit: Handled gracefully
```

### Three AI Features

#### 1. Quiz Generator

```java
QuizAIService.generateQuiz(topic, difficulty, count)
```

- Generates 5-15 questions on any CS topic
- Supports multiple difficulty levels (EASY/MEDIUM/HARD)
- Returns JSON with questions, options, and explanations

#### 2. AI Tutor

```java
TutorAIService.sendMessage(history, message, context)
```

- Context-aware chat for CS concepts
- Maintains conversation history
- Supports code examples and explanations

#### 3. Study Plan Generator

```java
StudyPlanAIService.generateStudyPlan(prompt, course)
```

- Creates personalized learning roadmaps
- Weekly breakdown with daily tasks
- Estimated time per topic

### Prompt Engineering Examples

**Quiz Generation Prompt:**

```
Generate 10 multiple choice questions about: {topic}
Difficulty: {difficulty}
Respond with JSON array containing question, options, correct answer, explanation
```

**Tutor Prompt:**

```
You are EduGenius AI Tutor, a friendly CS teaching assistant for AAU students.
Course context: {course}
Be encouraging, clear, and include Java code examples when helpful.
```

---

## 👤 User Flow

### Student Journey

```
1. Welcome Screen
   ↓
2. Sign Up (UGR/XXXX/XX)
   ↓
3. Student Dashboard
   ↓
   ├── Select Year/Semester → View Courses
   │
   ├── Click Course Card → AI Learning Dashboard
   │   ├── Study Plan → Generate AI Roadmap
   │   ├── AI Quiz → Generate & Take Quiz
   │   └── AI Tutor → Chat with AI Assistant
   │
   └── Logout → Welcome Screen
```

### Teacher Journey

```
1. Welcome Screen
   ↓
2. Sign Up (EMP/XXXX/XX)
   ↓
3. Teacher Dashboard
   ↓
   └── AI Quiz Generator
       ├── Enter Prompt
       ├── Select Settings
       ├── Generate Quiz
       ├── Review Questions
       └── Assign to Class
```

---

## 🚀 Setup Guide

### Prerequisites

```bash
# Required Software
- Java JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Git
```

### Step 1: Clone Repository

```bash
git clone https://github.com/yourusername/edugenius.git
cd edugenius
```

### Step 2: Configure MySQL

```bash
# Create Database
mysql -u root -p
source schema.sql

# Verify
USE edugenius_db;
SELECT * FROM courses;  # Should show 6 courses
```

### Step 3: Configure Application

```properties
# src/main/resources/config.properties
db.host=localhost
db.port=3306
db.name=edugenius_db
db.user=root
db.password=YOUR_MYSQL_PASSWORD

groq.api.key=YOUR_GROQ_API_KEY
groq.model=llama-3.3-70b-versatile
```

### Step 4: Build & Run

```bash
# Build
mvn clean package

# Run
java -jar target/edugenius-1.0.0.jar

# Or with Maven
mvn exec:java -Dexec.mainClass="com.edugenius.Main"
```

### Step 5: Test Credentials

```
Student Test Account:
- ID: UGR/1234/15
- Password: Test@1234

Teacher Test Account:
- ID: EMP/1234/15
- Password: Test@1234
```

---

## 🔐 Security Features

- **Password Hashing:** SHA-256 encryption (never stored in plain text)
- **AAU ID Validation:** Regex pattern matching
- **Session Management:** User session stored in AuthService
- **Database Security:** Prepared statements (prevents SQL injection)
- **Role-Based Access:** Student/Teacher dashboards separated

---

## 📊 Features Demo Script

### For Presentation (5 Minutes)

```
1. Welcome Screen (30s)
   - Show modern login/signup UI
   - Explain AAU ID format

2. Student Dashboard (1m)
   - Show courses by year/semester
   - Click a course → AI Learning Hub

3. AI Quiz Generator (1m)
   - Enter topic: "Binary Trees"
   - Generate quiz (5 questions)
   - Answer a question → AI Explanation

4. AI Tutor (1m)
   - Ask: "Explain recursion in Java"
   - Show AI response with code

5. Study Plan (1m)
   - Generate personalized roadmap
   - Show weekly breakdown

6. Teacher Dashboard (1m)
   - Show AI Quiz Creator
   - Generate and review questions
```

---

## 🏆 Key Achievements

### OOP Concepts Demonstrated

- ✅ Inheritance (User → Student/Teacher)
- ✅ Polymorphism (getDashboardType())
- ✅ Abstraction (User is abstract)
- ✅ Encapsulation (Private fields)
- ✅ 9 Design Patterns

### Technical Highlights

- ✅ AI Integration (Groq LLaMA 3.3)
- ✅ Desktop Application (Java Swing)
- ✅ Full Database (14 tables)
- ✅ Professional UI (FlatLaf)
- ✅ Complete User Flow


## 📄 License

This project is built for educational purposes at Addis Ababa University.

---

## 🎓 Acknowledgments

- Addis Ababa University - Computer Science Department
- Groq - Free AI API (LLaMA 3.3 70B)
- All contributors and testers

Built with ❤️ for AAU CS Students

> "Study smarter, not harder" - EduGenius Team
