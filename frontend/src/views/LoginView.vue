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
          <defs>
            <linearGradient id="bgGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#0F4C81;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#1A6B9C;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#3D8EB9;stop-opacity:1" />
            </linearGradient>
            <linearGradient id="goldGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#D4AF37;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#E8B96B;stop-opacity:1" />
            </linearGradient>
            <filter id="glow">
              <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
              <feMerge>
                <feMergeNode in="coloredBlur"/>
                <feMergeNode in="SourceGraphic"/>
              </feMerge>
            </filter>
          </defs>
          <rect width="400" height="400" fill="url(#bgGradient)" rx="24" />
          
          <!-- 装饰性六边形网格 -->
          <g opacity="0.08">
            <polygon points="50,80 80,60 110,80 110,120 80,140 50,120" fill="white"/>
            <polygon points="300,50 330,30 360,50 360,90 330,110 300,90" fill="white"/>
            <polygon points="320,300 350,280 380,300 380,340 350,360 320,340" fill="white"/>
          </g>
          
          <!-- 奖杯 -->
          <g transform="translate(200, 140)" filter="url(#glow)">
            <path d="M-30 -60 L30 -60 L25 -20 Q0 10 -25 -20 Z" fill="url(#goldGradient)"/>
            <rect x="-8" y="-20" width="16" height="25" fill="#B8962E"/>
            <ellipse cx="0" cy="10" rx="25" ry="8" fill="#B8962E"/>
            <ellipse cx="0" cy="-60" rx="30" ry="10" fill="#F5D485"/>
            <path d="M-35 -55 Q-55 -50 -50 -30 Q-45 -10 -30 -15" stroke="url(#goldGradient)" stroke-width="4" fill="none"/>
            <path d="M35 -55 Q55 -50 50 -30 Q45 -10 30 -15" stroke="url(#goldGradient)" stroke-width="4" fill="none"/>
            <text x="0" y="-35" text-anchor="middle" font-size="16" fill="#B8962E" font-weight="bold">1</text>
          </g>
          
          <!-- 书本 -->
          <g transform="translate(120, 260)">
            <rect x="-40" y="-25" width="80" height="50" rx="3" fill="#E8E4DD"/>
            <rect x="-38" y="-23" width="36" height="46" fill="#FFFFFF"/>
            <rect x="0" y="-23" width="36" height="46" fill="#F8F6F2"/>
            <line x1="-35" y1="-15" x2="-5" y2="-15" stroke="#D4CFC6" stroke-width="2"/>
            <line x1="-35" y1="-5" x2="-5" y2="-5" stroke="#D4CFC6" stroke-width="2"/>
            <line x1="-35" y1="5" x2="-5" y2="5" stroke="#D4CFC6" stroke-width="2"/>
            <line x1="5" y1="-15" x2="35" y2="-15" stroke="#D4CFC6" stroke-width="2"/>
            <line x1="5" y1="-5" x2="35" y2="-5" stroke="#D4CFC6" stroke-width="2"/>
          </g>
          
          <!-- 证书 -->
          <g transform="translate(280, 260)">
            <rect x="-35" y="-25" width="70" height="50" rx="2" fill="#FFFFFF"/>
            <rect x="-30" y="-20" width="60" height="40" fill="#FAFAF8"/>
            <line x1="-25" y1="-10" x2="25" y2="-10" stroke="#D4AF37" stroke-width="2"/>
            <line x1="-20" y1="0" x2="20" y2="0" stroke="#D4CFC6" stroke-width="1"/>
            <line x1="-20" y1="6" x2="20" y2="6" stroke="#D4CFC6" stroke-width="1"/>
            <circle cx="0" cy="15" r="8" fill="url(#goldGradient)"/>
          </g>
          
          <!-- 星星装饰 -->
          <g fill="url(#goldGradient)" opacity="0.6">
            <polygon points="60,180 63,190 73,190 65,196 68,206 60,200 52,206 55,196 47,190 57,190">
              <animate attributeName="opacity" values="0.6;1;0.6" dur="2s" repeatCount="indefinite"/>
            </polygon>
            <polygon points="340,200 343,210 353,210 345,216 348,226 340,220 332,226 335,216 327,210 337,210">
              <animate attributeName="opacity" values="0.6;1;0.6" dur="2.5s" repeatCount="indefinite"/>
            </polygon>
          </g>
          
          <!-- 浮动圆点装饰 -->
          <circle cx="50" cy="50" r="6" fill="white" opacity="0.2">
            <animate attributeName="cy" values="50;45;50" dur="3s" repeatCount="indefinite"/>
          </circle>
          <circle cx="350" cy="80" r="8" fill="white" opacity="0.15">
            <animate attributeName="cy" values="80;75;80" dur="4s" repeatCount="indefinite"/>
          </circle>
          <circle cx="320" cy="350" r="5" fill="white" opacity="0.2">
            <animate attributeName="cy" values="350;345;350" dur="3.5s" repeatCount="indefinite"/>
          </circle>
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
              <stop offset="0%" style="stop-color:#1A6B9C;stop-opacity:1" />
              <stop offset="50%" style="stop-color:#0F4C81;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#0A3A5F;stop-opacity:1" />
            </linearGradient>
            <linearGradient id="goldGradient2" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#D4AF37;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#F5D485;stop-opacity:1" />
            </linearGradient>
            <filter id="glow2">
              <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
              <feMerge>
                <feMergeNode in="coloredBlur"/>
                <feMergeNode in="SourceGraphic"/>
              </feMerge>
            </filter>
          </defs>
          <rect width="400" height="400" fill="url(#regBgGradient)" rx="24" />
          
          <!-- 装饰性六边形网格 -->
          <g opacity="0.06">
            <polygon points="30,300 60,280 90,300 90,340 60,360 30,340" fill="white"/>
            <polygon points="310,70 340,50 370,70 370,110 340,130 310,110" fill="white"/>
          </g>
          
          <!-- 笔记本电脑 -->
          <g transform="translate(200, 180)">
            <rect x="-70" y="-50" width="140" height="90" rx="6" fill="#2C3E50"/>
            <rect x="-62" y="-42" width="124" height="74" fill="#1A252F"/>
            <rect x="-58" y="-38" width="116" height="66" fill="#0F4C81"/>
            <rect x="-20" y="-5" width="40" height="8" rx="4" fill="url(#goldGradient2)"/>
            <rect x="-35" y="10" width="70" height="4" rx="2" fill="rgba(255,255,255,0.3)"/>
            <rect x="-25" y="18" width="50" height="4" rx="2" fill="rgba(255,255,255,0.2)"/>
            <rect x="-55" y="40" width="110" height="12" rx="2" fill="#3D566E"/>
            <rect x="-40" y="44" width="80" height="4" rx="1" fill="#4A637B"/>
          </g>
          
          <!-- 漂浮的文档 -->
          <g transform="translate(100, 120)">
            <rect x="-25" y="-32" width="50" height="64" rx="3" fill="#FFFFFF" opacity="0.95">
              <animate attributeName="y" values="-32;-28;-32" dur="3s" repeatCount="indefinite"/>
            </rect>
            <line x1="-18" y1="-22" x2="18" y2="-22" stroke="#D4AF37" stroke-width="2"/>
            <line x1="-15" y1="-12" x2="15" y2="-12" stroke="#E2DDD5" stroke-width="2"/>
            <line x1="-15" y1="-4" x2="15" y2="-4" stroke="#E2DDD5" stroke-width="2"/>
            <line x1="-15" y1="4" x2="10" y2="4" stroke="#E2DDD5" stroke-width="2"/>
            <circle cx="0" cy="18" r="6" fill="url(#goldGradient2)"/>
          </g>
          
          <!-- 漂浮的奖章 -->
          <g transform="translate(300, 130)">
            <circle cx="0" cy="0" r="25" fill="url(#goldGradient2)" filter="url(#glow2)">
              <animate attributeName="r" values="25;27;25" dur="2s" repeatCount="indefinite"/>
            </circle>
            <circle cx="0" cy="0" r="18" fill="#B8962E"/>
            <polygon points="0,-12 3,-4 12,-4 5,2 7,10 0,5 -7,10 -5,2 -12,-4 -3,-4" fill="#F5D485"/>
            <path d="M-8 25 L0 20 L8 25 L0 40 Z" fill="#D4AF37"/>
          </g>
          
          <!-- 铅笔 -->
          <g transform="translate(320, 280) rotate(-30)">
            <rect x="-5" y="-40" width="10" height="50" fill="#F5D485"/>
            <polygon points="0,15 -5,10 5,10" fill="#2C3E50"/>
            <rect x="-5" y="-40" width="10" height="8" fill="#E8B96B"/>
          </g>
          
          <!-- 星星装饰 -->
          <g fill="url(#goldGradient2)" opacity="0.5">
            <polygon points="70,220 72,228 80,228 74,233 76,241 70,236 64,241 66,233 60,228 68,228">
              <animate attributeName="opacity" values="0.5;0.9;0.5" dur="2.2s" repeatCount="indefinite"/>
            </polygon>
            <polygon points="330,320 332,328 340,328 334,333 336,341 330,336 324,341 326,333 320,328 328,328">
              <animate attributeName="opacity" values="0.5;0.9;0.5" dur="2.8s" repeatCount="indefinite"/>
            </polygon>
          </g>
          
          <!-- 浮动圆点装饰 -->
          <circle cx="60" cy="60" r="5" fill="white" opacity="0.15">
            <animate attributeName="cy" values="60;55;60" dur="3.5s" repeatCount="indefinite"/>
          </circle>
          <circle cx="340" cy="50" r="7" fill="white" opacity="0.12">
            <animate attributeName="cy" values="50;45;50" dur="4s" repeatCount="indefinite"/>
          </circle>
          <circle cx="80" cy="340" r="4" fill="white" opacity="0.18">
            <animate attributeName="cy" values="340;335;340" dur="3s" repeatCount="indefinite"/>
          </circle>
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
          
          <el-form-item v-if="selectedRole !== 'admin'">
            <button @click="handleRegister" :disabled="registerLoading" class="btn solid">
              {{ registerLoading ? '注册中...' : '注册' }}
            </button>
          </el-form-item>
          
          <div v-else class="admin-register-notice">
            <el-alert
              title="管理员账号不支持注册"
              description="请联系系统管理员获取账号"
              type="warning"
              :closable="false"
              show-icon
            />
          </div>
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
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()

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
          const lastLoginTime = localStorage.getItem('loginTime') || '未知'
          // 通过 store 存储用户信息和 token
          userStore.login({
            username: response.data.user.username,
            role: response.data.user.role,
            name: response.data.user.name || response.data.user.username,
            id: response.data.user.id,
            avatar: response.data.user.avatar || ''
          }, response.data.token)
          // 存储登录时间
          localStorage.setItem('loginTime', currentTime)
          localStorage.setItem('lastLoginTime', lastLoginTime)
          // 跳转到系统主页
          router.push('/home')
        } else {
          ElMessage.error(response.data.message)
        }
      })
      .catch(error => {
        loginLoading.value = false
        ElMessage.error('登录失败，请检查用户名和密码或联系管理员')
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
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;500;600;700&family=Source+Sans+3:wght@300;400;500;600;700&family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body, input {
  font-family: 'Source Sans 3', 'Noto Sans SC', 'Microsoft YaHei', sans-serif;
}

:root {
  --primary-deep: #0F4C81;
  --primary-medium: #1A6B9C;
  --primary-light: #3D8EB9;
  --accent-gold: #D4AF37;
  --accent-warm: #E8B96B;
  --accent-light: #F5D485;
  --bg-cream: #FAF8F5;
  --bg-paper: #F5F2ED;
  --text-ink: #1A1A2E;
  --text-slate: #4A5568;
  --white-color: #ffffff;
  --success-color: #2D8B5E;
  --warning-color: #D4953A;
  --danger-color: #C44536;
  --shadow-sm: 0 2px 8px rgba(15, 76, 129, 0.08);
  --shadow-md: 0 4px 20px rgba(15, 76, 129, 0.12);
  --shadow-lg: 0 8px 40px rgba(15, 76, 129, 0.16);
  --shadow-xl: 0 16px 60px rgba(15, 76, 129, 0.2);
  --shadow-gold: 0 4px 24px rgba(212, 175, 55, 0.3);
  --transition-fast: 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-base: 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-slow: 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-bounce: 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 主容器样式 */
.container {
  position: relative;
  width: 100%;
  background: linear-gradient(135deg, var(--bg-cream) 0%, var(--bg-paper) 50%, #EDE8E0 100%);
  min-height: 100vh;
  overflow: hidden;
}

.container::before {
  content: "";
  position: absolute;
  height: 2200px;
  width: 2200px;
  top: -15%;
  right: 46%;
  transform: translateY(-50%);
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 50%, var(--primary-light) 100%);
  transition: 1.8s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 50%;
  z-index: 6;
  box-shadow: 
    0 0 80px rgba(15, 76, 129, 0.3),
    0 0 160px rgba(15, 76, 129, 0.15),
    inset 0 0 100px rgba(255, 255, 255, 0.05);
}

.container::before::after {
  content: "";
  position: absolute;
  top: 10%;
  left: 10%;
  right: 10%;
  bottom: 10%;
  border-radius: 50%;
  border: 2px solid rgba(212, 175, 55, 0.2);
}

.container::after {
  content: "";
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  background-image: 
    radial-gradient(circle at 15% 35%, rgba(15, 76, 129, 0.06) 0%, transparent 45%),
    radial-gradient(circle at 85% 25%, rgba(212, 175, 55, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 45% 85%, rgba(15, 76, 129, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(212, 175, 55, 0.04) 0%, transparent 35%);
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
  padding: 2.5rem 3rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  grid-column: 1 / 2;
  grid-row: 1 / 2;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 
    0 4px 24px rgba(15, 76, 129, 0.1),
    0 8px 48px rgba(15, 76, 129, 0.08),
    0 0 0 1px rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  min-height: 500px;
  position: relative;
  animation: formSlideIn 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.form-wrapper::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-deep), var(--accent-gold), var(--primary-light));
  border-radius: 24px 24px 0 0;
}

@keyframes formSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 标题样式 */
.title {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-size: 1.75rem;
  color: var(--text-ink);
  margin-bottom: 24px;
  font-weight: 600;
  letter-spacing: -0.3px;
  text-align: center;
  position: relative;
}

.title::after {
  content: "";
  display: block;
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, var(--accent-gold), var(--accent-warm));
  margin: 12px auto 0;
  border-radius: 2px;
}

/* 输入框样式 */
.input-field {
  max-width: 380px;
  width: 100%;
  background: linear-gradient(135deg, #FAFAF8 0%, #F5F5F2 100%);
  margin: 12px 0;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  position: relative;
  transition: all var(--transition-base);
  box-shadow: 
    0 2px 8px rgba(15, 76, 129, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  box-sizing: border-box;
  overflow: hidden;
  background-clip: padding-box;
  border: 2px solid transparent;
}

.input-field:hover {
  box-shadow: 
    0 4px 16px rgba(15, 76, 129, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  transform: translateY(-1px);
}

.input-field:focus-within {
  border-color: var(--primary-medium);
  box-shadow: 
    0 4px 20px rgba(15, 76, 129, 0.12),
    0 0 0 4px rgba(15, 76, 129, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  background: #FFFFFF;
}

.input-field i {
  margin-right: 14px;
  color: var(--primary-medium);
  transition: all var(--transition-base);
  font-size: 1.15rem;
  z-index: 1;
}

.input-field:focus-within i {
  color: var(--accent-gold);
  transform: scale(1.1);
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
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 100%);
  border: none;
  outline: none;
  height: 56px;
  border-radius: 16px;
  color: var(--white-color);
  text-transform: uppercase;
  font-weight: 600;
  margin: 20px 0;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: 
    0 4px 16px rgba(15, 76, 129, 0.25),
    0 2px 8px rgba(15, 76, 129, 0.15);
  font-size: 1rem;
  letter-spacing: 1.5px;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s ease;
}

.btn:hover {
  background: linear-gradient(135deg, var(--primary-medium) 0%, var(--primary-light) 100%);
  box-shadow: 
    0 6px 24px rgba(15, 76, 129, 0.35),
    0 4px 12px rgba(15, 76, 129, 0.2);
  transform: translateY(-3px);
}

.btn:hover::before {
  left: 100%;
}

.btn:active {
  transform: translateY(-1px);
  box-shadow: 
    0 4px 16px rgba(15, 76, 129, 0.25),
    0 2px 8px rgba(15, 76, 129, 0.15);
}

/* 角色选择器样式 */
.role-selector {
  margin: 20px 0;
  width: 100%;
}

.role-group {
  display: flex;
  justify-content: space-between;
  width: 100%;
  background: transparent !important;
  border-radius: 16px;
  padding: 0;
  box-shadow: none;
  transition: all var(--transition-base);
  gap: 0;
  border: 2px solid rgba(15, 76, 129, 0.15);
  background: linear-gradient(135deg, #FAFAF8 0%, #F5F5F2 100%);
}

.role-group:hover {
  border-color: rgba(15, 76, 129, 0.3);
}

.role-selector :deep(.el-radio-group) {
  background: transparent !important;
  width: 100% !important;
  display: flex !important;
  border-radius: 14px !important;
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
  color: var(--text-slate) !important;
  border-radius: 0 !important;
  padding: 14px 8px !important;
  transition: all var(--transition-base) !important;
  font-size: 14px !important;
  font-weight: 600 !important;
  box-shadow: none !important;
  line-height: normal !important;
  height: auto !important;
  min-height: 52px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.role-selector :deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-radius: 14px 0 0 14px !important;
}

.role-selector :deep(.el-radio-button:last-child .el-radio-button__inner) {
  border-radius: 0 14px 14px 0 !important;
}

.role-selector :deep(.el-radio-button__inner:hover) {
  background: rgba(15, 76, 129, 0.06) !important;
  color: var(--primary-deep) !important;
}

.role-selector :deep(.el-radio-button.is-active .el-radio-button__inner) {
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 100%) !important;
  color: var(--white-color) !important;
  box-shadow: 0 4px 12px rgba(15, 76, 129, 0.25) !important;
}

.role-selector :deep(.el-radio-button__orig-radio:checked + .el-radio-button__inner) {
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 100%) !important;
  color: var(--white-color) !important;
}

.role-selector :deep(.el-radio-button--default.is-active .el-radio-button__inner) {
  border-color: transparent !important;
  color: var(--white-color) !important;
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 100%) !important;
}

.role-selector :deep(.el-radio-button--default:not(.is-active) .el-radio-button__inner) {
  border-color: transparent !important;
  color: var(--text-slate) !important;
  background: transparent !important;
}

.role-selector :deep(.el-radio-button + .el-radio-button) {
  margin-left: 0 !important;
  border-left: none !important;
}

.role-selector :deep(.el-radio-button.is-active) {
  z-index: 1;
}

.role-selector :deep(.el-radio-button.is-active + .el-radio-button .el-radio-button__inner) {
  border-left: none !important;
}

.role-selector :deep(.el-radio-button__inner) {
  --el-button-bg-color: transparent !important;
  --el-button-border-color: transparent !important;
  --el-button-hover-bg-color: rgba(15, 76, 129, 0.06) !important;
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
  transition: transform 0.9s cubic-bezier(0.4, 0, 0.2, 1);
  transition-delay: 0.6s;
  max-width: 320px;
}

.panel h3 {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-weight: 600;
  line-height: 1.3;
  font-size: 2rem;
  margin-bottom: 16px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.panel p {
  font-family: 'Source Sans 3', 'Noto Sans SC', sans-serif;
  font-size: 1rem;
  padding: 0.7rem 0;
  margin-bottom: 28px;
  line-height: 1.6;
  opacity: 0.92;
}

.btn.transparent {
  margin: 0;
  background: transparent;
  border: 2px solid rgba(255, 255, 255, 0.8);
  width: 140px;
  height: 48px;
  font-weight: 600;
  font-size: 0.95rem;
  color: var(--white-color);
  transition: all var(--transition-base);
  border-radius: 24px;
  letter-spacing: 0.5px;
}

.btn.transparent:hover {
  background: var(--white-color);
  color: var(--primary-deep);
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  border-color: var(--white-color);
}

/* 图片样式 */
.image {
  width: 100%;
  transition: transform 1.1s cubic-bezier(0.4, 0, 0.2, 1), opacity 1.1s cubic-bezier(0.4, 0, 0.2, 1);
  transition-delay: 0.4s;
  max-width: 320px;
  opacity: 0.95;
  filter: drop-shadow(0 10px 30px rgba(0, 0, 0, 0.15));
}

/* 右侧面板初始状态 */
.right-panel .image,
.right-panel .content {
  transform: translateX(800px);
}

/* 注册模式样式 */
.container.sign-up-mode::before {
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
  
  .image {
    max-width: 280px;
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
    transition: 1s 0.8s cubic-bezier(0.4, 0, 0.2, 1);
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
    width: 220px;
    transition: transform 0.9s cubic-bezier(0.4, 0, 0.2, 1);
    transition-delay: 0.6s;
  }
  
  .panel .content {
    padding-right: 15%;
    transition: transform 0.9s cubic-bezier(0.4, 0, 0.2, 1);
    transition-delay: 0.8s;
  }
  
  .panel h3 {
    font-size: 1.4rem;
  }
  
  .panel p {
    font-size: 0.9rem;
    padding: 0.5rem 0;
  }
  
  .btn.transparent {
    width: 120px;
    height: 42px;
    font-size: 0.85rem;
  }
  
  .container::before {
    width: 1500px;
    height: 1500px;
    transform: translateX(-50%);
    left: 30%;
    bottom: 68%;
    right: initial;
    top: initial;
    transition: 2s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .container.sign-up-mode::before {
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
    min-height: 480px;
    width: 100%;
    border-radius: 20px;
  }
  
  .form-wrapper::before {
    border-radius: 20px 20px 0 0;
  }
  
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
  
  .container::before {
    bottom: 72%;
    left: 50%;
  }
  
  .container.sign-up-mode::before {
    bottom: 28%;
    left: 50%;
  }
  
  .title {
    font-size: 1.5rem;
  }
  
  .input-field {
    margin: 10px 0;
    height: 52px;
    border-radius: 14px;
  }
  
  .btn {
    width: 160px;
    height: 50px;
    font-size: 0.9rem;
    border-radius: 14px;
  }
  
  .role-group {
    flex-direction: row;
    gap: 4px;
    border-radius: 14px;
  }
  
  .role-button {
    width: auto;
    flex: 1;
  }
  
  .panel h3 {
    font-size: 1.4rem;
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
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 100%) !important;
  border: none !important;
  color: var(--white-color) !important;
  height: 42px !important;
  padding: 0 18px !important;
  font-size: 0.85rem !important;
  border-radius: 12px !important;
  transition: all var(--transition-base) !important;
  box-shadow: 0 3px 12px rgba(15, 76, 129, 0.25) !important;
  font-weight: 600 !important;
}

.custom-send-code-btn:hover {
  background: linear-gradient(135deg, var(--primary-medium) 0%, var(--primary-light) 100%) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 5px 16px rgba(15, 76, 129, 0.35) !important;
}

/* 忘记密码对话框按钮样式 */
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px 0 0 0;
}

.dialog-footer .el-button {
  border-radius: 14px !important;
  padding: 12px 28px !important;
  font-weight: 600 !important;
  transition: all var(--transition-base) !important;
  min-width: 120px !important;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, var(--primary-deep) 0%, var(--primary-medium) 100%) !important;
  border: none !important;
  box-shadow: 0 4px 16px rgba(15, 76, 129, 0.25) !important;
  color: var(--white-color) !important;
}

.dialog-footer .el-button--primary:hover {
  background: linear-gradient(135deg, var(--primary-medium) 0%, var(--primary-light) 100%) !important;
  box-shadow: 0 6px 20px rgba(15, 76, 129, 0.35) !important;
  transform: translateY(-2px) !important;
}

.dialog-footer .el-button--default {
  background: transparent !important;
  border: 2px solid var(--primary-medium) !important;
  color: var(--primary-deep) !important;
  box-shadow: none !important;
}

.dialog-footer .el-button--default:hover {
  background: rgba(15, 76, 129, 0.06) !important;
  transform: translateY(-2px) !important;
  box-shadow: 0 4px 12px rgba(15, 76, 129, 0.15) !important;
}

/* 加载动画 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  animation: pulse 1.5s ease-in-out infinite;
}

/* 忘记密码链接样式 */
.forgot-password-container {
  text-align: center;
  margin-top: 20px;
  margin-bottom: 15px;
}

.forgot-password-container .el-link {
  color: var(--primary-medium) !important;
  font-weight: 500 !important;
  transition: all var(--transition-base);
  font-size: 0.9rem;
}

.forgot-password-container .el-link:hover {
  color: var(--accent-gold) !important;
}

.admin-register-notice {
  width: 100%;
  max-width: 380px;
  margin: 20px 0;
}

.admin-register-notice :deep(.el-alert) {
  border-radius: 12px;
  padding: 16px 20px;
}

.admin-register-notice :deep(.el-alert__title) {
  font-weight: 600;
  font-size: 1rem;
}

.admin-register-notice :deep(.el-alert__description) {
  margin-top: 6px;
  font-size: 0.9rem;
}
</style>