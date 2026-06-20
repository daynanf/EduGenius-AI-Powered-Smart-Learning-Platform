-- =============================================
-- CS COMPLETE COURSES SEED DATA
-- Source: AAU CNCS Class Schedules 2025/2026
-- Year I Semester 1 → Year IV Semester 2
-- =============================================

INSERT INTO courses (course_code, course_name, description, year_level, semester, credits, icon_color) VALUES

-- =============================================
-- YEAR I - SEMESTER 1
-- (Natural Science common first year)
-- =============================================
('FLEn1011', 'Communicative English Language Skills I', 'Develops academic English communication skills including reading, writing, speaking, and listening for university-level study.', 1, 1, 3, '#00C9A7'),
('SpSc1011', 'Physical Fitness', 'Physical education course focused on fitness, wellness, and healthy lifestyle habits for university students.', 1, 1, 3, '#95A5A6'),
('Psyc1011', 'General Psychology', 'Introduction to human behavior, cognition, perception, learning, memory, motivation, and psychological development.', 1, 1, 3, '#FFB347'),
('Phys1011', 'General Physics', 'Fundamental principles of mechanics, thermodynamics, waves, and electromagnetism with mathematical applications.', 1, 1, 3, '#4ECDC4'),
('Math1011', 'Mathematics for Natural Sciences', 'Pre-calculus and calculus foundations: algebra, trigonometry, functions, limits, and introductory differentiation.', 1, 1, 3, '#7C5CBF'),
('LoCT1011', 'Critical Thinking', 'Principles of logic, argumentation, reasoning, fallacy identification, and analytical problem-solving skills.', 1, 1, 3, '#A29BFE'),
('GeES1011', 'Geography of Ethiopia and the Horn', 'Physical and human geography of Ethiopia and the Horn of Africa including resources, climate, and regional development.', 1, 1, 3, '#FD79A8'),
('Hist1102', 'History of Ethiopia and the Horn I', 'Political, economic, social, and cultural history of Ethiopia and the Horn of Africa from ancient times to present.', 1, 1, 3, '#E17055'),

-- =============================================
-- YEAR I - SEMESTER 2
-- =============================================
('FLEn1012', 'Communicative English Language Skills II', 'Advanced academic English skills: essay writing, research-based reading, oral presentations, and technical communication.', 1, 2, 3, '#00C9A7'),
('Anth1012', 'Social Anthropology', 'Study of human societies, cultures, social structures, kinship, religion, and cultural change in a global context.', 1, 2, 3, '#95A5A6'),
('CoSc1011', 'Fundamentals of Computer Science', 'Introduction to computing concepts, number systems, data representation, algorithms, hardware, software, and the internet.', 1, 2, 3, '#7C5CBF'),
('Econ1011', 'Economics', 'Introduction to microeconomics and macroeconomics: supply and demand, market structures, GDP, inflation, and fiscal policy.', 1, 2, 3, '#FFB347'),
('EmTe1012', 'Introduction to Emerging Technologies', 'Survey of transformative technologies including AI, blockchain, IoT, cloud computing, and their societal implications.', 1, 2, 3, '#4ECDC4'),
('MCiE1012', 'Moral and Civic Education', 'Ethical reasoning, civic responsibility, democratic values, human rights, and the role of citizens in society.', 1, 2, 3, '#FD79A8'),
('CoSc1012', 'Computer Programming', 'Introduction to programming using a high-level language: variables, control flow, functions, arrays, and basic problem solving.', 1, 2, 3, '#FF6B6B'),

-- =============================================
-- YEAR II - SEMESTER 1
-- =============================================
('CoSc2111', 'Computer Programming II', 'Continuation of programming fundamentals covering advanced control structures, functions, arrays, pointers, and file handling.', 2, 1, 3, '#00C9A7'),
('Math2021', 'Calculus I', 'Limits, continuity, differentiation, integration, and their applications. Foundation for advanced mathematical methods in computing.', 2, 1, 3, '#7C5CBF'),
('Math2191', 'Introduction to Linear Algebra', 'Vectors, matrices, systems of linear equations, determinants, eigenvalues, and eigenvectors with computing applications.', 2, 1, 3, '#FFB347'),
('Stat2181', 'Introduction to Statistics', 'Descriptive statistics, probability, distributions, hypothesis testing, and statistical inference for computer science students.', 2, 1, 3, '#FF6B6B'),
('ECEG1351', 'Fundamentals of Electricity and Digital Electronics', 'Basic electrical circuits, logic gates, Boolean algebra, combinational and sequential digital circuits.', 2, 1, 3, '#4ECDC4'),
('GlTr1012', 'Global Trends', 'Contemporary global issues, technological, economic, and social transformations shaping the modern world.', 2, 1, 3, '#95A5A6'),

-- =============================================
-- YEAR II - SEMESTER 2
-- =============================================
('CoSc2012', 'Computer Organization and Architecture', 'CPU design, instruction sets, memory hierarchy, I/O systems, pipelining, and performance evaluation.', 2, 2, 3, '#00C9A7'),
('CoSc2112', 'Object Oriented Programming', 'OOP principles including classes, objects, inheritance, polymorphism, encapsulation, and exception handling.', 2, 2, 3, '#7C5CBF'),
('CoSc2212', 'Fundamentals of Database Systems', 'Relational model, SQL, database design, normalization, transactions, and query optimization.', 2, 2, 3, '#FFB347'),
('Math2022', 'Calculus II', 'Techniques of integration, sequences and series, multivariable calculus, and differential equations.', 2, 2, 3, '#FF6B6B'),
('Stat2182', 'Introduction to Probability Theory', 'Probability axioms, random variables, probability distributions, expectation, and limit theorems.', 2, 2, 3, '#4ECDC4'),
('Math3221', 'Applied Numerical Analysis', 'Numerical methods for solving mathematical problems: interpolation, integration, linear systems, and differential equations.', 2, 2, 3, '#A29BFE'),

