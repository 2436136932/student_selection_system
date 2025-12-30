-- 确保使用正确的数据库
USE student_selection_system;

-- 禁用外键约束检查
SET FOREIGN_KEY_CHECKS = 0;

-- 先删除有外键依赖的表（按依赖关系从下往上删除）
DROP TABLE IF EXISTS chat_messages;
DROP TABLE IF EXISTS chat_sessions;
DROP TABLE IF EXISTS student_award_records;
DROP TABLE IF EXISTS student_award_applications;
DROP TABLE IF EXISTS selections;
DROP TABLE IF EXISTS scores;

-- 然后删除被依赖的表
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS awards;
DROP TABLE IF EXISTS notices;
DROP TABLE IF EXISTS carousel;
DROP TABLE IF EXISTS majors;
DROP TABLE IF EXISTS verification_codes;
DROP TABLE IF EXISTS users;

-- 启用外键约束检查
SET FOREIGN_KEY_CHECKS = 1;

-- 用户表（用于系统登录）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    role VARCHAR(20) NOT NULL COMMENT '角色：admin/teacher/student',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) DEFAULT 'https://picsum.photos/id/1005/200/200' COMMENT '用户头像URL，默认头像',
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
    teacher_id BIGINT COMMENT '授课教师ID',
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
    quota INT DEFAULT 0 COMMENT '名额',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status VARCHAR(20) DEFAULT '未发布' COMMENT '奖项状态：未发布/已发布/已结束',
    current_status VARCHAR(20) DEFAULT '待开始' COMMENT '当前状态：待开始/进行中/已完成/已关闭',
    current_stage VARCHAR(20) DEFAULT '未开始' COMMENT '当前阶段：学生申请/教师审批/管理员审批/结果公示',
    approving_teacher_id BIGINT COMMENT '审批教师ID',
    approving_teacher_name VARCHAR(50) COMMENT '审批教师姓名',
    email_sent INT DEFAULT 0 COMMENT '邮件是否已发送：0-未发送，1-已发送',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖项表';



