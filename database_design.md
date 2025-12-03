# 学生评奖评优资格筛选系统 - 数据库设计

## 1. 数据库概述

本数据库设计采用关系型数据库模型，基于MySQL数据库，用于存储学生评奖评优系统的所有数据，包括用户信息、学生信息、成绩数据、奖项设置、评选标准和结果等。

## 2. 核心表结构设计

### 2.1 用户表 (users)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 用户名 |
| password | VARCHAR(100) | NOT NULL | 密码（加密存储） |
| name | VARCHAR(50) | NOT NULL | 真实姓名 |
| email | VARCHAR(100) | UNIQUE, NOT NULL | 邮箱 |
| phone | VARCHAR(20) | NULL | 联系电话 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| status | TINYINT | DEFAULT 1 | 状态（1-正常，0-禁用） |

### 2.2 角色表 (roles)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 角色ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 角色名称 |
| description | VARCHAR(200) | NULL | 角色描述 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 2.3 权限表 (permissions)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 权限ID |
| name | VARCHAR(50) | UNIQUE, NOT NULL | 权限名称 |
| description | VARCHAR(200) | NULL | 权限描述 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 2.4 用户角色关联表 (user_roles)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| user_id | INT | NOT NULL | 用户ID |
| role_id | INT | NOT NULL | 角色ID |
| PRIMARY KEY (user_id, role_id) | | | 联合主键 |
| FOREIGN KEY (user_id) REFERENCES users(id) | | | 外键约束 |
| FOREIGN KEY (role_id) REFERENCES roles(id) | | | 外键约束 |

### 2.5 角色权限关联表 (role_permissions)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| role_id | INT | NOT NULL | 角色ID |
| permission_id | INT | NOT NULL | 权限ID |
| PRIMARY KEY (role_id, permission_id) | | | 联合主键 |
| FOREIGN KEY (role_id) REFERENCES roles(id) | | | 外键约束 |
| FOREIGN KEY (permission_id) REFERENCES permissions(id) | | | 外键约束 |

### 2.6 专业表 (majors)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 专业ID |
| name | VARCHAR(100) | UNIQUE, NOT NULL | 专业名称 |
| department | VARCHAR(100) | NOT NULL | 所属院系 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 2.7 学生表 (students)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 学生ID |
| student_id | VARCHAR(20) | UNIQUE, NOT NULL | 学号 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | TINYINT | NOT NULL | 性别（1-男，2-女） |
| birthday | DATE | NULL | 出生日期 |
| major_id | INT | NOT NULL | 专业ID |
| class_name | VARCHAR(50) | NOT NULL | 班级 |
| admission_year | YEAR | NOT NULL | 入学年份 |
| total_score | DECIMAL(5,2) | DEFAULT 0 | 总分数 |
| ranking | INT | NULL | 排名 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| FOREIGN KEY (major_id) REFERENCES majors(id) | | | 外键约束 |

### 2.8 课程表 (courses)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 课程ID |
| name | VARCHAR(100) | NOT NULL | 课程名称 |
| credit | DECIMAL(3,1) | NOT NULL | 学分 |
| semester | VARCHAR(20) | NOT NULL | 学期 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 2.9 成绩表 (scores)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 成绩ID |
| student_id | INT | NOT NULL | 学生ID |
| course_id | INT | NOT NULL | 课程ID |
| score | DECIMAL(5,2) | NOT NULL | 分数 |
| semester | VARCHAR(20) | NOT NULL | 学期 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |
| FOREIGN KEY (course_id) REFERENCES courses(id) | | | 外键约束 |

### 2.10 竞赛表 (competitions)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 竞赛ID |
| name | VARCHAR(100) | NOT NULL | 竞赛名称 |
| level | VARCHAR(20) | NOT NULL | 竞赛级别（国家级、省级、校级） |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |

### 2.11 学生竞赛表 (student_competitions)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 记录ID |
| student_id | INT | NOT NULL | 学生ID |
| competition_id | INT | NOT NULL | 竞赛ID |
| award_level | VARCHAR(20) | NOT NULL | 获奖等级（一等奖、二等奖、三等奖、优秀奖） |
| competition_time | DATE | NOT NULL | 参赛时间 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |
| FOREIGN KEY (competition_id) REFERENCES competitions(id) | | | 外键约束 |

### 2.12 科研成果表 (research_achievements)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 成果ID |
| student_id | INT | NOT NULL | 学生ID |
| title | VARCHAR(200) | NOT NULL | 成果标题 |
| type | VARCHAR(50) | NOT NULL | 成果类型（论文、专利、项目等） |
| publication | VARCHAR(100) | NULL | 发表刊物/平台 |
| publish_date | DATE | NULL | 发表日期 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |

### 2.13 志愿活动表 (volunteer_activities)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 活动ID |
| student_id | INT | NOT NULL | 学生ID |
| name | VARCHAR(100) | NOT NULL | 活动名称 |
| organization | VARCHAR(100) | NOT NULL | 组织单位 |
| start_time | DATE | NOT NULL | 开始时间 |
| end_time | DATE | NOT NULL | 结束时间 |
| hours | INT | NOT NULL | 志愿时长（小时） |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |

