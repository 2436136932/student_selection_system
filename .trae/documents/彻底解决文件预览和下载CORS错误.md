### 问题分析

错误信息显示：`Access to fetch at 'https://dsp.lenovo.com.cn/lenovo/bid?clientType=api&positionId=50296' from origin 'null' has been blocked by CORS policy`

**关键发现**：

1. 我们的应用代码中没有直接引用这个URL
2. 这是一个外部注入的请求（可能来自浏览器扩展、广告脚本等）
3. 它正在干扰我们的文件预览和下载功能

### 解决方案设计

1. **为iframe添加sandbox属性**：限制iframe的权限，防止它执行恶意脚本或发出不必要的请求
2. **优化预览URL构建**：确保预览URL始终使用正确的协议和源
3. **为MaterialPreview组件添加CORS处理**：忽略外部请求，只关注我们自己的API
4. **增强后端CORS配置**：确保后端正确处理来自前端的请求
5. **添加拦截器**：在前端添加axios拦截器，过滤掉外部请求

### 实现步骤

1. **修改MaterialPreview组件**：

   * 为iframe添加sandbox属性

   * 优化预览URL处理

   * 添加请求过滤逻辑

2. **增强后端CORS配置**：

   * 在Spring Boot中添加全局CORS配置

   * 确保所有API端点允许跨域请求

3. **添加axios拦截器**：

   * 在前端添加拦截器，只处理我们自己的API请求

   * 过滤掉所有外部请求

### 代码修改点

1. **修改iframe标签**：

   ```vue
   <iframe
     :src="previewUrl"
     frameborder="0"
     sandbox="allow
   ```