-- =============================================
-- YEAR III - SEMESTER 1
-- =============================================
('CoSc3011', 'Advanced Database Systems', 'Advanced SQL, stored procedures, triggers, transactions, concurrency control, query optimization, and NoSQL databases.', 3, 1, 3, '#00C9A7'),
('CoSc3111', 'Data Structures and Algorithms', 'Arrays, linked lists, stacks, queues, trees, graphs, hashing, sorting, searching, and algorithm complexity analysis.', 3, 1, 3, '#7C5CBF'),
('CoSc3211', 'Computer Networking and Data Communication', 'OSI model, TCP/IP, LAN/WAN technologies, routing protocols, and network programming fundamentals.', 3, 1, 3, '#FFB347'),
('CoSc3311', 'Introduction to Software Engineering', 'Software development life cycle, requirements engineering, system design, testing, project management, and quality assurance.', 3, 1, 3, '#FF6B6B'),
('CoSc2033', 'Microprocessor and Assembly Language', 'Microprocessor architecture, assembly language programming, memory interfacing, and I/O programming.', 3, 1, 3, '#4ECDC4'),
('Math2231', 'Discrete Mathematics and Combinatorics', 'Sets, logic, proof techniques, relations, functions, combinatorics, graph theory, and their applications in CS.', 3, 1, 3, '#A29BFE'),

-- =============================================
-- YEAR III - SEMESTER 2
-- =============================================
('CoSc3012', 'Design and Analysis of Algorithms', 'Algorithm design paradigms: divide and conquer, greedy, dynamic programming, graph algorithms, and NP-completeness.', 3, 2, 3, '#00C9A7'),
('CoSc3112', 'Operating Systems', 'Process management, scheduling, memory management, file systems, I/O, deadlocks, and distributed systems concepts.', 3, 2, 3, '#7C5CBF'),
('CoSc3212', 'Network and System Administration', 'Server configuration, user management, network services, security policies, backup strategies, and performance monitoring.', 3, 2, 3, '#FFB347'),
('CoSc3312', 'Web Application Development', 'Client-server architecture, HTML/CSS, JavaScript, server-side scripting, RESTful APIs, and web security fundamentals.', 3, 2, 3, '#FF6B6B'),
('CoSc3412', 'Computer Graphics', 'Raster and vector graphics, 2D/3D transformations, rendering, shading, OpenGL, and visualization techniques.', 3, 2, 3, '#4ECDC4'),
('MGMT1012', 'Entrepreneurship', 'Business plan development, innovation, startup ecosystem, financial basics, and entrepreneurial mindset for technology professionals.', 3, 2, 3, '#FD79A8'),

-- =============================================
-- YEAR IV - SEMESTER 1
-- =============================================
('CoSc4011', 'Introduction to Artificial Intelligence', 'Search algorithms, knowledge representation, reasoning, machine learning basics, natural language processing, and expert systems.', 4, 1, 3, '#00C9A7'),
('CoSc4111', 'Wireless Communication and Mobile Computing', 'Wireless technologies, mobile network architectures, protocols, mobile application development, and IoT fundamentals.', 4, 1, 3, '#7C5CBF'),
('CoSc4311', 'Formal Language and Automata Theory', 'Finite automata, regular expressions, context-free grammars, pushdown automata, Turing machines, and computability.', 4, 1, 3, '#FFB347'),
('CoSc4411', 'Final Year Project I', 'First phase of the capstone project: problem identification, literature review, proposal writing, and initial system design.', 4, 1, 3, '#FF6B6B'),
('CoSc4511', 'Technical Report Writing in Computer Science', 'Scientific writing, academic paper structure, technical documentation, research presentation, and publication standards.', 4, 1, 3, '#4ECDC4'),
('CoSc4021', 'Elective I', 'Specialized elective course covering advanced topics in computer science based on current industry and research trends.', 4, 1, 3, '#A29BFE'),

-- =============================================
-- YEAR IV - SEMESTER 2
-- =============================================
('CoSc4012', 'Computer Security', 'Cryptography, network security, authentication, access control, intrusion detection, malware analysis, and security policies.', 4, 2, 3, '#00C9A7'),
('CoSc4212', 'Computer Design', 'Advanced digital design, VLSI concepts, FPGA programming, hardware-software co-design, and system-on-chip architectures.', 4, 2, 3, '#7C5CBF'),
('CoSc4312', 'Complexity Theory', 'Computational complexity classes, P vs NP, reductions, approximation algorithms, randomized algorithms, and intractability.', 4, 2, 3, '#FFB347'),
('CoSc4412', 'Real Time and Embedded Systems', 'Real-time operating systems, scheduling, embedded hardware, microcontroller programming, and system reliability.', 4, 2, 3, '#FF6B6B'),
('CoSc4112', 'Final Year Project II', 'Completion of the capstone project: system implementation, testing, evaluation, documentation, and final presentation.', 4, 2, 3, '#4ECDC4'),
('CoSc4022', 'Elective II', 'Advanced elective covering emerging topics such as cloud computing, cybersecurity, data science, or distributed systems.', 4, 2, 3, '#A29BFE'),
('Hist1012', 'History of Ethiopia and the Horn II', 'Continuation of Ethiopian and Horn of Africa history with focus on modern political, economic, and social developments.', 4, 2, 3, '#FD79A8');