### 2.14 奖项表 (awards)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 奖项ID |
| name | VARCHAR(100) | NOT NULL | 奖项名称 |
| type | VARCHAR(50) | NOT NULL | 奖项类型（奖学金、优秀学生、优秀干部等） |
| description | TEXT | NULL | 奖项描述 |
| quota | INT | NOT NULL | 名额 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

### 2.15 评选标准表 (selection_criteria)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 标准ID |
| award_id | INT | NOT NULL | 奖项ID |
| criterion_name | VARCHAR(100) | NOT NULL | 标准名称 |
| criterion_type | VARCHAR(50) | NOT NULL | 标准类型（成绩、竞赛、科研、志愿等） |
| weight | DECIMAL(3,2) | NOT NULL | 权重 |
| threshold | DECIMAL(5,2) | NULL | 阈值（最低要求） |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| FOREIGN KEY (award_id) REFERENCES awards(id) | | | 外键约束 |

### 2.16 评选流程表 (selection_processes)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 流程ID |
| award_id | INT | NOT NULL | 奖项ID |
| process_name | VARCHAR(100) | NOT NULL | 流程名称（初审、复审、终审） |
| start_time | DATETIME | NOT NULL | 开始时间 |
| end_time | DATETIME | NOT NULL | 结束时间 |
| status | TINYINT | DEFAULT 0 | 状态（0-未开始，1-进行中，2-已结束） |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| FOREIGN KEY (award_id) REFERENCES awards(id) | | | 外键约束 |

### 2.17 学生奖项申请表 (student_award_applications)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 申请ID |
| student_id | INT | NOT NULL | 学生ID |
| award_id | INT | NOT NULL | 奖项ID |
| application_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 申请时间 |
| status | TINYINT | DEFAULT 0 | 状态（0-待审核，1-通过，2-未通过） |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |
| FOREIGN KEY (award_id) REFERENCES awards(id) | | | 外键约束 |

### 2.18 评选结果表 (selection_results)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 结果ID |
| student_id | INT | NOT NULL | 学生ID |
| award_id | INT | NOT NULL | 奖项ID |
| final_score | DECIMAL(6,2) | NOT NULL | 最终得分 |
| ranking | INT | NOT NULL | 排名 |
| status | TINYINT | DEFAULT 0 | 状态（0-待公示，1-已公示，2-已确认） |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |
| FOREIGN KEY (award_id) REFERENCES awards(id) | | | 外键约束 |

### 2.19 异议表 (objections)

| 字段名 | 数据类型 | 约束 | 描述 |
| :--- | :--- | :--- | :--- |
| id | INT | PRIMARY KEY, AUTO_INCREMENT | 异议ID |
| student_id | INT | NOT NULL | 学生ID |
| result_id | INT | NOT NULL | 评选结果ID |
| content | TEXT | NOT NULL | 异议内容 |
| status | TINYINT | DEFAULT 0 | 状态（0-待处理，1-已处理，2-已驳回） |
| reply | TEXT | NULL | 处理回复 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| FOREIGN KEY (student_id) REFERENCES students(id) | | | 外键约束 |
| FOREIGN KEY (result_id) REFERENCES selection_results(id) | | | 外键约束 |

## 3. 索引设计

为了提高查询效率，在以下字段上创建索引：

1. `users`表：`username`(唯一索引), `email`(唯一索引)
2. `students`表：`student_id`(唯一索引), `major_id`, `total_score`, `ranking`
3. `scores`表：`student_id`, `course_id`
4. `awards`表：`type`
5. `selection_criteria`表：`award_id`
6. `student_award_applications`表：`student_id`, `award_id`
7. `selection_results`表：`student_id`, `award_id`, `ranking`

## 4. 数据关系图

```
[用户] ---1:N--- [用户角色] ---N:1--- [角色]
[角色] ---1:N--- [角色权限] ---N:1--- [权限]
[专业] ---1:N--- [学生]
[学生] ---1:N--- [成绩]
[课程] ---1:N--- [成绩]
[学生] ---1:N--- [学生竞赛] ---N:1--- [竞赛]
[学生] ---1:N--- [科研成果]
[学生] ---1:N--- [志愿活动]
[奖项] ---1:N--- [评选标准]
[奖项] ---1:N--- [评选流程]
[学生] ---1:N--- [学生奖项申请] ---N:1--- [奖项]
[学生] ---1:N--- [评选结果] ---N:1--- [奖项]
[学生] ---1:N--- [异议] ---N:1--- [评选结果]
```

## 5. 数据初始化建议

1. **角色初始化**：创建学校管理员、教师、学生三种角色
2. **权限初始化**：为每种角色分配相应的权限
3. **专业初始化**：导入学校的所有专业信息
4. **课程初始化**：导入基础课程信息
5. **奖项初始化**：创建基础奖项（如国家奖学金、校级奖学金等）和对应的评选标准

## 6. 数据库安全设计

1. 使用参数化查询防止SQL注入
2. 对用户密码进行加密存储（使用BCrypt或Argon2算法）
3. 实施基于角色的访问控制（RBAC）
4. 定期备份数据库
5. 限制数据库用户的权限，遵循最小权限原则

## 7. 性能优化建议

1. 合理设计索引，避免过多索引影响插入和更新性能
2. 对大表进行分区（如成绩表按学期分区）
3. 优化查询语句，避免全表扫描
4. 定期清理过期数据
5. 使用连接池管理数据库连接