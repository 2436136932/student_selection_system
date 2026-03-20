# 计划：管理员自主配置AI智能推荐权重

## 需求分析
当前AI智能推荐的权重是硬编码在 `AwardRecommendationService.java` 中的：
- 成绩匹配度：40%
- 奖项匹配度：30%
- 专业匹配：15%
- 历史数据：10%
- 竞争程度：5%

需要让管理员可以在前端界面自主调整这些权重比例。

## 修改方案

### 1. 数据库层
**新增表**: `recommendation_weights` (推荐权重配置表)
```sql
CREATE TABLE recommendation_weights (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    grade_weight DECIMAL(5,2) DEFAULT 40.00 COMMENT '成绩权重',
    award_weight DECIMAL(5,2) DEFAULT 30.00 COMMENT '奖项权重',
    major_weight DECIMAL(5,2) DEFAULT 15.00 COMMENT '专业权重',
    history_weight DECIMAL(5,2) DEFAULT 10.00 COMMENT '历史数据权重',
    competition_weight DECIMAL(5,2) DEFAULT 5.00 COMMENT '竞争程度权重',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by BIGINT COMMENT '更新人ID'
);
```

### 2. 后端层
**新增文件**:
- `entity/RecommendationWeight.java` - 实体类
- `mapper/RecommendationWeightMapper.java` - Mapper接口
- `service/RecommendationWeightService.java` - 服务类
- `controller/RecommendationWeightController.java` - 控制器

**修改文件**:
- `AwardRecommendationService.java` - 从数据库读取权重配置

### 3. 前端层
**修改文件**: `SystemSettingsView.vue`
- 添加"AI推荐权重配置"模块（仅管理员可见）
- 使用滑块组件调整各权重
- 权重总和必须为100%，自动校验

**修改文件**: `AwardRecommendationView.vue`
- 从后端获取当前权重配置并显示

## 详细任务清单

### 后端任务
1. 创建 `RecommendationWeight` 实体类
2. 创建 `RecommendationWeightMapper` 接口
3. 创建 `RecommendationWeightService` 服务类
4. 创建 `RecommendationWeightController` 控制器
5. 修改 `AwardRecommendationService` 使用动态权重
6. 更新 `database_init.sql` 添加新表

### 前端任务
1. 修改 `SystemSettingsView.vue` 添加权重配置模块
2. 修改 `AwardRecommendationView.vue` 显示当前权重

## 预期效果
- 管理员可以在"系统设置"页面调整AI推荐的各项权重
- 权重总和必须为100%
- 修改后立即生效，影响所有学生的推荐结果
- 学生端可以看到当前使用的权重配置