-- 通知表
CREATE TABLE IF NOT EXISTS notices (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    publisher_id BIGINT NOT NULL COMMENT '发布人ID',
    publisher_name VARCHAR(50) NOT NULL COMMENT '发布人姓名',
    publish_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status INT DEFAULT 1 COMMENT '状态（0：未发布，1：已发布）',
    type VARCHAR(20) DEFAULT 'info' COMMENT '通知类型：info/warning/error'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 专业表
CREATE TABLE IF NOT EXISTS majors (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '专业ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '专业名称',
    department VARCHAR(100) NOT NULL COMMENT '所属院系',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业表';

-- 插入初始专业数据（将在TRUNCATE后执行）


-- 学生奖项关联表
-- 创建学生奖项申请表
CREATE TABLE IF NOT EXISTS student_award_applications (
    application_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    award_id BIGINT NOT NULL COMMENT '奖项ID',
    application_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    description VARCHAR(255) NULL COMMENT '申请理由',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核, 1-教师审核通过，待管理员审批, 2-教师审核拒绝, 3-管理员审批通过, 4-管理员审批拒绝',
    teacher_approval_status TINYINT DEFAULT 0 COMMENT '教师审批状态：0-待审核, 1-通过, 2-不通过',
    teacher_approval_time DATETIME COMMENT '教师审批时间',
    teacher_approval_comments TEXT COMMENT '教师审批意见',
    admin_approval_status TINYINT DEFAULT 0 COMMENT '管理员审批状态：0-待审核, 1-通过, 2-不通过',
    admin_approval_time DATETIME COMMENT '管理员审批时间',
    admin_approval_comments TEXT COMMENT '管理员审批意见',
    material_path VARCHAR(255) DEFAULT '' COMMENT '申请材料文件路径',
    material_name VARCHAR(255) DEFAULT '' COMMENT '申请材料文件名',
    material_size BIGINT DEFAULT 0 COMMENT '申请材料文件大小（字节）',
    material_type VARCHAR(100) DEFAULT '' COMMENT '申请材料文件类型',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (award_id) REFERENCES awards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生奖项申请表';

-- 创建学生获奖记录表
CREATE TABLE IF NOT EXISTS student_award_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '获奖记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    award_id BIGINT NOT NULL COMMENT '奖项ID',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    student_number VARCHAR(20) NOT NULL COMMENT '学号',
    class_name VARCHAR(50) NOT NULL COMMENT '班级',
    major VARCHAR(100) NOT NULL COMMENT '专业',
    award_name VARCHAR(100) NOT NULL COMMENT '奖项名称',
    award_level VARCHAR(20) NOT NULL COMMENT '奖项级别（国家级、省级、校级）',
    award_type VARCHAR(20) NOT NULL COMMENT '奖项类型（奖学金、优秀学生、优秀干部等）',
    award_time DATETIME NOT NULL COMMENT '获奖时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (award_id) REFERENCES awards(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生获奖记录表';

-- 创建轮播图表
CREATE TABLE IF NOT EXISTS carousel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '轮播图ID',
    image_url VARCHAR(255) NOT NULL COMMENT '图片URL',
    title VARCHAR(100) COMMENT '标题',
    description VARCHAR(255) COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    interval_time INT DEFAULT 3000 COMMENT '切换时间（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- 创建聊天会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    user1_id BIGINT NOT NULL COMMENT '用户1 ID',
    user2_id BIGINT NOT NULL COMMENT '用户2 ID',
    status VARCHAR(20) DEFAULT 'active' COMMENT '会话状态：active/closed',
    last_message TEXT COMMENT '最后一条消息内容',
    last_message_time DATETIME COMMENT '最后一条消息时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user1_user2 (user1_id, user2_id),
    UNIQUE KEY uk_user2_user1 (user2_id, user1_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 创建聊天消息表
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    sender_type VARCHAR(20) NOT NULL COMMENT '发送者类型：student/teacher/admin',
    content TEXT NOT NULL COMMENT '消息内容',
    read_status VARCHAR(20) DEFAULT 'unread' COMMENT '阅读状态：read/unread',
    message_type VARCHAR(20) DEFAULT 'text' COMMENT '消息类型：text/image/emoji/file',
    file_name VARCHAR(255) COMMENT '文件名',
    file_url VARCHAR(255) COMMENT '文件URL',
    file_size BIGINT COMMENT '文件大小（字节）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 创建验证码表
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '验证码ID',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    code VARCHAR(20) NOT NULL COMMENT '验证码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    status TINYINT DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验证码表';

-- 插入轮播图初始数据
INSERT INTO carousel (image_url, title, description, sort_order, interval_time, status) VALUES
('https://picsum.photos/1200/800?random=1', '欢迎使用学生评选系统', '学生评选系统提供奖学金、助学金等多种评选功能', 1, 3000, 1),
('https://picsum.photos/1200/800?random=2', '评选流程说明', '了解详细的评选流程和规则', 2, 4000, 1),
('https://picsum.photos/1200/800?random=3', '最新通知', '查看系统发布的最新通知和公告', 3, 5000, 1),
('https://picsum.photos/1200/800?random=4', '优秀学生展示', '表彰各类优秀学生，激励大家共同进步', 4, 6000, 1),
('https://picsum.photos/1200/800?random=5', '活动预告', '即将开展的各类学生活动，欢迎积极参与', 5, 7000, 1);

-- 插入初始数据

-- 插入初始专业数据
INSERT INTO majors (name, department) VALUES
('计算机科学与技术', '计算机学院'),
('软件工程', '计算机学院'),
('网络工程', '计算机学院'),
('数学与应用数学', '数学学院'),
('信息与计算科学', '数学学院'),
('电子信息工程', '电子工程学院'),
('通信工程', '电子工程学院'),
('自动化', '自动化学院'),
('机械工程', '机械工程学院'),
('会计学', '经济管理学院'),
('市场营销', '经济管理学院'),
('物理学', '物理学院'),
('化学工程', '化学学院');

-- 插入管理员用户 (密码为: password)
INSERT INTO users (username, password, role, real_name, email, phone, avatar, status) VALUES
('admin', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'admin', '系统管理员', 'admin@example.com', '13800138000', 'https://picsum.photos/id/1005/200/200', 1);

-- 插入示例教师
INSERT INTO teachers (teacher_number, name, gender, title, department, phone, email) VALUES
('T001', '张三', '男', '教授', '计算机学院', '13900139001', 'zhangsan@example.com'),
('T002', '李四', '女', '副教授', '数学学院', '13900139002', 'lisi@example.com'),
('T003', '王五', '男', '讲师', '物理学院', '13900139003', 'wangwu@example.com');

-- 插入教师用户 (密码为: password)
INSERT INTO users (username, password, role, real_name, email, phone, avatar, status) VALUES
('teacher1', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'teacher', '张三', 'zhangsan@example.com', '13900139001', 'https://picsum.photos/id/1005/200/200', 1),
('teacher2', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'teacher', '李四', 'lisi@example.com', '13900139002', 'https://picsum.photos/id/1005/200/200', 1),
('teacher3', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'teacher', '王五', 'wangwu@example.com', '13900139003', 'https://picsum.photos/id/1005/200/200', 1);

-- 更新教师表中的user_id
UPDATE teachers SET user_id = (SELECT id FROM users WHERE username = 'teacher1') WHERE teacher_number = 'T001';
UPDATE teachers SET user_id = (SELECT id FROM users WHERE username = 'teacher2') WHERE teacher_number = 'T002';
UPDATE teachers SET user_id = (SELECT id FROM users WHERE username = 'teacher3') WHERE teacher_number = 'T003';

-- 插入示例课程
INSERT INTO courses (course_code, course_name, credits, hours, department, teacher_id, semester, year, max_students, current_students, status, description) VALUES
('CS101', '计算机导论', 3.0, 48, '计算机学院', 1, '2024-2025-1', 2024, 100, 0, 1, '计算机科学基础课程'),
('MA201', '高等数学', 4.0, 64, '数学学院', 2, '2024-2025-1', 2024, 150, 0, 1, '数学基础课程'),
('PH301', '大学物理', 3.5, 56, '物理学院', 3, '2024-2025-1', 2024, 120, 0, 1, '物理基础课程');

-- 插入示例奖项
INSERT INTO awards (award_name, award_level, award_type, description, requirement, approving_teacher_id, approving_teacher_name) VALUES
('国家奖学金', 'national', 'academic', '国家最高级别奖学金', '成绩优异，综合素质突出', 1, '张三'),
('省级三好学生', 'provincial', 'comprehensive', '省级优秀学生荣誉', '德智体美劳全面发展', 1, '张三'),
('校级一等奖学金', 'school', 'academic', '校级最高级别奖学金', '专业成绩排名前5%', 2, '李四'),
('院级优秀学生干部', 'department', 'leadership', '院级优秀学生干部', '在学生工作中表现突出', 3, '王五');


-- 插入示例学生
INSERT INTO students (student_number, name, gender, birth_date, major, class_name, admission_year, phone, email, status) VALUES
('S001', '赵同学', '男', '2002-01-01', '计算机科学与技术', '计科2001', 2020, '13800138001', 'zhaostudent@example.com', 1),
('S002', '钱同学', '女', '2002-02-02', '数学与应用数学', '数学2001', 2020, '13800138002', 'qianstudent@example.com', 1),
('S003', '孙同学', '男', '2002-03-03', '物理学', '物理2001', 2020, '13800138003', 'sunstudent@example.com', 1);   



-- 注意：这里不需要TRUNCATE TABLE users，因为我们已经在前面清空过了
INSERT INTO users (username, password, role, real_name, email, phone, avatar, status) VALUES
('student1', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'student', '赵同学', 'zhaostudent@example.com', '13800138001', 'https://picsum.photos/id/1005/200/200', 1),
('student2', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'student', '钱同学', 'qianstudent@example.com', '13800138002', 'https://picsum.photos/id/1005/200/200', 1),
('student3', '$2a$10$QFSTE8rMelK7GRMcyV.E.O4h9DZH5511KLErT.QkHS2xcL7bqpeyi', 'student', '孙同学', 'sunstudent@example.com', '13800138003', 'https://picsum.photos/id/1005/200/200', 1);




-- 更新学生表中的user_id
UPDATE students SET user_id = (SELECT id FROM users WHERE username = 'student1') WHERE student_number = 'S001';
UPDATE students SET user_id = (SELECT id FROM users WHERE username = 'student2') WHERE student_number = 'S002';
UPDATE students SET user_id = (SELECT id FROM users WHERE username = 'student3') WHERE student_number = 'S003';



-- 插入示例通知数据
INSERT INTO notices (title, content, publisher_id, publisher_name, publish_time, status, type) VALUES
('2025年校级奖学金评选开始通知', '各位同学：2025年校级奖学金评选工作已经开始，请符合条件的同学及时申请。申请截止时间：2025年9月15日。', 1, '系统管理员', '2025-11-30 09:00:00', 1, 'info'),
('关于规范学生评优材料提交的通知', '为确保评优工作的公平公正，现将评优材料提交要求进行规范，请各位同学认真阅读并按照要求提交材料。', 1, '系统管理员', '2025-12-02 10:30:00', 1, 'warning'),
('2025年度奖学金发放通知', '2024年度奖学金已经发放，请各位获奖同学及时查收。如有疑问，请联系学生工作处。', 1, '系统管理员', '2025-12-10 14:00:00', 1, 'info'),
('评优系统使用说明', '为方便各位同学使用评优系统，现将系统使用方法和注意事项进行说明，请大家仔细阅读。', 1, '系统管理员', '2025-12-20 09:00:00', 1, 'info'),
('关于推迟科技创新大赛申报截止时间的通知', '由于部分同学反映准备时间不足，经研究决定，将科技创新大赛申报截止时间推迟至2025年4月25日。', 1, '系统管理员', '2025-12-10 16:00:00', 1, 'warning');

-- 为学生添加选课记录
INSERT INTO selections (student_id, course_id, selection_time, status) VALUES
(1, 1, NOW(), 1),  -- 学生1选计算机导论
(1, 2, NOW(), 1),  -- 学生1选高等数学
(2, 1, NOW(), 1),  -- 学生2选计算机导论
(3, 3, NOW(), 1);  -- 学生3选大学物理

-- 为学生添加成绩记录
INSERT INTO scores (student_id, course_id, usual_score, exam_score, total_score, grade, status) VALUES
(1, 1, 85.5, 90.0, 87.5, 'A', 1),  -- 学生1计算机导论成绩
(1, 2, 80.0, 85.0, 82.5, 'B', 1),  -- 学生1高等数学成绩
(2, 1, 75.0, 80.0, 77.5, 'B', 1),  -- 学生2计算机导论成绩
(3, 3, 90.0, 95.0, 92.5, 'A', 1);  -- 学生3大学物理成绩

-- 更新课程的当前选课人数
UPDATE courses SET current_students = (SELECT COUNT(*) FROM selections WHERE course_id = 1 AND status = 1) WHERE id = 1;
UPDATE courses SET current_students = (SELECT COUNT(*) FROM selections WHERE course_id = 2 AND status = 1) WHERE id = 2;
UPDATE courses SET current_students = (SELECT COUNT(*) FROM selections WHERE course_id = 3 AND status = 1) WHERE id = 3;

-- 创建索引
-- 暂时注释掉索引创建语句，避免语法错误
-- 索引可以在数据库初始化成功后手动创建
/*
CREATE INDEX idx_students_major ON students(major);
CREATE INDEX idx_students_class_name ON students(class_name);
CREATE INDEX idx_courses_department ON courses(department);
CREATE INDEX idx_courses_teacher_id ON courses(teacher_id);
CREATE INDEX idx_courses_semester ON courses(semester);
CREATE INDEX idx_scores_student_id ON scores(student_id);
CREATE INDEX idx_scores_course_id ON scores(course_id);
CREATE INDEX idx_scores_total_score ON scores(total_score);
CREATE INDEX idx_notices_publish_time ON notices(publish_time);
CREATE INDEX idx_notices_status ON notices(status);
*/

--密码本
--账号student1 密码password
--账号admin 密码password
