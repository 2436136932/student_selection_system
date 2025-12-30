## 数据库初始化失败原因分析与修复方案

### 问题原因
1. **Spring Boot 3.x 默认行为变化**：
   - 从 Spring Boot 2.5 开始，`spring.sql.init.mode` 的默认值从 `always` 改为了 `embedded`
   - 这意味着它只对嵌入式数据库（如 H2）生效，对外部数据库（如 MySQL）需要显式配置

2. **缺少必要配置**：
   - 对于外部数据库，需要添加 `spring.sql.init.platform` 配置来指定数据库类型
   - 没有该配置时，Spring Boot 不会执行初始化脚本

### 修复方案
修改 `application.yml` 文件，添加 `spring.sql.init.platform` 配置：

```yaml
spring:
  # SQL初始化配置
  sql:
    init:
      schema-locations: classpath:database_init.sql
      mode: always
      platform: mysql  # 添加这行，指定数据库类型为MySQL
```

### 修复依据
根据 Spring Boot 3.x 官方文档，`spring.sql.init.platform` 属性用于指定数据库平台，当使用外部数据库时，必须显式设置此属性，否则初始化脚本不会执行。

### 预期效果
添加该配置后，当启动后端服务时，Spring Boot 会：
1. 检测到 `platform: mysql` 配置
2. 识别当前使用的是外部 MySQL 数据库
3. 执行 `schema-locations` 指定的 SQL 脚本
4. 完成数据库初始化

### 其他建议
1. **验证数据库连接**：确保数据库连接配置正确，包括 URL、用户名、密码
2. **检查SQL语法**：确保 SQL 脚本中的语法兼容当前 MySQL 版本
3. **查看日志**：启动后查看日志中是否有 SQL 初始化相关的调试信息
4. **生产环境调整**：生产环境中建议将 `mode` 改为 `never` 或 `embedded`，避免每次启动时重新初始化数据库

通过添加 `platform: mysql` 配置，应该能够解决数据库初始化失败的问题。