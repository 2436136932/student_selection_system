<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </div>
      </template>

      <div class="profile-content">
        <!-- 头像和基本信息 -->
        <div class="profile-basic">
          <div class="avatar-section">
            <el-avatar
              :size="120"
              :src="userInfo.avatar || defaultAvatar"
              class="user-avatar"
              @click="showAvatarUpload = true"
            >
              <el-icon v-if="!userInfo.avatar"><UserFilled /></el-icon>
            </el-avatar>
            <div class="avatar-text">点击更换头像</div>
          </div>

          <div class="info-section">
            <div class="info-item">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">真实姓名：</span>
              <span class="value">{{ userInfo.real_name || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">角色：</span>
              <el-tag :type="roleType">{{ userInfo.role || '-' }}</el-tag>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号：</span>
              <span class="value">{{ userInfo.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">状态：</span>
              <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
                {{ userInfo.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 修改信息卡片 -->
        <el-card class="modify-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <el-icon><EditPen /></el-icon>
              <span>修改个人信息</span>
            </div>
          </template>

          <el-form
            :model="form"
            label-position="top"
            :rules="rules"
            ref="formRef"
            class="modify-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入邮箱" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
            </el-row>



            <div class="form-actions">
              <el-button type="primary" @click="submitForm">保存修改</el-button>
              <el-button @click="resetForm">重置</el-button>
            </div>
          </el-form>
        </el-card>

        <!-- 修改密码卡片 -->
        <el-card class="modify-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <el-icon><Lock /></el-icon>
              <span>修改密码</span>
            </div>
          </template>

          <el-form
            :model="passwordForm"
            label-position="top"
            :rules="passwordRules"
            ref="passwordFormRef"
            class="modify-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
                </el-form-item>
              </el-col>
            </el-row>

            <div class="form-actions">
              <el-button type="primary" @click="submitPasswordForm">保存修改</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </div>
          </el-form>
        </el-card>
      </div>
    </el-card>

    <!-- 头像上传对话框 -->
    <el-dialog v-model="showAvatarUpload" title="更换头像" width="400px">
      <div class="avatar-upload-container">
        <!-- 自定义上传，不使用action -->
        <el-upload
          action=""
          :http-request="handleAvatarUpload"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          class="avatar-uploader"
        >
          <img v-if="previewImage" :src="previewImage" class="avatar-preview" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">点击上传头像，支持 JPG、PNG、GIF 格式，大小不超过 10MB</div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAvatarUpload = false">取消</el-button>
          <el-button type="primary" @click="confirmAvatarUpload">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, EditPen, Lock, UserFilled, Edit, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

// 默认头像
const defaultAvatar = 'https://picsum.photos/id/1005/200/200'

// 用户信息
const userInfo = ref({
  id: '',
  username: '',
  real_name: '',
  email: '',
  phone: '',
  role: '',
  status: 1,
  avatar: ''
})

// 表单数据
const formRef = ref(null)
const form = reactive({
  email: '',
  phone: ''
})

// 表单验证规则
const rules = reactive({
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
})

// 密码表单数据
const passwordFormRef = ref(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ]
})

// 头像上传相关
const showAvatarUpload = ref(false)
const previewImage = ref('')
const uploadedAvatarFile = ref(null)

// 角色类型颜色
const roleType = computed(() => {
  const roleMap = {
    'admin': 'danger',
    'teacher': 'warning',
    'student': 'success'
  }
  return roleMap[userInfo.value.role] || 'info'
})

// 获取用户信息
const getUserInfo = async () => {
  try {
    // 获取当前用户信息
    const response = await axios.get('/api/users/current')
    userInfo.value = response.data
    
    // 初始化表单数据
    form.email = userInfo.value.email || ''
    form.phone = userInfo.value.phone || ''
    
    // 设置头像预览
    if (userInfo.value.avatar) {
      previewImage.value = userInfo.value.avatar
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败：' + error.message)
  }
}

// 表单提交
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用API更新用户信息
        const response = await axios.put(`/api/users/${userInfo.value.id}`, {
          email: form.email,
          phone: form.phone
        })
        
        // 更新用户信息
        userInfo.value = response.data
        ElMessage.success('个人信息更新成功')
      } catch (error) {
        ElMessage.error('更新失败：' + error.message)
      }
    } else {
      return false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  
  // 恢复原始数据
  form.email = userInfo.value.email || ''
  form.phone = userInfo.value.phone || ''
}

// 密码表单提交
const submitPasswordForm = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 调用API修改密码
        await axios.put('/api/users/change-password', {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        ElMessage.success('密码修改成功')
        resetPasswordForm()
      } catch (error) {
        ElMessage.error('密码修改失败：' + error.message)
      }
    } else {
      return false
    }
  })
}

// 重置密码表单
const resetPasswordForm = () => {
  if (!passwordFormRef.value) return
  passwordFormRef.value.resetFields()
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10
  
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  
  // 生成预览图
  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
  }
  reader.readAsDataURL(file)
  
  uploadedAvatarFile.value = file
  return false  // 返回false，使用自定义上传
}

// 处理头像上传
const handleAvatarUpload = (file) => {
  // 自定义上传已在beforeAvatarUpload中处理，此函数仅为满足接口要求
}

// 确认头像上传
const confirmAvatarUpload = async () => {
  if (!uploadedAvatarFile.value) {
    ElMessage.warning('请选择要上传的头像')
    return
  }
  
  try {
    const formData = new FormData()
    formData.append('file', uploadedAvatarFile.value)
    
    // 调用API上传头像
    const response = await axios.post('/api/users/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    // 更新用户头像
    userInfo.value.avatar = response.data.avatar
    showAvatarUpload.value = false
    ElMessage.success('头像上传成功')
  } catch (error) {
    ElMessage.error('头像上传失败：' + error.message)
  }
}

// 初始化
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px;
  min-height: 80vh;
}

.profile-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409eff;
}

.profile-basic {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.avatar-text {
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.info-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  min-width: 600px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 16px;
  line-height: 1.8;
}

.info-item .label {
  width: 80px;
  color: #606266;
  font-weight: 500;
}

.info-item .value {
  color: #303133;
  font-weight: 500;
}

.modify-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.modify-form {
  padding: 10px 0;
}

.form-actions {
  display: flex;
  justify-content: flex-start;
  margin-top: 20px;
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 200px;
  text-align: center;
  line-height: 200px;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.upload-tip {
  margin-top: 15px;
  color: #909399;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-basic {
    flex-direction: column;
    align-items: center;
  }
  
  .info-section {
    grid-template-columns: 1fr;
    min-width: auto;
    width: 100%;
  }
  
  .info-item {
    justify-content: center;
  }
  
  .modify-form {
    padding: 0 10px;
  }
  
  .form-actions {
    justify-content: center;
  }
}
</style>