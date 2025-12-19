# 修复材料预览功能的403 Forbidden错误

## 问题分析
经过分析，我发现403错误的根本原因是：

1. **请求路径问题**：错误信息显示请求被发送到了前端服务器 `http://localhost:5173`，而不是后端服务器 `http://localhost:8080`
2. **代理配置问题**：尽管 `vite.config.js` 配置了 `/api` 代理，但预览请求没有被正确转发到后端
3. **认证信息问题**：如果请求没有正确携带 JWT 令牌，后端会返回 403 Forbidden

## 修复方案

### 1. 修复前端axios请求配置
修改 `MaterialPreview.vue` 中的 `loadFileContent` 函数，确保 axios 请求正确使用 baseURL 或代理配置：

- 确保请求路径以 `/api` 开头
- 确保 axios 拦截器能够正确处理请求
- 添加调试信息，便于排查问题

### 2. 检查并优化代理配置
确认 `vite.config.js` 中的代理配置正确，能够将 `/api` 请求转发到后端

### 3. 增强错误处理和调试信息
- 在前端添加更多调试日志，便于查看请求的完整URL和头信息
- 在后端添加更详细的日志，便于查看认证失败的具体原因

## 修复步骤

1. **修改 `MaterialPreview.vue`**：
   - 在 `loadFileContent` 函数中添加调试日志
   - 确保请求使用正确的URL格式
   - 增强错误处理，显示更详细的错误信息

2. **测试修复效果**：
   - 重新启动前后端服务
   - 测试预览功能是否正常工作
   - 检查浏览器控制台的日志信息

3. **进一步优化（如果需要）**：
   - 如果问题仍然存在，检查 JWT 令牌是否正确存储和发送
   - 检查后端的 `@PreAuthorize` 注解配置
   - 检查 `JwtAuthenticationFilter` 的实现

## 预期效果
修复后，预览请求将被正确发送到后端服务器，并携带有效的 JWT 令牌，后端将返回文件内容而不是 403 Forbidden 错误。

## 代码修改点

1. **`MaterialPreview.vue`**：修改 `loadFileContent` 函数，添加调试日志和优化请求配置
2. **`StudentAwardApplicationController.java`**：添加更详细的认证失败日志（可选）

这个修复方案将解决预览功能的 403 Forbidden 错误，使学生和教师能够正常预览申请材料。