### 问题分析

文件预览时出现CORS错误：`Access to fetch at 'https://dsp.lenovo.com.cn/lenovo/bid?clientType=api&positionId=50296' from origin 'null' has been blocked by CORS policy`

**原因**：
1. 虽然该请求不是我们的应用代码直接发出的，但它干扰了预览功能
2. 可能是浏览器扩展或第三方脚本注入的广告请求
3. 我们的后端API可能缺少正确的CORS配置

### 解决方案设计

1. **添加CORS配置**：在后端添加跨域资源共享配置，允许来自前端的请求
2. **优化MaterialPreview组件**：提高组件的鲁棒性，确保在第三方脚本出错时仍能正常工作
3. **确保后端API正确处理预览请求**：确保预览和下载API返回正确的CORS头

### 实现步骤

1. **修改后端代码**：
   - 在`StudentAwardApplicationController`中添加CORS配置
   - 确保`previewMaterial`和`downloadMaterial`方法允许跨域请求

2. **优化MaterialPreview组件**：
   - 添加错误处理，确保组件在第三方请求失败时仍能正常工作
   - 优化iframe预览，确保PDF等文件能正确加载

3. **测试功能**：
   - 上传新文件并提交申请
   - 测试预览和下载功能
   - 验证CORS错误是否已解决

### 代码修改点

1. **后端CORS配置**：
   ```java
   // 在控制器类上添加CORS注解
   @CrossOrigin(origins = "http://localhost:5173")
   ```

2. **优化MaterialPreview组件**：
   ```vue
   // 为iframe添加错误处理
   <iframe
     :src="previewUrl"
     frameborder="0"
     style="width: 100%; height: 70vh;"
     @error="handleIframeError"
   ></iframe>
   ```

### 预期效果

- 解决文件预览CORS错误
- 提高MaterialPreview组件的鲁棒性
- 确保预览和下载功能正常工作
- 不受第三方脚本请求的影响

### 测试建议

1. 上传新文件并提交申请
2. 测试图片预览功能
3. 测试PDF预览功能
4. 测试文档预览功能
5. 验证下载功能
6. 检查浏览器控制台是否还有CORS错误