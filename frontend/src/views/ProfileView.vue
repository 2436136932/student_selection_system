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
          <div class="avatar-section" @click="showAvatarUpload = true">
            <el-avatar
              :size="120"
              :src="userInfo.avatar"
              class="user-avatar"
              @error="handleAvatarError"
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
            
            <!-- 教师额外信息 -->
            <div v-if="userInfo.role === 'teacher'" class="info-item">
              <span class="label">工号：</span>
              <span class="value">{{ teacherInfo?.teacherNumber || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'teacher'" class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ teacherInfo?.gender || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'teacher'" class="info-item">
              <span class="label">职称：</span>
              <span class="value">{{ teacherInfo?.title || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'teacher'" class="info-item">
              <span class="label">所属部门：</span>
              <span class="value">{{ teacherInfo?.department || '-' }}</span>
            </div>
            
            <!-- 学生额外信息 -->
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">学号：</span>
              <span class="value">{{ studentInfo?.studentNumber || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ studentInfo?.gender || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">出生日期：</span>
              <span class="value">{{ studentInfo?.birthDate || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">专业：</span>
              <span class="value">{{ studentInfo?.major || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">班级：</span>
              <span class="value">{{ studentInfo?.className || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">入学年份：</span>
              <span class="value">{{ studentInfo?.admissionYear || '-' }}</span>
            </div>
            <div v-if="userInfo.role === 'student'" class="info-item">
              <span class="label">在读状态：</span>
              <el-tag :type="studentInfo?.status === 1 ? 'success' : 'info'">
                {{ studentInfo?.status === 1 ? '在读' : '毕业' }}
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
        <!-- 自定义上传 -->
        <el-upload
          action=""
          :http-request="handleAvatarUpload"
          :show-file-list="false"
          :auto-upload="false"
          @change="handleFileChange"
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

// 默认头像 - 使用Element Plus内置默认头像，不再依赖外部链接
const defaultAvatar = ''

// 用户信息
const userInfo = ref({
  id: '',
  username: '',
  real_name: '',
  email: '',
  phone: '',
  role: '',
  status: 1,
  avatar: null
})

// 教师详细信息
const teacherInfo = ref(null)

// 学生详细信息
const studentInfo = ref(null)

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

// 头像加载错误处理
const handleAvatarError = (error) => {
  console.error('=== 头像加载失败详情 ===')
  console.error('错误事件：', error)
  console.error('当前头像URL：', userInfo.value.avatar)
  console.error('浏览器完整URL：', window.location.href)
  
  // 仅显示警告，不再将头像URL设为null，便于调试
  ElMessage.warning('头像加载失败，显示默认头像')
}

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
    const userData = response.data
    
    // 转换头像URL为完整URL
    if (userData.avatar && userData.avatar.startsWith('/')) {
      userData.avatar = `http://localhost:8080${userData.avatar}`
    }
    
    userInfo.value = userData
    
    // 初始化表单数据
    form.email = userInfo.value.email || ''
    form.phone = userInfo.value.phone || ''
    
    // 设置头像预览
    if (userInfo.value.avatar) {
      previewImage.value = userInfo.value.avatar
    }
    
    // 根据用户角色获取额外信息
    if (userData.role === 'teacher') {
      // 获取教师详细信息 - 根据user_id查询，而不是id
      const teacherResponse = await axios.get(`/api/teachers/user/${userData.id}`)
      teacherInfo.value = teacherResponse.data
    } else if (userData.role === 'student') {
      // 获取学生详细信息 - 根据user_id查询，而不是id
      const studentResponse = await axios.get(`/api/students/user/${userData.id}`)
      studentInfo.value = studentResponse.data
    }
    
    // 调试信息
    console.log('获取用户信息成功，头像URL：', userInfo.value.avatar)
  } catch (error) {
    ElMessage.error('获取用户信息失败：' + error.message)
    console.error('获取用户信息失败：', error)
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

// 处理文件选择变化
const handleFileChange = (file) => {
  const rawFile = file.raw
  if (rawFile) {
    // 验证文件类型和大小
    const isImage = rawFile.type.startsWith('image/')
    const isLt10M = rawFile.size / 1024 / 1024 < 10
    
    if (!isImage) {
      ElMessage.error('请上传图片文件')
      return
    }
    if (!isLt10M) {
      ElMessage.error('图片大小不能超过 10MB')
      return
    }
    
    // 生成预览图
    const reader = new FileReader()
    reader.onload = (e) => {
      previewImage.value = e.target.result
    }
    reader.readAsDataURL(rawFile)
    
    uploadedAvatarFile.value = rawFile
  }
}

// 处理头像上传（仅为满足接口要求）
const handleAvatarUpload = (file) => {
  // 自定义上传已在handleFileChange中处理，此函数仅为满足接口要求
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
    
    // 获取头像URL
    let avatarUrl = response.data.avatar
    
    // 转换为完整URL（添加后端域名和端口）
    if (avatarUrl && avatarUrl.startsWith('/')) {
      // 后端服务运行在8080端口
      avatarUrl = `http://localhost:8080${avatarUrl}`
    }
    
    // 更新用户头像
    userInfo.value.avatar = avatarUrl
    
    // 同步更新预览图
    previewImage.value = avatarUrl
    
    // 同步更新localStorage中的用户信息，确保右上角头像同步显示
    const currentUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    localStorage.setItem('userInfo', JSON.stringify({
      ...currentUserInfo,
      avatar: avatarUrl
    }))
    
    showAvatarUpload.value = false
    ElMessage.success('头像上传成功')
    
    // 增强调试信息
    console.log('上传成功，原始头像URL：', response.data.avatar)
    console.log('转换后完整头像URL：', avatarUrl)
    console.log('头像访问测试：', avatarUrl)
    
    // 验证头像URL是否可访问
    try {
      const img = new Image()
      img.onload = () => console.log('头像URL访问成功')
      img.onerror = () => console.error('头像URL访问失败')
      img.src = avatarUrl
    } catch (imgError) {
      console.error('头像URL验证失败：', imgError)
    }
  } catch (error) {
    ElMessage.error('头像上传失败：' + error.message)
    console.error('头像上传失败：', error)
    console.error('错误详情：', error.response?.data || error.message)
  }
}

// 初始化
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600;700&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

.profile-container {
  padding: 0;
  min-height: 80vh;
}

.profile-card {
  border-radius: 20px;
  border: 1px solid rgba(15, 76, 129, 0.08);
  box-shadow: 
    0 4px 24px rgba(15, 76, 129, 0.06),
    0 0 0 1px rgba(255, 255, 255, 0.8);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.profile-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-bottom: 1px solid rgba(15, 76, 129, 0.08);
  padding: 20px 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-size: 18px;
  font-weight: 600;
  color: #1A1A2E;
}

.card-header::before {
  content: "";
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #D4AF37, #E8B96B);
  border-radius: 2px;
}

.card-header .el-icon {
  font-size: 20px;
  color: #1A6B9C;
}

.profile-basic {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  padding: 32px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  position: relative;
}

.user-avatar {
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 4px solid #FAF8F5;
  box-shadow: 
    0 8px 32px rgba(15, 76, 129, 0.15),
    0 0 0 4px rgba(212, 175, 55, 0.2);
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
}

.user-avatar:hover {
  transform: scale(1.08);
  box-shadow: 
    0 12px 40px rgba(15, 76, 129, 0.2),
    0 0 0 6px rgba(212, 175, 55, 0.35);
}

.avatar-text {
  margin-top: 16px;
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  color: #718096;
  font-size: 13px;
  font-weight: 500;
  padding: 6px 16px;
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-radius: 20px;
  border: 1px solid rgba(15, 76, 129, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.avatar-section:hover .avatar-text {
  background: linear-gradient(135deg, #0F4C81 0%, #1A6B9C 100%);
  color: white;
  border-color: transparent;
}

.info-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  min-width: 600px;
  flex: 1;
}

.info-item {
  display: flex;
  align-items: center;
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 15px;
  line-height: 1.8;
  padding: 12px 16px;
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-radius: 12px;
  border: 1px solid rgba(15, 76, 129, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.info-item:hover {
  border-color: rgba(15, 76, 129, 0.12);
  box-shadow: 0 4px 12px rgba(15, 76, 129, 0.06);
  transform: translateX(4px);
}

.info-item .label {
  width: 85px;
  color: #718096;
  font-weight: 500;
}

.info-item .value {
  color: #1A1A2E;
  font-weight: 600;
}

.info-item :deep(.el-tag) {
  border-radius: 20px;
  font-weight: 500;
  padding: 4px 12px;
  border: none;
}

.info-item :deep(.el-tag--danger) {
  background: linear-gradient(135deg, #C44536 0%, #D65A4A 100%);
  color: white;
}

.info-item :deep(.el-tag--warning) {
  background: linear-gradient(135deg, #D4953A 0%, #E8AB52 100%);
  color: white;
}

.info-item :deep(.el-tag--success) {
  background: linear-gradient(135deg, #2D8B5E 0%, #3DA76F 100%);
  color: white;
}

.info-item :deep(.el-tag--info) {
  background: linear-gradient(135deg, #1A6B9C 0%, #3D8EB9 100%);
  color: white;
}

.modify-card {
  border-radius: 16px;
  border: 1px solid rgba(15, 76, 129, 0.08);
  box-shadow: 
    0 4px 24px rgba(15, 76, 129, 0.06),
    0 0 0 1px rgba(255, 255, 255, 0.8);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  margin-top: 24px !important;
}

.modify-card :deep(.el-card__header) {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-bottom: 1px solid rgba(15, 76, 129, 0.08);
  padding: 16px 20px;
}

.modify-form {
  padding: 16px 0;
}

.modify-form :deep(.el-form-item__label) {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-weight: 500;
  color: #1A1A2E;
}

.modify-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(15, 76, 129, 0.06);
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
}

.modify-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(15, 76, 129, 0.1);
}

.modify-form :deep(.el-input__wrapper.is-focus) {
  border-color: #1A6B9C;
  box-shadow: 0 4px 16px rgba(15, 76, 129, 0.12);
  background: white;
}

.form-actions {
  display: flex;
  justify-content: flex-start;
  gap: 12px;
  margin-top: 24px;
}

.form-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #0F4C81 0%, #1A6B9C 100%);
  border: none;
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 28px;
  box-shadow: 0 4px 16px rgba(15, 76, 129, 0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-actions :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #1A6B9C 0%, #3D8EB9 100%);
  box-shadow: 0 6px 20px rgba(15, 76, 129, 0.35);
  transform: translateY(-2px);
}

.form-actions :deep(.el-button--default) {
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 28px;
  border: 2px solid rgba(15, 76, 129, 0.2);
  color: #1A6B9C;
  background: transparent;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-actions :deep(.el-button--default:hover) {
  background: rgba(15, 76, 129, 0.06);
  border-color: #1A6B9C;
  transform: translateY(-2px);
}

/* 头像上传对话框 */
:deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 16px 64px rgba(15, 76, 129, 0.2);
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-bottom: 1px solid rgba(15, 76, 129, 0.1);
  padding: 20px 24px;
}

:deep(.el-dialog__title) {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-weight: 600;
  color: #1A1A2E;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 0;
}

.avatar-uploader {
  border: 2px dashed rgba(15, 76, 129, 0.2);
  border-radius: 16px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
}

.avatar-uploader:hover {
  border-color: #D4AF37;
  background: linear-gradient(135deg, #F5F2ED 0%, #EBE6DE 100%);
  box-shadow: 0 8px 24px rgba(15, 76, 129, 0.1);
}

.avatar-uploader-icon {
  font-size: 48px;
  color: #718096;
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-preview {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.upload-tip {
  margin-top: 20px;
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  color: #718096;
  font-size: 13px;
  text-align: center;
  padding: 10px 20px;
  background: linear-gradient(135deg, #FAF8F5 0%, #F5F2ED 100%);
  border-radius: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.dialog-footer :deep(.el-button--primary) {
  background: linear-gradient(135deg, #0F4C81 0%, #1A6B9C 100%);
  border: none;
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 28px;
  box-shadow: 0 4px 16px rgba(15, 76, 129, 0.25);
}

.dialog-footer :deep(.el-button--default) {
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 28px;
  border: 2px solid rgba(15, 76, 129, 0.2);
  color: #1A6B9C;
}

/* 响应式设计 */
@media (max-width: 900px) {
  .profile-basic {
    flex-direction: column;
    align-items: center;
    gap: 32px;
  }
  
  .info-section {
    grid-template-columns: 1fr;
    min-width: auto;
    width: 100%;
  }
  
  .info-item {
    justify-content: flex-start;
  }
  
  .modify-form {
    padding: 0;
  }
  
  .form-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-item .label {
    width: auto;
  }
}
</style>