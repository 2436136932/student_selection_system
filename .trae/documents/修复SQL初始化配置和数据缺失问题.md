## 问题分析

从配置文件和数据库初始化脚本中，我发现了导致日志错误和数据缺失的两个关键问题：

1. **SQL初始化配置错误**：
   - `schema-locations`和`data-locations`指向同一个文件`database_init.sql`
   - 这会导致SQL语句被重复执行，引发错误
   - `mode: never`设置阻止了SQL初始化执行，导致数据库无表无数据

2. **配置逻辑问题**：
   - 同一个SQL文件不应同时作为schema和data文件使用
   - 当`mode: never`时，Spring Boot不会执行任何SQL初始化

## 修复方案

1. **修改`application.yml`配置**：
   - 保留`schema-locations`配置，移除`data-locations`配置（因为`database_init.sql`已经包含了数据插入）
   - 将`mode`设置为`always`，确保SQL初始化执行

2. **验证修复结果**：
   - 重启后端服务
   - 检查数据库表结构是否正确创建
   - 检查初始数据是否成功插入
   - 检查日志是否有错误信息

## 预期效果

- 数据库表结构正确创建
- 初始数据成功插入
- 日志不再显示SQL初始化错误
- 系统能够正常访问和使用