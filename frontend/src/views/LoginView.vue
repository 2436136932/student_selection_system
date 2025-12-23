<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
      <div class="bg-circle circle-4"></div>
    </div>
    
    <div class="login-box">
      <div class="login-header">
        <!-- 大学风格的标志 -->
        <div class="university-logo">
          <svg width="60" height="60" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M50 10L90 90H10L50 10Z" fill="#2c3e50"/>
            <path d="M50 30L70 70H30L50 30Z" fill="#3498db"/>
            <path d="M50 50L60 60H40L50 50Z" fill="#ffffff"/>
            <path d="M30 20L40 30H60L70 20" stroke="#2c3e50" stroke-width="4" fill="none"/>
            <path d="M30 80L40 70H60L70 80" stroke="#2c3e50" stroke-width="4" fill="none"/>
          </svg>
        </div>
        <h1>学生评奖评优系统</h1>
        <p class="subtitle">University Student Evaluation & Award System</p>
        <p class="role-guide">请选择您的角色登录或注册</p>
      </div>
      
      <div class="role-selector">
        <el-radio-group v-model="selectedRole" class="role-group">
          <el-radio-button label="admin" class="role-button">
            <span class="role-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </span>
            管理员
          </el-radio-button>
          <el-radio-button label="teacher" class="role-button">
            <span class="role-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                <path d="M20 21V19c0-2.76-4-5-9-5s-9 2.24-9 5v2h18zM7.5 8.25h9v2.62h-9zM12 2C9.24 2 7 4.24 7 7s2.24 5 5 5 5-2.24 5-5-2.24-5-5-5z"/>
              </svg>
            </span>
            教师
          </el-radio-button>
          <el-radio-button label="student" class="role-button">
            <span class="role-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
            </span>
            学生
          </el-radio-button>
        </el-radio-group>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="用户名"
                prefix-icon="el-icon-user"
                size="large"
              ></el-input>
            </el-form-item>
            
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                prefix-icon="el-icon-lock"
                show-password
                size="large"
              ></el-input>
            </el-form-item>
            
            <el-form-item>
              <button @click="handleLogin" :disabled="loginLoading" class="custom-login-btn">
                {{ loginLoading ? '登录中...' : '登录' }}
              </button>
            </el-form-item>
            
            <div class="forgot-password-container">
              <el-link type="primary" :underline="false" @click="openForgotPasswordDialog">忘记密码？</el-link>
            </div>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="login-form">
            <div class="form-row">
              <el-form-item prop="username" class="form-col">
                <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                  prefix-icon="el-icon-user"
                  size="large"
                ></el-input>
              </el-form-item>
              
              <el-form-item prop="name" class="form-col">
                <el-input
                  v-model="registerForm.name"
                  placeholder="真实姓名"
                  prefix-icon="el-icon-user"
                  size="large"
                ></el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item prop="password" class="form-col">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="密码"
                  prefix-icon="el-icon-lock"
                  show-password
                  size="large"
                ></el-input>
              </el-form-item>
              
              <el-form-item prop="confirmPassword" class="form-col">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="确认密码"
                  prefix-icon="el-icon-lock"
                  show-password
                  size="large"
                ></el-input>
              </el-form-item>
            </div>
            
            <div class="form-row">
              <el-form-item prop="email" class="form-col">
                <el-input
                  v-model="registerForm.email"
                  placeholder="邮箱"
                  prefix-icon="el-icon-message"
                  size="large"
                ></el-input>
              </el-form-item>
              
              <el-form-item prop="phone" class="form-col">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="联系电话"
                  prefix-icon="el-icon-phone"
                  size="large"
                ></el-input>
              </el-form-item>
            </div>
            
            <el-form-item>
              <button @click="handleRegister" :disabled="registerLoading" class="custom-register-btn">
                {{ registerLoading ? '注册中...' : '注册' }}
              </button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="forgotPasswordVisible"
      title="重置密码"
      width="400px"
      center
      @close="closeForgotPasswordDialog"
    >

      <el-form :model="forgotPasswordForm" :rules="forgotPasswordRules" ref="forgotPasswordFormRef" class="forgot-password-form">
        <el-form-item prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            placeholder="请输入注册邮箱"
            prefix-icon="el-icon-message"
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="code">
          <el-input
            v-model="forgotPasswordForm.code"
            placeholder="请输入验证码"
            prefix-icon="el-icon-document"
            size="large"
          >
            <template #append>
              <el-button
                type="primary"
                :disabled="codeSending || countdown > 0"
                @click="sendResetCode"
                class="custom-send-code-btn"
              >
                {{ codeSending ? '发送中...' : countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <el-input
            v-model="forgotPasswordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            prefix-icon="el-icon-lock"
            show-password
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="forgotPasswordForm.confirmPassword"
            type="password"
            placeholder="请确认新密码"
            prefix-icon="el-icon-lock"
            show-password
            size="large"
          ></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeForgotPasswordDialog">取消</el-button>
          <el-button type="primary" @click="resetPassword" :loading="resetPasswordLoading">重置密码</el-button>
        </div>
      </template>
    </el-dialog>
    
    <div class="login-footer">
      <div class="footer-content">
        <p class="copyright">© 2026 学生评奖评优系统 - 版权所有：俞伟杰</p>
        <div class="footer-links">
          <a href="#" class="footer-link">隐私政策</a>
          <span class="footer-divider">|</span>
          <a href="#" class="footer-link">服务条款</a>
          <span class="footer-divider">|</span>
          <a href="#" class="footer-link">联系我们</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

// 设置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080'

const router = useRouter()

// 角色选择
const selectedRole = ref('student')

// 标签页切换
const activeTab = ref('login')

// 登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  email: '',
  phone: ''
})

