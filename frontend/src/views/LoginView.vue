<template>
  <div class="container" ref="containerRef">
    <!-- 左侧和右侧面板 -->
    <div class="panels-container">
      <!-- 左侧面板 - 登录引导 -->
      <div class="panel left-panel">
        <div class="content">
          <h3>新用户？</h3>
          <p>
            填写您的信息，开始使用学生评奖评优系统
          </p>
          <button class="btn transparent" id="sign-up-btn" @click="switchToRegister">
            注册
          </button>
        </div>
        <!-- 大型插画 - 登录场景 -->
        <svg class="image" viewBox="0 0 400 400" fill="none" xmlns="http://www.w3.org/2000/svg">
          <!-- 背景 -->
          <defs>
            <linearGradient id="bgGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#3498db;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#2980b9;stop-opacity:1" />
            </linearGradient>
          </defs>
          <rect width="400" height="400" fill="url(#bgGradient)" rx="20" />
          
          <!-- 人物 -->
          <g transform="translate(200, 300)">
            <!-- 身体 -->
            <ellipse cx="0" cy="-50" rx="30" ry="50" fill="#34495e" />
            <!-- 头部 -->
            <circle cx="0" cy="-90" r="25" fill="#f1c40f" />
            <!-- 头发 -->
            <path d="M-25 -90 C-35 -100 -35 -110 -25 -120 C-20 -125 20 -125 25 -120 C35 -110 35 -100 25 -90 Z" fill="#2c3e50" />
            <!-- 眼睛 -->
            <circle cx="-10" cy="-90" r="4" fill="#2c3e50" />
            <circle cx="10" cy="-90" r="4" fill="#2c3e50" />
            <!-- 嘴巴 -->
            <path d="M-8 -85 Q0 -80 8 -85" stroke="#2c3e50" stroke-width="2" fill="none" />
            <!-- 手臂 -->
            <path d="M30 -50 Q45 -60 50 -80" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <path d="M-30 -50 Q-45 -60 -50 -80" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <!-- 手 -->
            <circle cx="50" cy="-80" r="6" fill="#f1c40f" />
            <circle cx="-50" cy="-80" r="6" fill="#f1c40f" />
            <!-- 腿 -->
            <path d="M20 0 Q25 20 20 40" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <path d="M-20 0 Q-25 20 -20 40" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <!-- 脚 -->
            <rect x="10" y="40" width="20" height="8" fill="#34495e" rx="4" />
            <rect x="-30" y="40" width="20" height="8" fill="#34495e" rx="4" />
          </g>
          
          <!-- 火箭 -->
          <g transform="translate(100, 150) scale(0.6)">
            <!-- 火箭主体 -->
            <path d="M50 0 L150 100 L150 250 L50 350 L-50 250 L-50 100 Z" fill="#ecf0f1" />
            <!-- 火箭窗户 -->
            <circle cx="50" cy="100" r="30" fill="#3498db" />
            <circle cx="50" cy="100" r="20" fill="#ecf0f1" />
            <!-- 火箭鳍 -->
            <path d="M-50 150 L-100 125 L-100 175 Z" fill="#e74c3c" />
            <path d="M150 150 L200 125 L200 175 Z" fill="#e74c3c" />
            <path d="M-50 250 L-100 225 L-100 275 Z" fill="#e74c3c" />
            <path d="M150 250 L200 225 L200 275 Z" fill="#e74c3c" />
            <!-- 火箭火焰 -->
            <path d="M50 350 L20 400 L80 400 Z" fill="#f39c12" />
            <path d="M50 370 L30 420 L70 420 Z" fill="#e74c3c" />
          </g>
          
          <!-- 装饰元素 -->
          <circle cx="50" cy="50" r="10" fill="white" opacity="0.3" />
          <circle cx="350" cy="100" r="15" fill="white" opacity="0.3" />
          <circle cx="300" cy="350" r="8" fill="white" opacity="0.3" />
          <circle cx="100" cy="300" r="12" fill="white" opacity="0.3" />
        </svg>
      </div>
      
      <!-- 右侧面板 - 注册引导 -->
      <div class="panel right-panel">
        <div class="content">
          <h3>已有账号？</h3>
          <p>
            请使用您的账号登录学生评奖评优系统
          </p>
          <button class="btn transparent" id="sign-in-btn" @click="switchToLogin">
            登录
          </button>
        </div>
        <!-- 大型插画 - 注册场景 -->
        <svg class="image" viewBox="0 0 400 400" fill="none" xmlns="http://www.w3.org/2000/svg">
          <defs>
            <linearGradient id="regBgGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#8e44ad;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#9b59b6;stop-opacity:1" />
            </linearGradient>
          </defs>
          <rect width="400" height="400" fill="url(#regBgGradient)" rx="20" />
          
          <!-- 人物 -->
          <g transform="translate(200, 300)">
            <!-- 身体 -->
            <ellipse cx="0" cy="-50" rx="30" ry="50" fill="#34495e" />
            <!-- 头部 -->
            <circle cx="0" cy="-90" r="25" fill="#e84393" />
            <!-- 头发 -->
            <path d="M-25 -90 C-35 -100 -35 -110 -25 -120 C-20 -125 20 -125 25 -120 C35 -110 35 -100 25 -90 Z" fill="#2c3e50" />
            <!-- 眼睛 -->
            <circle cx="-10" cy="-90" r="4" fill="#2c3e50" />
            <circle cx="10" cy="-90" r="4" fill="#2c3e50" />
            <!-- 嘴巴 -->
            <path d="M-8 -85 Q0 -80 8 -85" stroke="#2c3e50" stroke-width="2" fill="none" />
            <!-- 手臂 -->
            <path d="M30 -50 Q45 -60 50 -80" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <path d="M-30 -50 Q-45 -60 -50 -80" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <!-- 手 -->
            <circle cx="50" cy="-80" r="6" fill="#e84393" />
            <circle cx="-50" cy="-80" r="6" fill="#e84393" />
            <!-- 腿 -->
            <path d="M20 0 Q25 20 20 40" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <path d="M-20 0 Q-25 20 -20 40" stroke="#34495e" stroke-width="12" fill="none" stroke-linecap="round" />
            <!-- 脚 -->
            <rect x="10" y="40" width="20" height="8" fill="#34495e" rx="4" />
            <rect x="-30" y="40" width="20" height="8" fill="#34495e" rx="4" />
          </g>
          
          <!-- 电脑 -->
          <g transform="translate(200, 200)">
            <!-- 屏幕 -->
            <rect x="-60" y="-100" width="120" height="80" fill="#2c3e50" rx="5" />
            <!-- 屏幕内容 -->
            <rect x="-50" y="-90" width="100" height="60" fill="#3498db" />
            <!-- 电脑支架 -->
            <rect x="-20" y="-20" width="40" height="20" fill="#7f8c8d" />
            <!-- 键盘 -->
            <rect x="-45" y="0" width="90" height="20" fill="#7f8c8d" rx="3" />
            <!-- 按键 -->
            <circle cx="-35" cy="10" r="3" fill="#2c3e50" />
            <circle cx="-25" cy="10" r="3" fill="#2c3e50" />
            <circle cx="-15" cy="10" r="3" fill="#2c3e50" />
            <circle cx="-5" cy="10" r="3" fill="#2c3e50" />
            <circle cx="5" cy="10" r="3" fill="#2c3e50" />
            <circle cx="15" cy="10" r="3" fill="#2c3e50" />
            <circle cx="25" cy="10" r="3" fill="#2c3e50" />
            <circle cx="35" cy="10" r="3" fill="#2c3e50" />
          </g>
          
          <!-- 装饰元素 -->
          <circle cx="50" cy="50" r="10" fill="white" opacity="0.3" />
          <circle cx="350" cy="100" r="15" fill="white" opacity="0.3" />
          <circle cx="300" cy="350" r="8" fill="white" opacity="0.3" />
          <circle cx="100" cy="300" r="12" fill="white" opacity="0.3" />
        </svg>
      </div>
    </div>
    
    <!-- 登录和注册表单 -->
    <div class="signin-signup">
      <!-- 登录表单 -->
      <div v-show="activeTab === 'login'" class="form-wrapper">
        <h2 class="title">学生评奖评优系统 - 登录</h2>
        
        <!-- 角色选择 -->
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
        
        <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
          <el-form-item prop="username">
            <div class="input-field">
              <i class="el-icon-user"></i>
              <el-input
                v-model="loginForm.username"
                placeholder="用户名"
                size="large"
                class="no-border-input"
              ></el-input>
            </div>
          </el-form-item>
          
          <el-form-item prop="password">
            <div class="input-field">
              <i class="el-icon-lock"></i>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                show-password
                size="large"
                class="no-border-input"
              ></el-input>
            </div>
          </el-form-item>
          
          <el-form-item>
            <button @click="handleLogin" :disabled="loginLoading" class="btn solid">
              {{ loginLoading ? '登录中...' : '登录' }}
            </button>
          </el-form-item>
          
          <div class="forgot-password-container">
            <el-link type="primary" :underline="false" @click="openForgotPasswordDialog">忘记密码？</el-link>
          </div>
        </el-form>
      </div>
      
      <!-- 注册表单 -->
      <div v-show="activeTab === 'register'" class="form-wrapper">
        <h2 class="title">学生评奖评优系统 - 注册</h2>
        
        <!-- 角色选择 -->
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
        
        <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="login-form">
          <!-- 第一行：用户名和真实姓名 -->
          <div class="form-row">
            <el-form-item prop="username" class="form-col">
              <div class="input-field">
                <i class="el-icon-user"></i>
                <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                  size="large"
                  class="no-border-input"
                ></el-input>
              </div>
            </el-form-item>
            
            <el-form-item prop="name" class="form-col">
              <div class="input-field">
                <i class="el-icon-user"></i>
                <el-input
                  v-model="registerForm.name"
                  placeholder="真实姓名"
                  size="large"
                  class="no-border-input"
                ></el-input>
              </div>
            </el-form-item>
          </div>
          
          <!-- 第二行：密码和确认密码 -->
          <div class="form-row">
            <el-form-item prop="password" class="form-col">
              <div class="input-field">
                <i class="el-icon-lock"></i>
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="密码"
                  show-password
                  size="large"
                  class="no-border-input"
                ></el-input>
              </div>
            </el-form-item>
            
            <el-form-item prop="confirmPassword" class="form-col">
              <div class="input-field">
                <i class="el-icon-lock"></i>
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="确认密码"
                  show-password
                  size="large"
                  class="no-border-input"
                ></el-input>
              </div>
            </el-form-item>
          </div>
          
          <!-- 第三行：邮箱和联系电话 -->
          <div class="form-row">
            <el-form-item prop="email" class="form-col">
              <div class="input-field">
                <i class="el-icon-message"></i>
                <el-input
                  v-model="registerForm.email"
                  placeholder="邮箱"
                  size="large"
                  class="no-border-input"
                ></el-input>
              </div>
            </el-form-item>
            
            <el-form-item prop="phone" class="form-col">
              <div class="input-field">
                <i class="el-icon-phone"></i>
                <el-input
                  v-model="registerForm.phone"
                  placeholder="联系电话"
                  size="large"
                  class="no-border-input"
                ></el-input>
              </div>
            </el-form-item>
          </div>
          
          <el-form-item>
            <button @click="handleRegister" :disabled="registerLoading" class="btn solid">
              {{ registerLoading ? '注册中...' : '注册' }}
            </button>
          </el-form-item>
        </el-form>
      </div>
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
          <div class="input-field">
            <i class="el-icon-message"></i>
            <el-input
              v-model="forgotPasswordForm.email"
              placeholder="请输入注册邮箱"
              size="large"
              class="no-border-input"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="code">
          <div class="input-field with-append">
            <i class="el-icon-document"></i>
            <el-input
              v-model="forgotPasswordForm.code"
              placeholder="请输入验证码"
              size="large"
              class="no-border-input"
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
          </div>
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <div class="input-field">
            <i class="el-icon-lock"></i>
            <el-input
              v-model="forgotPasswordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
              size="large"
              class="no-border-input"
            ></el-input>
          </div>
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <div class="input-field">
            <i class="el-icon-lock"></i>
            <el-input
              v-model="forgotPasswordForm.confirmPassword"
              type="password"
              placeholder="请确认新密码"
              show-password
              size="large"
              class="no-border-input"
            ></el-input>
          </div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeForgotPasswordDialog">取消</el-button>
          <el-button type="primary" @click="resetPassword" :loading="resetPasswordLoading">重置密码</el-button>
        </div>
      </template>
    </el-dialog>
    

  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
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

