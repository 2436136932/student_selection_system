# 修复材料预览功能的403 Forbidden错误

## 问题分析
经过分析，403错误的根本原因是：

1. **请求路径问题**：错误信息显示请求被发送到了前端服务器 `http://localhost:5173`，而不是后端服务器 `http://localhost:8080`
2. **代理配置问题**：尽管 `vite.config.js` 配置了 `/api` 代理，但预览请求没有被正确转发到后端
3. **axios baseURL 未生效**：axios 的 baseURL 设置可能没有被正确应用到请求中

## 修复方案

### 1. 修复 axios 请求配置
修改 `MaterialPreview.vue` 中的 `loadFileContent` 函数，确保 axios 请求使用正确的 URL：

- 确保请求 URL 包含完整的后端地址
- 或确保相对路径能被正确代理到后端

### 2. 检查并优化 vite 代理配置
确认 `vite.config.js` 中的代理配置正确，能够将 `/api` 请求转发到后端

### 3. 修复前端预览 URL 生成逻辑
修改 `StudentAwardApplicationView.vue` 中的 `previewMaterial` 函数，确保生成的预览 URL 格式正确

## 修复步骤

1. **修改 `MaterialPreview.vue`**：
   - 在 `loadFileContent` 函数中，确保使用完整的后端 URL 或正确的相对路径
   - 添加更多调试日志，便于排查问题

2. **修改 `StudentAwardApplicationView.vue`**：
   - 在 `previewMaterial` 函数中，确保生成的预览 URL 格式正确
   - 添加调试日志，便于查看生成的 URL

3. **测试修复效果**：
   - 重新启动前后端服务
   - 测试预览功能是否正常工作
   - 检查浏览器控制台的日志信息

4. **进一步优化（如果需要）**：
   - 如果问题仍然存在，检查 vite 代理配置
   - 检查 axios 拦截器的实现
   - 检查后端的认证配置

## 预期效果
修复后，预览请求将被正确发送到后端服务器 `http://localhost:8080`，而不是前端服务器 `http://localhost:5173`，从而避免 403 Forbidden 错误。

## 代码修改点

1. **`MaterialPreview.vue`**：修改 `loadFileContent` 函数，确保使用正确的 URL
2. **`StudentAwardApplicationView.vue`**：修改 `previewMaterial` 函数，确保生成正确的 URL

这个修复方案将解决预览功能的 403 Forbidden 错误，使学生和教师能够正常预览申请材料。