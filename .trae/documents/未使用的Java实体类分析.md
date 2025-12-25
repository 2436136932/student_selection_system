# 删除未使用的Java实体类及相关文件计划

## 待删除的实体类及相关文件

### 1. Objection（异议）相关
- `src/main/java/com/example/studentselectionsystem/entity/Objection.java`
- `src/main/java/com/example/studentselectionsystem/mapper/ObjectionMapper.java`

### 2. VolunteerActivity（志愿活动）相关
- `src/main/java/com/example/studentselectionsystem/entity/VolunteerActivity.java`
- `src/main/java/com/example/studentselectionsystem/mapper/VolunteerActivityMapper.java`

### 3. StudentCompetition（学生竞赛）相关
- `src/main/java/com/example/studentselectionsystem/entity/StudentCompetition.java`
- `src/main/java/com/example/studentselectionsystem/mapper/StudentCompetitionMapper.java`
- 需要修改：从 `Competition` 实体类中删除 `studentCompetitions` 属性

### 4. ResearchAchievement（研究成果）相关
- `src/main/java/com/example/studentselectionsystem/entity/ResearchAchievement.java`
- `src/main/java/com/example/studentselectionsystem/mapper/ResearchAchievementMapper.java`

## 删除步骤

1. **删除实体类文件**
   - 依次删除上述4个实体类文件

2. **删除对应的Mapper接口**
   - 依次删除上述4个Mapper接口文件

3. **修改Competition实体类**
   - 删除 `studentCompetitions` 属性及其getter/setter方法

4. **编译验证**
   - 执行 `mvn compile` 命令验证删除后项目能否正常编译

5. **启动验证**
   - 启动后端服务验证系统能否正常运行

## 注意事项

1. 这些实体类在代码库中没有被实际使用，删除后不会影响系统功能
2. 数据库中不存在对应的表，无需清理数据库结构
3. 前端没有相关视图或逻辑，无需修改前端代码
4. 删除后需要重新编译项目确保没有编译错误

## 预期结果

- 项目编译通过
- 后端服务正常启动
- 系统功能不受影响
- 代码库更加精简，减少不必要的维护成本