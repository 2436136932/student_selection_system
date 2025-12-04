-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_selection_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_selection_system;

-- 用户表（用于系统登录）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    role VARCHAR(20) NOT NULL COMMENT '角色：admin/teacher/student',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学生表
CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    student_number VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    class_name VARCHAR(50) NOT NULL COMMENT '班级',
    admission_year YEAR NOT NULL COMMENT '入学年份',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在读，0-毕业',
    user_id BIGINT UNIQUE COMMENT '关联用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 教师表
CREATE TABLE IF NOT EXISTS teachers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    teacher_number VARCHAR(20) NOT NULL UNIQUE COMMENT '工号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    title VARCHAR(50) COMMENT '职称',
    department VARCHAR(100) NOT NULL COMMENT '所属部门',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    user_id BIGINT UNIQUE COMMENT '关联用户ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师表';

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程代码',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credits DECIMAL(3,1) NOT NULL COMMENT '学分',
    hours INT NOT NULL COMMENT '学时',
    department VARCHAR(100) NOT NULL COMMENT '开课部门',
    teacher_id BIGINT NOT NULL COMMENT '授课教师ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期（如：2024-2025-1）',
    year YEAR NOT NULL COMMENT '学年',
    max_students INT DEFAULT 100 COMMENT '最大选课人数',
    current_students INT DEFAULT 0 COMMENT '当前选课人数',
    status TINYINT DEFAULT 1 COMMENT '状态：1-开设，0-关闭',
    description TEXT COMMENT '课程描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 选课表
CREATE TABLE IF NOT EXISTS selections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    selection_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已退选',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课表';

-- 成绩表
CREATE TABLE IF NOT EXISTS scores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    usual_score DECIMAL(5,2) COMMENT '平时成绩',
    exam_score DECIMAL(5,2) COMMENT '考试成绩',
    total_score DECIMAL(5,2) COMMENT '总成绩',
    grade VARCHAR(10) COMMENT '等级（A/B/C/D/E/F）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已发布，0-未发布',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_course (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 奖项表
CREATE TABLE IF NOT EXISTS awards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '奖项ID',
    award_name VARCHAR(100) NOT NULL COMMENT '奖项名称',
    award_level VARCHAR(20) NOT NULL COMMENT '奖项级别：national/provincial/school/department',
    award_type VARCHAR(50) NOT NULL COMMENT '奖项类型',
    description TEXT COMMENT '奖项描述',
    requirement TEXT COMMENT '评奖要求',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖项表';

-- 学生奖项关联表
CREATE TABLE IF NOT EXISTS student_awards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    award_id BIGINT NOT NULL COMMENT '奖项ID',
    award_year YEAR NOT NULL COMMENT '获奖年份',
    award_date DATE NOT NULL COMMENT '获奖日期',
    description TEXT COMMENT '获奖描述',
    certificate_path VARCHAR(255) COMMENT '证书路径',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已确认，0-待审核',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_award_year (student_id, award_id, award_year),
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (award_id) REFERENCES awards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生奖项关联表';

-- 创建评选标准表
CREATE TABLE IF NOT EXISTS selection_criteria (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标准ID',
    award_id BIGINT NOT NULL COMMENT '奖项ID',
    criterion_name VARCHAR(100) NOT NULL COMMENT '标准名称',
    criterion_type VARCHAR(50) NOT NULL COMMENT '标准类型（成绩、竞赛、科研、志愿等）',
    weight DECIMAL(5,2) NOT NULL COMMENT '权重',
    threshold DECIMAL(5,2) DEFAULT 0 COMMENT '阈值（最低要求）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (award_id) REFERENCES awards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评选标准表';

-- 创建学生奖项申请表
CREATE TABLE IF NOT EXISTS student_award_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    award_id BIGINT NOT NULL COMMENT '奖项ID',
    application_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核, 1-通过, 2-未通过',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (award_id) REFERENCES awards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生奖项申请表';

-- 创建评选流程表
CREATE TABLE IF NOT EXISTS selection_process (
    process_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流程ID',
    award_id BIGINT NOT NULL COMMENT '奖项ID',
    process_name VARCHAR(50) NOT NULL COMMENT '流程名称',
    process_order INT NOT NULL COMMENT '流程顺序',
    start_date DATETIME NOT NULL COMMENT '开始时间',
    end_date DATETIME NOT NULL COMMENT '结束时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (award_id) REFERENCES awards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评选流程表';

-- 创建评选结果表
CREATE TABLE IF NOT EXISTS selection_result (
    result_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '结果ID',
    application_id BIGINT NOT NULL COMMENT '申请ID',
    score DECIMAL(5,2) COMMENT '评分',
    comment TEXT COMMENT '评语',
    reviewer VARCHAR(50) NOT NULL COMMENT '评审人',
    review_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评审时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (application_id) REFERENCES student_award_applications(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评选结果表';

-- 插入初始数据
-- 插入管理员用户 (密码为: adminpassword)
INSERT INTO users (username, password, role, real_name, email, phone, status) VALUES
('admin', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'admin', '系统管理员', 'admin@example.com', '13800138000', 1);

-- 插入示例教师
INSERT INTO teachers (teacher_number, name, gender, title, department, phone, email) VALUES
('T001', '张三', '男', '教授', '计算机学院', '13900139001', 'zhangsan@example.com'),
('T002', '李四', '女', '副教授', '数学学院', '13900139002', 'lisi@example.com'),
('T003', '王五', '男', '讲师', '物理学院', '13900139003', 'wangwu@example.com');

-- 插入示例课程
INSERT INTO courses (course_code, course_name, credits, hours, department, teacher_id, semester, year, max_students, current_students, status, description) VALUES
('CS101', '计算机导论', 3.0, 48, '计算机学院', 1, '2024-2025-1', 2024, 100, 0, 1, '计算机科学基础课程'),
('MA201', '高等数学', 4.0, 64, '数学学院', 2, '2024-2025-1', 2024, 150, 0, 1, '数学基础课程'),
('PH301', '大学物理', 3.5, 56, '物理学院', 3, '2024-2025-1', 2024, 120, 0, 1, '物理基础课程');

-- 插入示例奖项
INSERT INTO awards (award_name, award_level, award_type, description, requirement) VALUES
('国家奖学金', 'national', 'academic', '国家最高级别奖学金', '成绩优异，综合素质突出'),
('省级三好学生', 'provincial', 'comprehensive', '省级优秀学生荣誉', '德智体美劳全面发展'),
('校级一等奖学金', 'school', 'academic', '校级最高级别奖学金', '专业成绩排名前5%'),
('院级优秀学生干部', 'department', 'leadership', '院级优秀学生干部', '在学生工作中表现突出');

-- 创建索引
CREATE INDEX idx_students_major ON students(major);
CREATE INDEX idx_students_class_name ON students(class_name);
CREATE INDEX idx_courses_department ON courses(department);
CREATE INDEX idx_courses_teacher_id ON courses(teacher_id);
CREATE INDEX idx_courses_semester ON courses(semester);
CREATE INDEX idx_scores_student_id ON scores(student_id);
CREATE INDEX idx_scores_course_id ON scores(course_id);
CREATE INDEX idx_scores_total_score ON scores(total_score);
CREATE INDEX idx_student_awards_student_id ON student_awards(student_id);
CREATE INDEX idx_student_awards_award_id ON student_awards(award_id);
CREATE INDEX idx_student_awards_award_year ON student_awards(award_year);

--密码本
--账号testuser2 密码testpassword2
--账号admin 密码adminpassword
--账号testuser_new 密码123456
--账号testuserS 密码123456
--账号studentY 密码123456