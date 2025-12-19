## 问题分析

当前学生奖励申请材料上传存在问题，主要表现为"材料路径为空，可能无法保存材料信息"警告，导致材料无法保存到数据库。现有的实现使用原生input file结合axios直接上传，存在异步处理和状态管理的复杂性。

## 解决方案

使用Element Plus Upload组件替换原生input file，利用其完善的文件上传功能和Vue 3集成优势，简化上传流程并确保材料信息正确保存。

## 实现步骤

### 1. 修改前端组件结构

* 将原生input file替换为Element Plus Upload组件

* 配置Upload组件的自定义上传功能

* 保留现有的上传URL和后端接口

### 2. 优化上传逻辑

* 使用`before-upload`钩子函数处理文件上传前的验证

* 实现自定义上传方法，使用axios进行文件上传

* 使用`on-success`和`on-error`钩子函数处理上传结果

### 3. 确保材料信息正确更新

* 在上传成功后更新materialInfo对象

* 增强上传过程中的状态管理

* 确保materialInfo在提交申请前已经正确填充

### 4. 保留现有功能

* 保留文件类型和大小验证

* 保留上传进度显示

* 保留文件选择和移除功能

* 保留提交申请时的材料信息验证

## 代码修改要点

### 模板部分

```vue
<!-- 替换原生input file -->
<el-upload
  ref="uploadRef"
  class="custom-upload"
  :auto-upload="false"
  :before-upload="beforeUpload"
  :on-success="handleUploadSuccess"
  :on-error="handleUploadError"
  :file-list="fileList"
  :limit="1"
  :on-exceed="handleExceed"
  accept=".doc,.docx,.pdf,.ppt,.pptx,.png,.jpg,.jpeg"
>
  <template #trigger>
    <el-button type="primary" :disabled="uploading">
      <el-icon><Upload /></el-icon> {{ fileList.length > 0 ? selectedFileName : '选择文件' }}
    </el-button>
  </template>
  <template #tip>
    <div class="el-upload__tip">
      支持上传 Word、PDF、PPT、PNG、JPG 格式的文件，最大10MB
    </div>
  </template>
</el-upload>
```

### 脚本部分

```javascript
// 新增Upload组件相关变量
const uploadRef = ref(null)
const fileList = ref([])

// 上传前验证
const beforeUpload = (file) => {
  // 保留现有文件验证逻辑
  return validateFile(file)
}

// 自定义上传方法（复用现有逻辑）
const handleUpload = () => {
  // 复用现有uploadFile方法的逻辑
}

// 上传成功处理
const handleUploadSuccess = (response) => {
  // 更新materialInfo对象
  materialInfo.materialPath = response.materialPath || ''
  materialInfo.materialName = response.materialName || ''
  materialInfo.materialSize = response.materialSize || 0
  materialInfo.materialType = response.materialType || ''
}

// 上传失败处理
const handleUploadError = (error) => {
  // 处理上传错误
}
```

## 预期效果

* 简化文件上传流程，减少异步处理复杂性

* 利用Element Plus Upload组件的完善功能，提供更好的用户体验

* 确保材料信息正确保存到materialInfo对象

* 避免"材料路径为空"警告，确保申请材料成功保存到数据库

## 验证方法

1. 启动前端和后端服务
2. 登录系统，进入学生奖项申请页面
3. 点击"申请奖项"按钮
4. 填写申请信息并选择文件上传
5. 提交申请，验证申请是否成功保存
6. 检查数据库中是否包含完整的材料信息
7. 检查申请列表中是否可以预览和下载材料

