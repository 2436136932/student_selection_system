# 前后端一体化打包部署计划

## 项目分析

- **后端**: Spring Boot 3.1.5 + Java 21，可打包为可执行 JAR
- **前端**: Vue 3 + Vite，构建为静态资源
- **部署方式**: 将前端静态资源集成到 Spring Boot JAR 中，实现单文件部署

---

## 实施步骤

### 步骤 1: 配置前端构建输出目录

修改 `vite.config.js`，将前端构建输出到后端的静态资源目录：

```javascript
export default defineConfig({
  // ... 其他配置
  build: {
    outDir: '../src/main/resources/static',  // 输出到 Spring Boot 静态资源目录
    emptyOutDir: true  // 构建前清空目录
  }
})
```

### 步骤 2: 配置 Spring Boot 静态资源处理

确保 `WebMvcConfig.java` 正确配置静态资源路径，支持 SPA 路由。

### 步骤 3: 配置前端 API 请求

修改前端 `main.js` 和 `LoginView.vue`，在生产环境中使用相对路径访问 API：

```javascript
const getBaseURL = () => {
  if (import.meta.env.PROD) {
    return ''  // 生产环境使用相对路径
  }
  const hostname = window.location.hostname
  return `http://${hostname}:8080`
}
```

### 步骤 4: 创建打包脚本

创建一键打包脚本，自动化前后端构建流程：

**Windows (build.bat)**:
```batch
@echo off
echo Building frontend...
cd frontend
call npm install
call npm run build
cd ..
echo Building backend...
call mvn clean package -DskipTests
echo Build complete! JAR file is in target folder.
pause
```

### 步骤 5: 配置生产环境数据库

创建 `application-prod.yml` 生产环境配置文件，配置服务器数据库连接。

### 步骤 6: 服务器部署

将 JAR 文件上传到服务器，使用 systemd 或 nohup 运行。

---

## 文件修改清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `frontend/vite.config.js` | 修改 | 添加 build 配置 |
| `frontend/src/main.js` | 修改 | 生产环境 API 配置 |
| `frontend/src/views/LoginView.vue` | 修改 | 生产环境 API 配置 |
| `src/main/resources/application-prod.yml` | 新建 | 生产环境配置 |
| `build.bat` | 新建 | 一键打包脚本 |
| `WebMvcConfig.java` | 检查 | 确保 SPA 路由支持 |

---

## 部署后访问

部署完成后，只需访问 `http://服务器IP:8080`，前后端一体化运行。

---

## 预期结果

- 生成单个 `student-selection-system-0.0.1-SNAPSHOT.jar` 文件
- 包含前端静态资源和后端代码
- 部署简单，只需一个 JAR 文件和一个数据库
