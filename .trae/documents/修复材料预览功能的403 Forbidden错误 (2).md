# 修复材料预览功能的403 Forbidden错误

## 问题分析
经过详细分析，我发现403错误的根本原因是：

1. **请求路径问题**：错误信息显示请求被发送到了前端服务器 `http://localhost:5173`，而不是后端服务器 `http://localhost:8080`
2. **组件渲染顺序问题**：当 `MaterialPreview` 组件可见时，`el-image` 和 `iframe` 会立即尝试加载 `blobUrl`，但此时 `blobUrl` 可能为空
3. **错误处理问题**：当 `loadFileContent` 函数失败时，`blobUrl` 会被清理，但组件仍会渲染 `el-image` 和 `iframe`，导致它们尝试加载无效的URL

## 修复方案

### 1. 修复组件渲染逻辑
修改 `MaterialPreview.vue` 组件，确保只有当 `blobUrl` 有效时，才渲染 `el-image` 和 `iframe`：

- 添加 `blobUrl` 有效性检查
- 确保在 `loadFileContent` 函数失败时，不渲染可能导致无效请求的组件
- 增强错误状态管理

### 2. 修复URL处理逻辑
确保所有请求都使用完整的后端URL，避免依赖代理配置：

- 在 `StudentAwardApplicationView` 中生成完整的后端URL
- 在 `MaterialPreview` 组件中验证并修复URL格式
- 添加更详细的调试日志

### 3. 优化错误处理
- 当 `loadFileContent` 函数失败时，显示友好的错误信息，而不是尝试渲染无效的组件
- 添加重试机制，允许用户重新尝试加载文件

## 修复步骤

1. **修改 `MaterialPreview.vue`**：
   - 在模板中添加 `blobUrl` 有效性检查
   - 增强错误状态管理
   - 添加重试按钮

2. **修改 `StudentAwardApplicationView.vue`**：
   - 生成完整的后端URL，而不是相对路径
   - 添加调试日志

3. **测试修复效果**：
   - 重新启动前后端服务
   - 测试预览功能是否正常工作
   - 检查浏览器控制台的日志信息

4. **进一步优化（如果需要）**：
   - 如果问题仍然存在，检查 axios 配置
   - 检查后端认证配置
   - 检查网络请求流程

## 预期效果
修复后，预览请求将被正确发送到后端服务器 `http://localhost:8080`，而不是前端服务器 `http://localhost:5173`，从而避免 403 Forbidden 错误。当 `loadFileContent` 函数失败时，组件将显示友好的错误信息，而不是尝试渲染无效的组件。

## 代码修改点

1. **`MaterialPreview.vue`**：
   - 修改模板，添加 `blobUrl` 有效性检查
   - 增强错误状态管理
   - 添加重试按钮

2. **`StudentAwardApplicationView.vue`**：
   - 修改 `previewMaterial` 函数，生成完整的后端URL

这个修复方案将解决预览功能的 403 Forbidden 错误，使学生和教师能够正常预览申请材料。