// 容器引用
const containerRef = ref(null)

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

// 监听activeTab变化，切换样式类
watch(activeTab, (newVal) => {
  if (containerRef.value) {
    if (newVal === 'register') {
      containerRef.value.classList.add('sign-up-mode')
    } else {
      containerRef.value.classList.remove('sign-up-mode')
    }
  }
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

// 切换到注册
const switchToRegister = () => {
  activeTab.value = 'register'
}

// 切换到登录
const switchToLogin = () => {
  activeTab.value = 'login'
}

// 页面挂载时初始化
onMounted(() => {
  // 初始化样式类
  if (containerRef.value) {
    if (activeTab.value === 'register') {
      containerRef.value.classList.add('sign-up-mode')
    } else {
      containerRef.value.classList.remove('sign-up-mode')
    }
  }
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body, input {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

:root {
  --primary-color: #3498db;
  --secondary-color: #2980b9;
  --accent-color: #8e44ad;
  --light-color: #f8f9fa;
  --dark-color: #2c3e50;
  --white-color: #ffffff;
  --success-color: #27ae60;
  --warning-color: #f39c12;
  --danger-color: #e74c3c;
  --shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  --shadow-hover: 0 15px 40px rgba(0, 0, 0, 0.15);
  --transition: all 0.3s ease;
}

/* 主容器样式 */
.container {
  position: relative;
  width: 100%;
  background-color: var(--light-color);
  min-height: 100vh;
  overflow: hidden;
}

/* 背景装饰 */
.container:before {
  content: "";
  position: absolute;
  height: 2000px;
  width: 2000px;
  top: -10%;
  right: 48%;
  transform: translateY(-50%);
  background-image: linear-gradient(-45deg, var(--primary-color) 0%, var(--accent-color) 100%);
  transition: 1.8s ease-in-out;
  border-radius: 50%;
  z-index: 6;
  box-shadow: 0 0 100px rgba(0, 0, 0, 0.1);
}

/* 添加装饰性气泡 */
.container:after {
  content: "";
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(52, 152, 219, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(142, 68, 173, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(41, 128, 185, 0.1) 0%, transparent 50%);
  z-index: 5;
  pointer-events: none;
}

/* 表单容器 */
.forms-container {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 7;
}

/* 登录注册表单 */
.signin-signup {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  left: 75%;
  width: 50%;
  transition: 1s 0.7s ease-in-out;
  display: grid;
  grid-template-columns: 1fr;
  z-index: 7;
  max-width: 400px;
}

/* 表单样式 */
.form-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 2rem 3rem;
  transition: all 0.2s 0.7s;
  overflow: hidden;
  grid-column: 1 / 2;
  grid-row: 1 / 2;
  background: var(--white-color);
  border-radius: 20px;
  box-shadow: var(--shadow);
  min-height: 480px;
}

/* 标题样式 */
.title {
  font-size: 2rem;
  color: var(--dark-color);
  margin-bottom: 20px;
  font-weight: 700;
  letter-spacing: -0.5px;
  text-align: center;
}

/* 输入框样式 */
.input-field {
  max-width: 380px;
  width: 100%;
  background-color: var(--light-color);
  margin: 15px 0;
  height: 55px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  position: relative;
  transition: var(--transition);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
  overflow: hidden;
  background-clip: padding-box;
  border: none !important;
}

/* 确保表单没有默认样式 */
.login-form :deep(.el-form-item) {
  margin-bottom: 0 !important;
}

.login-form :deep(.el-form-item__label) {
  display: none !important;
}

.login-form :deep(.el-form-item__content) {
  margin-left: 0 !important;
  width: 100% !important;
}

.input-field:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.input-field i {
  margin-right: 15px;
  color: var(--primary-color);
  transition: 0.5s;
  font-size: 1.2rem;
  z-index: 1;
}

/* 无边框输入框样式 */
.no-border-input {
  background: transparent !important;
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  width: 100% !important;
  margin: 0 !important;
  padding: 0 !important;
  flex: 1 !important;
  height: 100% !important;
  border-radius: 0 !important;
}

/* 确保输入框完全填满胶囊 */
.input-field > .el-input {
  flex: 1 !important;
  width: 100% !important;
  height: 100% !important;
}

.input-field > .el-input > .el-input__wrapper {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  width: 100% !important;
  height: 100% !important;
  padding: 0 !important;
  margin: 0 !important;
  border-radius: 0 !important;
}

.input-field > .el-input > .el-input__wrapper > .el-input__inner {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  width: 100% !important;
  height: 100% !important;
  padding: 0 !important;
  margin: 0 !important;
  border-radius: 0 !important;
}

/* 表单行和列样式 */
.form-row {
  display: flex;
  gap: 15px;
  width: 100%;
  margin-bottom: 0;
}

.form-col {
  flex: 1;
  margin-bottom: 0;
  width: 100%;
}

.form-col :deep(.el-form-item) {
  margin-bottom: 0;
  width: 100%;
}

/* 确保登录表单没有额外样式 */
.login-form {
  width: 100%;
}

/* 深度修改Element Plus输入框样式 */
.input-field :deep(.el-input) {
  width: 100% !important;
  height: 100% !important;
  flex: 1 !important;
}

.input-field :deep(.el-input__wrapper) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  width: 100% !important;
  height: 100% !important;
  padding: 0 !important;
  margin: 0 !important;
  box-sizing: border-box !important;
  flex: 1 !important;
  min-width: 0 !important;
  overflow: hidden;
  border-radius: 0 !important;
  --el-input-wrapper-bg-color: transparent !important;
  --el-input-wrapper-border-color: transparent !important;
  --el-input-wrapper-focus-border-color: transparent !important;
  --el-input-wrapper-hover-border-color: transparent !important;
  --el-input-wrapper-focus-box-shadow: none !important;
}

.input-field :deep(.el-input__inner) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 0 !important;
  margin: 0 !important;
  font-size: 1.1rem !important;
  font-weight: 600 !important;
  color: var(--dark-color) !important;
  height: 100% !important;
  line-height: 55px !important;
  width: 100% !important;
  box-sizing: border-box !important;
  min-width: 0 !important;
  outline: none !important;
  border-radius: 0 !important;
}

/* 确保输入框容器没有额外样式 */
.login-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.login-form :deep(.el-form-item__content) {
  margin-left: 0 !important;
  width: 100%;
}

/* 移除输入框的默认样式 */
.el-input {
  --el-input-bg-color: transparent !important;
  --el-input-border-color: transparent !important;
  --el-input-focus-border-color: transparent !important;
  --el-input-hover-border-color: transparent !important;
  --el-input-border-radius: 0 !important;
}

/* 确保所有输入框都应用相同样式 */
:deep(.el-input__wrapper) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

:deep(.el-input__inner) {
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  border-radius: 0 !important;
}

/* 确保输入框没有额外的圆角 */
.el-input__inner {
  border-radius: 0 !important;
}

/* 确保输入框容器没有额外的样式 */
.el-form-item {
  margin-bottom: 0 !important;
}

.el-form-item__content {
  margin-left: 0 !important;
  width: 100% !important;
}

.no-border-input .el-input__inner::placeholder {
  color: #95a5a6 !important;
  font-weight: 500 !important;
}

/* 带有附加按钮的输入框 */
.input-field.with-append {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 按钮样式 */
.btn {
  width: 100%;
  max-width: 380px;
  background: #409eff;
  border: none;
  outline: none;
  height: 55px;
  border-radius: 50px;
  color: var(--white-color);
  text-transform: uppercase;
  font-weight: 600;
  margin: 20px 0;
  cursor: pointer;
  transition: var(--transition);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  font-size: 1rem;
  letter-spacing: 1px;
}

.btn:hover {
  background: #66b1ff;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  transform: translateY(-2px);
}

.btn:active {
  transform: translateY(0);
}

/* 角色选择器样式 */
.role-selector {
  margin: 25px 0;
  width: 100%;
}

.role-group {
  display: flex;
  justify-content: space-between;
  width: 100%;
  background: transparent !important;
  border-radius: 30px;
  padding: 0;
  box-shadow: none;
  transition: var(--transition);
  gap: 0;
  border: 2px solid rgba(52, 152, 219, 0.2);
}

.role-group:hover {
  border-color: rgba(52, 152, 219, 0.4);
}

/* 使用:deep()选择器确保样式被正确应用 */
.role-selector :deep(.el-radio-group) {
  background: transparent !important;
  width: 100% !important;
  display: flex !important;
  border-radius: 28px !important;
  padding: 0 !important;
}

.role-selector :deep(.el-radio-button) {
  flex: 1 !important;
  width: 33.33% !important;
  margin: 0 !important;
  border: none !important;
  background: transparent !important;
}

.role-selector :deep(.el-radio-button__inner) {
  border: none !important;
  background: transparent !important;
  color: var(--dark-color) !important;
  border-radius: 0 !important;
  padding: 14px 8px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  box-shadow: none !important;
  line-height: normal !important;
  height: auto !important;
  min-height: 50px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* 移除第一个和最后一个按钮的圆角 */
.role-selector :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 28px 0 0 28px !important;
}

.role-selector :deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0 28px 28px 0 !important;
}

/* 按钮悬停效果 */
.role-selector :deep(.el-radio-button__inner:hover) {
  background: rgba(52, 152, 219, 0.1) !important;
  color: var(--primary-color) !important;
}

/* 按钮激活效果 */
.role-selector :deep(.el-radio-button.is-active .el-radio-button__inner) {
  background: #409eff !important;
  color: var(--white-color) !important;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.3) !important;
}

/* 确保激活状态的按钮样式正确 */
.role-selector :deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background: #409eff !important;
  color: var(--white-color) !important;
}

/* 移除Element Plus默认的边框和背景 */
.role-selector :deep(.el-radio-button--default.is-active .el-radio-button__inner) {
  border-color: transparent !important;
  color: var(--white-color) !important;
  background: #409eff !important;
}

.role-selector :deep(.el-radio-button--default:not(.is-active) .el-radio-button__inner) {
  border-color: transparent !important;
  color: var(--dark-color) !important;
  background: transparent !important;
}

/* 确保没有额外的边框 */
.role-selector :deep(.el-radio-button + .el-radio-button) {
  margin-left: 0 !important;
  border-left: none !important;
}

/* 确保激活状态的按钮显示在最上层 */
.role-selector :deep(.el-radio-button.is-active) {
  z-index: 1;
}

/* 确保激活状态的按钮没有额外样式 */
.role-selector :deep(.el-radio-button.is-active + .el-radio-button .el-radio-button__inner) {
  border-left: none !important;
}

/* 移除所有可能导致白底的样式 */
.role-selector :deep(.el-radio-button__inner) {
  --el-button-bg-color: transparent !important;
  --el-button-border-color: transparent !important;
  --el-button-hover-bg-color: rgba(52, 152, 219, 0.1) !important;
  --el-button-hover-border-color: transparent !important;
  --el-button-active-bg-color: transparent !important;
  --el-button-active-border-color: transparent !important;
}

/* 角色图标样式 */
.role-icon {
  margin-right: 6px;
  vertical-align: middle;
  font-size: 15px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 忘记密码链接样式 */
.forgot-password-container {
  text-align: center;
  margin-top: 20px;
  margin-bottom: 15px;
}

.forgot-password-container .el-link {
  color: var(--primary-color) !important;
  font-weight: 500 !important;
  transition: var(--transition);
  font-size: 0.9rem;
}

.forgot-password-container .el-link:hover {
  color: var(--secondary-color) !important;
  text-decoration: underline !important;
}

/* 左侧和右侧面板样式 */
.panels-container {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  z-index: 6;
}

/* 面板样式 */
.panel {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-around;
  text-align: center;
  z-index: 6;
  padding: 3rem 12%;
}

.left-panel {
  pointer-events: all;
  align-items: flex-start;
}

.right-panel {
  pointer-events: none;
  align-items: flex-end;
}

.panel .content {
  color: var(--white-color);
  transition: transform 0.9s ease-in-out;
  transition-delay: 0.6s;
  max-width: 300px;
}

.panel h3 {
  font-weight: 700;
  line-height: 1.2;
  font-size: 2rem;
  margin-bottom: 15px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.panel p {
  font-size: 1rem;
  padding: 0.7rem 0;
  margin-bottom: 30px;
  line-height: 1.5;
  opacity: 0.9;
}

.btn.transparent {
  margin: 0;
  background: none;
  border: 2px solid var(--white-color);
  width: 150px;
  height: 45px;
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--white-color);
  transition: var(--transition);
  border-radius: 22px;
}

.btn.transparent:hover {
  background: var(--white-color);
  color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

/* 图片样式 */
.image {
  width: 100%;
  transition: transform 1.1s ease-in-out, opacity 1.1s ease-in-out;
  transition-delay: 0.4s;
  max-width: 300px;
  opacity: 0.95;
}

/* 右侧面板初始状态 */
.right-panel .image,
.right-panel .content {
  transform: translateX(800px);
}

/* 注册模式样式 */
.container.sign-up-mode:before {
  transform: translate(100%, -50%);
  right: 52%;
}

.container.sign-up-mode .left-panel .image,
.container.sign-up-mode .left-panel .content {
  transform: translateX(-800px);
}

.container.sign-up-mode .signin-signup {
  left: 25%;
}

.container.sign-up-mode .form-wrapper {
  opacity: 1;
  z-index: 2;
}

.container.sign-up-mode .right-panel .image,
.container.sign-up-mode .right-panel .content {
  transform: translateX(0%);
}

.container.sign-up-mode .left-panel {
  pointer-events: none;
}

.container.sign-up-mode .right-panel {
  pointer-events: all;
}



/* 响应式设计 */
@media (max-width: 1024px) {
  .signin-signup {
    width: 60%;
  }
  
  .panel h3 {
    font-size: 1.8rem;
  }
}

@media (max-width: 870px) {
  .container {
    min-height: 800px;
    height: 100vh;
  }
  
  .signin-signup {
    width: 90%;
    top: 95%;
    transform: translate(-50%, -100%);
    transition: 1s 0.8s ease-in-out;
  }
  
  .signin-signup, .container.sign-up-mode .signin-signup {
    left: 50%;
  }
  
  .panels-container {
    grid-template-columns: 1fr;
    grid-template-rows: 1fr 2fr 1fr;
  }
  
  .panel {
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    padding: 2rem 8%;
    grid-column: 1 / 2;
  }
  
  .right-panel {
    grid-row: 3 / 4;
  }
  
  .left-panel {
    grid-row: 1 / 2;
  }
  
  .image {
    width: 250px;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.6s;
  }
  
  .panel .content {
    padding-right: 15%;
    transition: transform 0.9s ease-in-out;
    transition-delay: 0.8s;
  }
  
  .panel h3 {
    font-size: 1.5rem;
  }
  
  .panel p {
    font-size: 0.9rem;
    padding: 0.5rem 0;
  }
  
  .btn.transparent {
    width: 130px;
    height: 40px;
    font-size: 0.8rem;
  }
  
  .container:before {
    width: 1500px;
    height: 1500px;
    transform: translateX(-50%);
    left: 30%;
    bottom: 68%;
    right: initial;
    top: initial;
    transition: 2s ease-in-out;
  }
  
  .container.sign-up-mode:before {
    transform: translate(-50%, 100%);
    bottom: 32%;
    right: initial;
  }
  
  .container.sign-up-mode .left-panel .image,
  .container.sign-up-mode .left-panel .content {
    transform: translateY(-300px);
  }
  
  .container.sign-up-mode .right-panel .image,
  .container.sign-up-mode .right-panel .content {
    transform: translateY(0px);
  }
  
  .right-panel .image,
  .right-panel .content {
    transform: translateY(300px);
  }
  
  .container.sign-up-mode .signin-signup {
    top: 5%;
    transform: translate(-50%, 0);
  }
}

@media (max-width: 570px) {
  .form-wrapper {
    padding: 2rem 1.5rem;
    min-height: 450px;
    width: 100%;
  }
  
  /* 小屏幕下单列显示 */
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .image {
    display: none;
  }
  
  .panel .content {
    padding: 0.5rem 1rem;
  }
  
  .container {
    padding: 1rem;
  }
  
  .container:before {
    bottom: 72%;
    left: 50%;
  }
  
  .container.sign-up-mode:before {
    bottom: 28%;
    left: 50%;
  }
  
  .title {
    font-size: 1.8rem;
  }
  
  .input-field {
    margin: 12px 0;
    height: 50px;
  }
  
  .btn {
    width: 160px;
    height: 45px;
    font-size: 0.9rem;
  }
  
  /* 身份选择器始终一行三个显示 */
  .role-group {
    flex-direction: row;
    gap: 4px;
  }
  
  .role-button {
    width: auto;
    flex: 1;
  }
  
  .panel h3 {
    font-size: 1.5rem;
  }
  
  .panel p {
    font-size: 0.85rem;
  }
}

/* 忘记密码对话框样式 */
.forgot-password-form .el-form-item {
  margin-bottom: 25px;
}

.forgot-password-form .input-field {
  margin: 15px 0;
}

/* 自定义发送验证码按钮 */
.custom-send-code-btn {
  background-color: var(--primary-color) !important;
  border: none !important;
  color: var(--white-color) !important;
  height: 40px !important;
  padding: 0 15px !important;
  font-size: 0.85rem !important;
  border-radius: 20px !important;
  transition: var(--transition) !important;
  box-shadow: 0 3px 10px rgba(52, 152, 219, 0.3) !important;
}

.custom-send-code-btn:hover {
  background-color: var(--secondary-color) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.4) !important;
}

/* 忘记密码对话框按钮样式 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding: 20px 0 0 0;
}

.dialog-footer .el-button {
  border-radius: 30px !important;
  padding: 10px 25px !important;
  font-weight: 600 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  min-width: 120px !important;
}

.dialog-footer .el-button--primary {
  background: #409eff !important;
  border: none !important;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3) !important;
  color: var(--white-color) !important;
}

.dialog-footer .el-button--primary:hover {
  background: #66b1ff !important;
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4) !important;
  transform: translateY(-1px) !important;
}

.dialog-footer .el-button--default {
  background: transparent !important;
  border: 2px solid var(--primary-color) !important;
  color: var(--primary-color) !important;
  box-shadow: none !important;
}

.dialog-footer .el-button--default:hover {
  background: rgba(52, 152, 219, 0.1) !important;
  transform: translateY(-1px) !important;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2) !important;
}

/* 添加加载动画 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  animation: pulse 1.5s ease-in-out infinite;
}
</style>