// 加载状态
const loginLoading = ref(false)
const registerLoading = ref(false)
const codeSending = ref(false)
const resetPasswordLoading = ref(false)
const countdown = ref(0)

// 倒计时定时器
let countdownTimer = null

// 监听codeSending和countdown状态变化
watch([codeSending, countdown], (newVal, oldVal) => {
  // 使用.value访问ref变量的值
  console.log('状态变化监听:', {
    newCodeSending: newVal[0],
    newCountdown: newVal[1],
    oldCodeSending: oldVal[0],
    oldCountdown: oldVal[1]
  })
})

// 表单引用
const loginFormRef = ref()
const registerFormRef = ref()
const forgotPasswordFormRef = ref()

// 忘记密码相关数据
const forgotPasswordVisible = ref(false)
const forgotPasswordForm = reactive({
  email: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

// 登录表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
})

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 注册表单验证规则
const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ]
})

// 忘记密码表单验证规则
const forgotPasswordRules = reactive({
  email: [
    { required: true, message: '请输入注册邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        if (value !== forgotPasswordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ]
})

// 登录处理
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      loginLoading.value = true
      // 调用后端登录API
      axios.post('/api/auth/login', {
        username: loginForm.username,
        password: loginForm.password
      })
      .then(response => {
        loginLoading.value = false
        if (response.data.success) {
          // 检查用户实际角色与选择的角色是否匹配
          if (response.data.user.role !== selectedRole.value) {
            ElMessage.error(`登录失败：您选择的角色(${selectedRole.value})与实际角色(${response.data.user.role})不匹配`)
            return // 角色不匹配，禁止登录
          }
          ElMessage.success('登录成功')
          // 获取当前时间
          const currentTime = new Date().toLocaleString('zh-CN')
          // 获取上次登录时间（如果有）
          const lastLoginTime = localStorage.getItem('loginTime') || '未知'
          // 存储用户信息和token到本地存储
          localStorage.setItem('userInfo', JSON.stringify({
            username: response.data.user.username,
            role: response.data.user.role,
            name: response.data.user.name || response.data.user.username,
            id: response.data.user.id
          }))
          // 存储登录时间
          localStorage.setItem('loginTime', currentTime)
          // 存储上次登录时间
          localStorage.setItem('lastLoginTime', lastLoginTime)
          // 存储token
          localStorage.setItem('token', response.data.token)
          // 跳转到系统主页
          router.push('/home')
        } else {
          ElMessage.error(response.data.message)
        }
      })
      .catch(error => {
        loginLoading.value = false
        ElMessage.error('登录失败，请检查网络或联系管理员')
        console.error('登录错误:', error)
      })
    }
  })
}

// 注册处理
const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      registerLoading.value = true
      // 调用后端注册API
      axios.post('/api/auth/register', {
        username: registerForm.username,
        password: registerForm.password,
        name: registerForm.name,
        email: registerForm.email,
        phone: registerForm.phone,
        role: selectedRole.value
      })
      .then(response => {
        registerLoading.value = false
        if (response.data.success) {
          ElMessage.success('注册成功，请登录')
          activeTab.value = 'login'
        } else {
          ElMessage.error(response.data.message)
        }
      })
      .catch(error => {
        registerLoading.value = false
        ElMessage.error('注册失败，请检查网络或联系管理员')
        console.error('注册错误:', error)
      })
    }
  })
}

