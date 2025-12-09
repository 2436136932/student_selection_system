USE student_selection_system;

-- 检查并修复student_award_applications表结构
ALTER TABLE student_award_applications
ADD COLUMN IF NOT EXISTS teacher_approval_status TINYINT DEFAULT 0 COMMENT '教师审批状态：0-待审核, 1-通过, 2-不通过',
ADD COLUMN IF NOT EXISTS teacher_approval_time DATETIME COMMENT '教师审批时间',
ADD COLUMN IF NOT EXISTS teacher_approval_comments TEXT COMMENT '教师审批意见',
ADD COLUMN IF NOT EXISTS admin_approval_status TINYINT DEFAULT 0 COMMENT '管理员审批状态：0-待审核, 1-通过, 2-不通过',
ADD COLUMN IF NOT EXISTS admin_approval_time DATETIME COMMENT '管理员审批时间',
ADD COLUMN IF NOT EXISTS admin_approval_comments TEXT COMMENT '管理员审批意见';

-- 检查是否添加成功
DESCRIBE student_award_applications;