// 打开忘记密码对话框
const openForgotPasswordDialog = () => {
  forgotPasswordVisible.value = true
  // 确保每次打开对话框时，所有相关状态都已重置
  codeSending.value = false
  countdown.value = 0
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

// 关闭忘记密码对话框
const closeForgotPasswordDialog = () => {
  forgotPasswordVisible.value = false
  // 清空表单
  Object.assign(forgotPasswordForm, {
    email: '',
    code: '',
    newPassword: '',
    confirmPassword: ''
  })
  // 重置表单验证
  forgotPasswordFormRef.value?.resetFields()
  // 清除倒计时
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdown.value = 0
  }
  // 重置发送状态
  codeSending.value = false
}

// 发送重置密码验证码
const sendResetCode = () => {
  // 使用自定义邮箱验证逻辑，绕过Element Plus表单验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  const email = forgotPasswordForm.email.trim()
  
  if (!email) {
    ElMessage.warning('请输入邮箱地址')
    return
  }
  
  if (!emailRegex.test(email)) {
    ElMessage.warning('请输入有效的邮箱地址')
    return
  }
  
  codeSending.value = true
  
  axios.post('/api/auth/send-reset-code', { email })
    .then(response => {
      if (response.status === 200) {
        ElMessage.success('验证码发送成功')
        // 开始倒计时
        countdown.value = 60
        countdownTimer = setInterval(() => {
          countdown.value--
          if (countdown.value <= 0) {
            clearInterval(countdownTimer)
            countdownTimer = null
          }
        }, 1000)
      } else {
        ElMessage.error(response.data || '验证码发送失败')
      }
    })
    .catch(error => {
      ElMessage.error('验证码发送失败，请稍后重试')
    })
    .finally(() => {
      codeSending.value = false
    })
}

// 重置密码
const resetPassword = () => {
  forgotPasswordFormRef.value.validate((valid) => {
    if (valid) {
      resetPasswordLoading.value = true
      axios.post('/api/auth/reset-password', forgotPasswordForm)
        .then(response => {
          if (response.status === 200) {
            ElMessage.success('密码重置成功，请使用新密码登录')
            closeForgotPasswordDialog()
          } else {
            ElMessage.error(response.data || '密码重置失败')
          }
        })
        .catch(error => {
          console.error('重置密码失败:', error)
          ElMessage.error('密码重置失败，请稍后重试')
        })
        .finally(() => {
          resetPasswordLoading.value = false
        })
    }
  })
}
</script>

<style scoped>
/* 大学风格配色方案 */
:root {
  --primary-color: #2c3e50;
  --secondary-color: #3498db;
  --accent-color: #e74c3c;
  --light-color: #ecf0f1;
  --dark-color: #34495e;
  --success-color: #27ae60;
  --warning-color: #f39c12;
}

.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #ffffff 0%, #a8d8ea 100%);
  background-size: 400% 400%;
  animation: gradientAnimation 15s ease infinite;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰元素 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  z-index: 0;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.1) 0%, rgba(44, 62, 80, 0.05) 100%);
  animation: float 20s ease-in-out infinite;
}

.bg-circle.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.bg-circle.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -150px;
  right: -100px;
  animation-delay: 5s;
}

.bg-circle.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 20%;
  animation-delay: 10s;
}

.bg-circle.circle-4 {
  width: 150px;
  height: 150px;
  bottom: 30%;
  right: 30%;
  animation-delay: 15s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 渐变背景动画 */
@keyframes gradientAnimation {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.login-box {
  background: white;
  border-radius: 24px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  padding: 60px 50px;
  width: 100%;
  max-width: 650px;
  animation: slideUp 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(52, 152, 219, 0.1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.university-logo {
  margin-bottom: 20px;
  animation: logoPulse 2s ease-in-out infinite;
}

@keyframes logoPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.login-header h1 {
  color: var(--primary-color);
  font-size: 32px;
  margin-bottom: 12px;
  font-weight: 700;
  letter-spacing: -0.5px;
  font-family: 'Microsoft YaHei', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.login-header .subtitle {
  color: var(--secondary-color);
  font-size: 14px;
  margin-bottom: 15px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.login-header .role-guide {
  color: var(--dark-color);
  font-size: 15px;
  font-weight: 400;
}

.role-selector {
  margin-bottom: 40px;
}

.role-group {
  display: flex;
  justify-content: center;
  width: 100%;
  background: var(--light-color);
  border-radius: 12px;
  padding: 6px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.role-button {
  flex: 1;
  text-align: center;
  font-size: 15px;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: none;
  background: transparent;
  color: var(--dark-color);
}

.role-button.is-active {
  background: white;
  color: var(--secondary-color);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2);
}

.role-icon {
  margin-right: 6px;
  vertical-align: middle;
}

.login-tabs {
  width: 100%;
}

.login-tabs .el-tabs__header {
  margin-bottom: 35px;
}

.login-tabs .el-tabs__nav-wrap::after {
  background-color: var(--light-color);
}

.login-tabs .el-tabs__active-bar {
  background-color: var(--secondary-color);
  height: 3px;
}

.login-tabs .el-tabs__item {
  font-size: 16px;
  font-weight: 500;
  color: var(--dark-color);
  padding: 0 20px;
}

.login-tabs .el-tabs__item.is-active {
  color: var(--secondary-color);
}

.login-form {
  margin-top: 30px;
}

.login-form .el-form-item {
  margin-bottom: 35px;
}

.login-form .el-input__wrapper {
  border-radius: 14px;
  height: 56px;
  background-color: var(--light-color);
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.login-form .el-input__wrapper:hover {
  border-color: var(--secondary-color);
  background-color: white;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.15);
}

.login-form .el-input__wrapper.is-focus {
  border-color: var(--secondary-color);
  background-color: white;
  box-shadow: 0 4px 16px rgba(52, 152, 219, 0.25);
}

.login-form .el-input__inner {
  font-size: 16px;
  color: var(--dark-color);
}

.login-form .el-input__prefix {
  color: var(--secondary-color);
}

.login-form .el-button {
  height: 52px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, var(--secondary-color) 0%, #2980b9 100%);
  border: none !important;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: white !important;
  z-index: 10;
  display: block !important;
  opacity: 1 !important;
}

.login-form .el-button:hover {
  background: linear-gradient(135deg, #2980b9 0%, var(--secondary-color) 100%);
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
  transform: translateY(-2px);
  color: white !important;
  border: none !important;
}

.login-form .el-button:active {
  transform: translateY(0);
  color: white !important;
  border: none !important;
}

/* 确保成功类型按钮也有清晰的文字颜色 */
.login-form .el-button--success {
  background: linear-gradient(135deg, var(--success-color) 0%, #219653 100%) !important;
  color: white !important;
}

.login-form .el-button--success:hover {
  background: linear-gradient(135deg, #219653 0%, var(--success-color) 100%) !important;
  color: white !important;
}

/* 自定义登录按钮样式 */
.custom-login-btn {
  background-color: #3498db !important;
  color: white !important;
  height: 56px !important;
  border-radius: 14px !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3) !important;
  z-index: 9999 !important;
  display: block !important;
  opacity: 1 !important;
  width: 100% !important;
  margin-top: 20px !important;
  line-height: 56px !important;
  text-align: center !important;
}

/* 自定义注册按钮样式 */
.custom-register-btn {
  background-color: #3498db !important;
  color: white !important;
  height: 56px !important;
  border-radius: 14px !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3) !important;
  z-index: 9999 !important;
  display: block !important;
  opacity: 1 !important;
  width: 100% !important;
  margin-top: 20px !important;
  line-height: 56px !important;
  text-align: center !important;
}



/* 双列表单布局 */
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 35px;
}

.form-col {
  flex: 1;
  margin-bottom: 0 !important;
}

/* 确保所有按钮都能正确显示 */
.el-form-item {
  position: relative;
  overflow: visible !important;
}

.el-button {
  position: relative !important;
  z-index: 1000 !important;
  visibility: visible !important;
  pointer-events: auto !important;
}

/* 忘记密码链接样式 */
.forgot-password-container {
  text-align: center;
  margin-top: -25px;
  margin-bottom: 20px;
}

/* 忘记密码表单样式 */
.forgot-password-form .el-form-item {
  margin-bottom: 25px;
}

.login-footer {
  margin-top: 40px;
  text-align: center;
  color: var(--dark-color);
  font-size: 14px;
  position: relative;
  z-index: 1;
}

.footer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.copyright {
  margin: 0;
  opacity: 0.8;
}

.footer-links {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer-link {
  color: var(--secondary-color);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

.footer-divider {
  color: rgba(0, 0, 0, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
    padding: 50px 40px;
    max-width: 550px;
    margin: 20px;
  }
  
  .login-header h1 {
    font-size: 32px;
  }
  
  .login-form .el-input__wrapper {
    height: 52px;
  }
  
  .custom-login-btn,
  .custom-register-btn {
    height: 52px;
    line-height: 52px;
  }
  
  .bg-circle {
    display: none;
  }
  
  /* 小屏幕下恢复单列布局 */
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}

@media (max-width: 480px) {
  .login-box {
    padding: 35px 25px;
  }
  
  .role-group {
    flex-direction: column;
    gap: 8px;
  }
  
  .role-button {
    width: 100%;
  }
  
  /* 小屏幕下恢复单列布局 */
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>
