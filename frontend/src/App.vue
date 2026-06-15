<template>
  <!-- 根据用户是否登录显示不同界面 -->
  <div v-if="userStore.isLoggedIn" class="app-container" :class="{ 'dark-mode': appStore.isDarkMode }">
    <el-container>
      <!-- 侧边栏导航 -->
      <el-aside :width="appStore.sidebarWidth" class="sidebar">
        <div class="logo-container">
          <div class="logo-icon">
            <svg width="32" height="32" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M50 10L90 90H10L50 10Z" fill="white"/>
              <path d="M50 30L70 70H30L50 30Z" fill="#3498db"/>
              <path d="M50 50L60 60H40L50 50Z" fill="#2c3e50"/>
            </svg>
          </div>
          <h3 class="logo-text" v-if="!appStore.isCollapse">学生评奖评优系统</h3>
        </div>
        <el-menu
          :default-active="activeIndex"
          class="sidebar-menu"
          @select="handleMenuSelect"
          :router="true"
        >
          <!-- 普通菜单项 -->
          <template v-for="menu in getVisibleMenus" :key="menu.index">
            <!-- 下拉菜单项 -->
            <el-sub-menu v-if="menu.children" :index="menu.index">
              <template #title>
                <el-icon class="menu-icon"><component :is="menu.icon" /></el-icon>
                <span class="menu-text" v-if="!appStore.isCollapse">{{ menu.text }}</span>
              </template>
              <el-menu-item
                v-for="subMenu in menu.children"
                :key="subMenu.index"
                :index="subMenu.index"
                class="menu-item"
              >
                <el-icon class="menu-icon"><component :is="subMenu.icon" /></el-icon>
                <span class="menu-text" v-if="!isCollapse">{{ subMenu.text }}</span>
              </el-menu-item>
            </el-sub-menu>
            <!-- 普通菜单项 -->
            <el-menu-item v-else :index="menu.index" class="menu-item">
              <el-icon class="menu-icon"><component :is="menu.icon" /></el-icon>
              <span class="menu-text" v-if="!appStore.isCollapse">{{ menu.text }}</span>
            </el-menu-item>
          </template>
        </el-menu>
        <div class="sidebar-toggle-btn" @click="appStore.toggleCollapse()">
          <el-icon v-if="appStore.isCollapse"><ArrowRight /></el-icon>
          <el-icon v-else><ArrowLeft /></el-icon>
        </div>
      </el-aside>

      <!-- 主内容区域 -->
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="top-header">
          <div class="header-content">
            <div class="header-left">
            </div>
            <div class="header-right">
              <div class="header-info">
                <span class="online-count">系统当前在线人数：{{ appStore.onlineUserCount }}</span>
                <span class="role-tag" :class="getRoleClass()">
                  {{ userStore.roleName }}
                </span>
                <el-dropdown trigger="click" @command="handleDropdownCommand">
                  <div class="user-info">
                    <el-avatar
                      size="small"
                      :src="getFullAvatarUrl(userStore.userInfo.avatar)"
                      @error="handleAvatarError"
                    >
                      <el-icon v-if="!userStore.userInfo.avatar"><UserFilled /></el-icon>
                    </el-avatar>
                    <span class="user-name">{{ userStore.displayName }}</span>
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="profile">
                        <el-icon><user /></el-icon>
                        个人中心
                      </el-dropdown-item>
                      <el-dropdown-item command="chat">
                        <el-icon><ChatLineRound /></el-icon>
                        聊天中心
                        <el-badge v-if="appStore.unreadMessageCount > 0" :value="appStore.unreadMessageCount" class="unread-badge" />
                      </el-dropdown-item>
                      <el-dropdown-item command="settings">
                        <el-icon><setting /></el-icon>
                        系统设置
                      </el-dropdown-item>
                      <el-dropdown-item divided command="logout">
                        <el-icon><switch-button /></el-icon>
                        退出登录
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="main-content" :class="{ 'compact-mode': appStore.isCompactMode }">
          <div class="content-wrapper">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
  <!-- 未登录时只显示路由内容（登录页面） -->
  <div v-else>
    <router-view />
  </div>

  <!-- 节日装饰层 -->
  <HolidayDecoration :theme="appStore.currentHolidayTheme" />

  <!-- 主题状态指示器 -->
  <div v-if="appStore.themeStatusText" class="theme-status-indicator" :class="{ 'solemn': appStore.isSolemn }">
    <el-tooltip :content="appStore.themeStatusText" placement="bottom">
      <div class="theme-indicator-dot"></div>
    </el-tooltip>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  User,
  UserFilled,
  Briefcase,
  Document,
  DocumentChecked,
  DocumentCopy,
  School,
  DataLine,
  Medal,
  Menu,
  ArrowDown,
  ArrowLeft,
  ArrowRight,
  Setting,
  SwitchButton,
  Bell,
  PictureRounded,
  ChatDotRound,
  ChatLineRound
} from '@element-plus/icons-vue'
import HolidayDecoration from './components/HolidayDecoration.vue'
import axios from 'axios'
import { useUserStore } from './store/user'
import { useAppStore } from './store/app'
import { useChatStore } from './store/chat'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()
const chatStore = useChatStore()

// 当前激活的菜单项
const activeIndex = computed(() => route.path)

// 菜单定义，包含每个菜单的权限信息
const menuItems = [
  { index: '/home', icon: House, text: '首页', roles: ['admin', 'teacher', 'student'] },
  { index: '/students', icon: User, text: '学生管理', roles: ['admin', 'teacher'] },
  { index: '/teachers', icon: Briefcase, text: '教师管理', roles: ['admin'] },
  { index: '/courses', icon: School, text: '课程管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/majors', icon: School, text: '专业管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/scores', icon: DataLine, text: '评分管理', roles: ['admin', 'teacher', 'student'] },
  {
    index: '/award-management',
    icon: DocumentChecked,
    text: '奖项管理',
    roles: ['admin', 'teacher', 'student'],
    children: [
      { index: '/awards', icon: Medal, text: '评奖评优', roles: ['admin', 'teacher', 'student'] },
      { index: '/student-award-applications', icon: DocumentCopy, text: '奖项申请', roles: ['admin', 'teacher', 'student'] },
      { index: '/student-award-records', icon: DocumentChecked, text: '获奖记录', roles: ['admin', 'teacher', 'student'] },
      { index: '/award-recommendation', icon: Medal, text: 'AI智能推荐', roles: ['student'] }
    ]
  },
  { index: '/statistics', icon: DataLine, text: '数据统计', roles: ['admin', 'teacher'] },
  { index: '/notices', icon: Bell, text: '通知管理', roles: ['admin'] },
  { index: '/users', icon: UserFilled, text: '用户管理', roles: ['admin'] },
  { index: '/carousel', icon: PictureRounded, text: '轮播图管理', roles: ['admin'] }
]

let onlineCountTimer = null

onMounted(() => {
  appStore.initDisplaySettings()
  appStore.initHolidayTheme()

  if (userStore.isLoggedIn) {
    chatStore.initWebSocket()
    appStore.fetchUnreadMessageCount()
    appStore.fetchOnlineUserCount()
    onlineCountTimer = setInterval(() => appStore.fetchOnlineUserCount(), 30000)
  }
})

onUnmounted(() => {
  if (onlineCountTimer) {
    clearInterval(onlineCountTimer)
  }
})

// 监听登录状态变化，触发WebSocket连接/断开
watch(() => userStore.isLoggedIn, (loggedIn) => {
  if (loggedIn) {
    chatStore.triggerConnection()
    appStore.fetchUnreadMessageCount()
    appStore.fetchOnlineUserCount()
    if (!onlineCountTimer) {
      onlineCountTimer = setInterval(() => appStore.fetchOnlineUserCount(), 30000)
    }
  } else {
    chatStore.closeConnection()
    if (onlineCountTimer) {
      clearInterval(onlineCountTimer)
      onlineCountTimer = null
    }
  }
})

// 菜单选择事件处理
const handleMenuSelect = (key) => {
  router.push(key)
}

// 获取完整的头像URL
const getFullAvatarUrl = (avatar) => {
  if (!avatar) return null
  if (avatar.startsWith('/')) {
    return `${axios.defaults.baseURL}${avatar}`
  }
  return avatar
}

// 处理头像加载错误
const handleAvatarError = () => {}

// 根据用户角色过滤可见菜单，并支持自定义顺序
const getVisibleMenus = computed(() => {
  const currentRole = userStore.role
  const filteredMenus = menuItems.filter(menu => menu.roles.includes(currentRole))

  const savedMenuOrder = localStorage.getItem('menuOrder')
  if (savedMenuOrder) {
    try {
      const menuOrder = JSON.parse(savedMenuOrder)
      const menuIndexMap = new Map()
      filteredMenus.forEach(menu => menuIndexMap.set(menu.index, menu))

      const orderedMenus = []
      menuOrder.forEach(index => {
        const menu = menuIndexMap.get(index)
        if (menu) {
          orderedMenus.push(menu)
          menuIndexMap.delete(index)
        }
      })
      menuIndexMap.forEach(menu => orderedMenus.push(menu))
      return orderedMenus
    } catch (error) {
      return filteredMenus
    }
  }
  return filteredMenus
})

// 获取角色样式类
const getRoleClass = () => {
  const roleClassMap = {
    'admin': 'role-admin',
    'teacher': 'role-teacher',
    'student': 'role-student'
  }
  return roleClassMap[userStore.role] || ''
}

// 下拉菜单命令处理
const handleDropdownCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'chat':
      router.push('/chat')
      break
    case 'settings':
      router.push('/system-settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  userStore.logout()
  chatStore.closeConnection()
  ElMessage.success('退出登录成功')
  router.push('/')
}
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', 'Noto Sans SC', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  background-color: #f8f9fa;
  color: #2c3e50;
  font-size: 14px;
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 统一标题样式 */
h1, h2, h3, h4, h5, h6 {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  color: #2c3e50;
  font-weight: 700;
  margin-top: 0;
  margin-bottom: 16px;
}

h1 {
  font-size: 28px;
  letter-spacing: -0.5px;
}

h2 {
  font-size: 24px;
  letter-spacing: -0.3px;
}

h3 {
  font-size: 20px;
  letter-spacing: -0.2px;
}

/* 统一正文段落样式 */
p {
  margin-bottom: 16px;
  color: #333;
}

/* 统一链接样式 */
a {
  color: #3498db;
  text-decoration: none;
  transition: color 0.3s ease;
}

a:hover {
  color: #2980b9;
  text-decoration: underline;
}

/* 统一按钮文字样式 */
.el-button {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 统一表单元素文字样式 */
.el-input__inner, .el-select__input, .el-textarea__inner {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  font-size: 14px;
  color: #303133;
}

/* 统一标签页文字样式 */
.el-tabs__item {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  font-weight: 500;
}

/* 统一表格文字样式 */
.el-table {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
}

.el-table__header-wrapper th {
  font-weight: 700;
  color: #2c3e50;
}

/* 统一卡片文字样式 */
.el-card__header {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
}

/* 统一面包屑文字样式 */
.el-breadcrumb__item {
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
}

/* 统一通知文字样式 */
.el-message {
  font-family: 'Inter', 'Noto Sans SC', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  border-radius: 14px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideInRight 0.3s ease;
}

.el-notification {
  border-radius: 14px !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideInRight 0.3s ease;
}

/* 通知堆叠显示 */
.el-notification__group {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.el-notification__wrapper {
  animation: slideInRight 0.3s ease;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 应用容器 */
.app-container {
  transition: all 0.3s ease;
  margin: 0;
  padding: 0;
  height: 100vh;
}

/* 确保html和body没有默认边距 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow: hidden;
}

/* 确保el-container和el-main充满可用空间 */
.el-container {
  height: 100vh;
  overflow: hidden;
}

.el-main {
  padding: 0;
  overflow: auto;
  height: 100%;
}

/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, #4A90E2 0%, #3D7BCB 50%, #2E6B9A 100%);
  box-shadow: 4px 0 24px rgba(74, 144, 226, 0.15);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  position: relative;
}

.sidebar::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 20%, rgba(255, 176, 138, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(91, 143, 249, 0.08) 0%, transparent 50%);
  pointer-events: none;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 65px;
  background: linear-gradient(135deg, rgba(255, 176, 138, 0.15) 0%, rgba(255, 154, 138, 0.08) 100%);
  padding: 0 20px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.15);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.logo-container::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 176, 138, 0.4), transparent);
}

.sidebar-toggle-btn {
  cursor: pointer;
  color: rgba(255, 255, 255, 0.85);
  font-size: 18px;
  padding: 10px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.1);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.sidebar-toggle-btn:hover {
  background: rgba(255, 176, 138, 0.2);
  color: #FFB08A;
  border-color: rgba(255, 176, 138, 0.3);
  transform: translateX(-50%) scale(1.05);
}

.logo-icon {
  margin-right: 12px;
}

.logo-text {
  font-family: 'Playfair Display', 'Noto Serif SC', Georgia, serif;
  font-size: 15px;
  font-weight: 600;
  color: white;
  letter-spacing: 0.5px;
}

/* 侧边栏菜单 */
.sidebar-menu {
  border-right: none;
  min-height: calc(100vh - 65px);
  background-color: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.9);
  --el-menu-active-color: white;
  --el-menu-hover-bg-color: rgba(255, 176, 138, 0.2);
}

.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: rgba(255, 255, 255, 0.9);
  height: 50px;
  line-height: 50px;
  padding: 0 22px;
  margin: 0 12px;
  border-radius: 10px;
  margin-bottom: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background-color: transparent;
  border-bottom: none;
  position: relative;
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background: linear-gradient(135deg, rgba(255, 176, 138, 0.2) 0%, rgba(255, 154, 138, 0.12) 100%);
  color: white;
  transform: translateX(4px);
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #FFB08A 0%, #FF9A8B 100%);
  color: #2C3E50;
  box-shadow: 0 4px 12px rgba(255, 176, 138, 0.3);
  font-weight: 600;
}

.sidebar-menu .el-menu-item.is-active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #2C3E50;
  border-radius: 0 2px 2px 0;
}

.sidebar-menu .el-sub-menu {
  background-color: transparent;
}

.sidebar-menu .el-sub-menu .el-menu {
  background: rgba(46, 107, 154, 0.95);
  border-radius: 10px;
  margin: 0 12px;
  padding: 8px 0;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.15);
}

.sidebar-menu .el-sub-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.9);
  height: 44px;
  line-height: 44px;
  padding: 0 32px;
  margin: 0 8px;
  border-radius: 8px;
  margin-bottom: 2px;
  background-color: transparent;
}

.sidebar-menu .el-sub-menu .el-menu-item:hover {
  background: rgba(255, 176, 138, 0.15);
  color: white;
}

.sidebar-menu .el-sub-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, #FFB08A 0%, #FF9A8B 100%);
  color: #2C3E50;
  box-shadow: 0 2px 8px rgba(255, 176, 138, 0.25);
  font-weight: 600;
}

.menu-icon {
  font-size: 18px;
  margin-right: 12px;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
}

/* 顶部导航栏 */
.top-header {
  background: linear-gradient(180deg, #FFFFFF 0%, #F8F9FA 100%);
  border-bottom: 1px solid rgba(74, 144, 226, 0.1);
  padding: 0 25px;
  height: 65px;
  box-shadow: 0 2px 16px rgba(74, 144, 226, 0.06);
  position: relative;
}

.top-header::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, #4A90E2, #FFB08A, #4A90E2);
  opacity: 0.4;
}

/* 紧凑模式 */
.compact-mode .el-card {
  margin: 5px 0;
  padding: 10px;
}

.compact-mode .el-form-item {
  margin-bottom: 10px;
}

.compact-mode .el-button {
  margin: 2px;
}

.compact-mode .el-input {
  margin: 2px 0;
}

.compact-mode .setting-item-card {
  margin-top: 10px !important;
}

/* 深色模式 */
.dark-mode {
  background-color: #1a1a1a;
  color: #ffffff;
}

.dark-mode .top-header {
  background-color: #2c3e50;
  border-bottom: 1px solid #34495e;
}

.dark-mode .main-content {
  background-color: #1a1a1a;
}

.dark-mode .content-wrapper {
  background-color: #1a1a1a;
}

.dark-mode .el-card {
  background-color: #2c3e50;
  border: 1px solid #34495e;
  color: #ffffff;
}

.dark-mode .el-card__header {
  background-color: #34495e;
  border-bottom: 1px solid #34495e;
}

.dark-mode .el-form-item__label {
  color: #ffffff;
}

.dark-mode .el-input__inner {
  background-color: #34495e;
  border: 1px solid #4a637b;
  color: #ffffff;
}

.dark-mode .el-input__inner::placeholder {
  color: #95a5a6;
}

.dark-mode .el-button {
  background-color: #34495e;
  border: 1px solid #4a637b;
  color: #ffffff;
}

.dark-mode .el-button:hover {
  background-color: #4a637b;
}

.dark-mode .el-button--primary {
  background-color: var(--el-color-primary);
  border: 1px solid var(--el-color-primary);
}

.dark-mode .sidebar {
  background-color: #2c3e50;
}

.dark-mode .sidebar-menu {
  background-color: #2c3e50;
}

.dark-mode .sidebar-menu .el-menu-item {
  color: #ecf0f1;
}

.dark-mode .sidebar-menu .el-menu-item:hover {
  background-color: #34495e;
}

.dark-mode .sidebar-menu .el-menu-item.is-active {
  background-color: var(--el-color-primary);
  color: white;
}

.dark-mode .breadcrumb {
  color: #ecf0f1;
}

.dark-mode .breadcrumb .el-breadcrumb__inner {
  color: #ecf0f1;
}

.dark-mode .el-tag {
  background-color: #34495e;
  border: 1px solid #4a637b;
  color: #ffffff;
}

.dark-mode .el-switch__core {
  background-color: #4a637b;
}

.dark-mode .el-switch.is-checked .el-switch__core {
  background-color: var(--el-color-primary);
}

.dark-mode .el-radio-button__inner {
  background-color: #34495e;
  border-color: #4a637b;
  color: #ffffff;
}

.dark-mode .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  color: #ffffff;
}

.dark-mode .el-dropdown-menu {
  background-color: #2c3e50;
  border: 1px solid #34495e;
}

.dark-mode .el-dropdown-item {
  color: #ecf0f1;
}

.dark-mode .el-dropdown-item:hover {
  background-color: #34495e;
}

.dark-mode .role-tag {
  background-color: #34495e;
  color: #ffffff;
  border: 1px solid #4a637b;
}

.dark-mode .el-table {
  background-color: #2c3e50;
  color: #ffffff;
}

.dark-mode .el-table__header-wrapper th {
  background-color: #34495e;
  color: #ffffff;
  border-bottom: 1px solid #4a637b;
}

.dark-mode .el-table__body-wrapper td {
  background-color: #2c3e50;
  color: #ffffff;
  border-bottom: 1px solid #34495e;
}

.dark-mode .el-table__body-wrapper tr:hover td {
  background-color: #34495e;
}

.dark-mode .el-pagination {
  color: #ffffff;
}

.dark-mode .el-pagination__sizes .el-input__inner {
  background-color: #34495e;
  border: 1px solid #4a637b;
  color: #ffffff;
}

.dark-mode .el-pagination__button {
  background-color: #34495e;
  border: 1px solid #4a637b;
  color: #ffffff;
}

.dark-mode .el-pagination__button:hover {
  background-color: #4a637b;
}

.dark-mode .el-pagination__button--active {
  background-color: var(--el-color-primary);
  border: 1px solid var(--el-color-primary);
}

.dark-mode .el-dialog {
  background-color: #2c3e50;
  border: 1px solid #34495e;
  color: #ffffff;
}

.dark-mode .el-dialog__header {
  background-color: #34495e;
  border-bottom: 1px solid #34495e;
}

.dark-mode .el-dialog__body {
  background-color: #2c3e50;
  color: #ffffff;
}

.dark-mode .el-upload {
  border: 1px dashed #4a637b;
}

.dark-mode .el-upload-dragger {
  background-color: #34495e;
  border: 1px dashed #4a637b;
}

.dark-mode .el-upload-dragger:hover {
  border-color: var(--el-color-primary);
}

.dark-mode .avatar-uploader {
  border: 1px dashed #4a637b;
  background-color: #34495e;
}

.dark-mode .avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  color: #5A6C7D;
  transition: color 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.toggle-btn:hover {
  color: #4A90E2;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 在线人数样式 */
.online-count {
  font-size: 13px;
  color: #5A6C7D;
  margin-right: 15px;
  padding: 6px 14px;
  background: linear-gradient(135deg, #F8F9FA 0%, #F0F2F5 100%);
  border-radius: 20px;
  border: 1px solid rgba(74, 144, 226, 0.1);
}

/* 角色标签 */
.role-tag {
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  color: white;
  letter-spacing: 0.3px;
}

.role-admin {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8787 100%);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.role-teacher {
  background: linear-gradient(135deg, #52C41A 0%, #73D13D 100%);
  box-shadow: 0 2px 8px rgba(82, 196, 26, 0.3);
}

.role-student {
  background: linear-gradient(135deg, #4A90E2 0%, #5BA3F5 100%);
  box-shadow: 0 2px 8px rgba(74, 144, 226, 0.3);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 14px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: transparent;
  border: 1px solid transparent;
}

.user-info:hover {
  background: linear-gradient(135deg, #F8F9FA 0%, #F0F2F5 100%);
  border-color: rgba(74, 144, 226, 0.1);
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #2C3E50;
}

/* 主内容区域 */
.main-content {
  padding: 24px;
  background: linear-gradient(135deg, #F8F9FA 0%, #F0F2F5 50%, #E8E8F0 100%);
  min-height: 100%;
  height: 100%;
  position: relative;
  overflow: auto;
  margin: 0;
}

.main-content::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 10% 20%, rgba(74, 144, 226, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 90% 80%, rgba(255, 176, 138, 0.03) 0%, transparent 50%);
  pointer-events: none;
}

/* 聊天中心页面样式 */
.main-content:has(.chat-container) {
  padding: 0;
  background-color: transparent;
  min-height: 100%;
  height: 100%;
  margin: 0;
}

.main-content:has(.chat-container) .content-wrapper {
  padding: 0;
  background-color: transparent;
  border-radius: 0;
  box-shadow: none;
  min-height: 100%;
  height: 100%;
  width: 100%;
  display: flex;
  align-items: stretch;
  margin: 0;
  overflow: hidden;
}

.content-wrapper {
  background-color: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 25px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08), 0 8px 32px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  min-height: calc(100vh - 65px);
  margin: 0;
}

.content-wrapper:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12), 0 16px 48px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .sidebar {
    width: 60px !important;
  }
  
  .logo-text {
    display: none;
  }
  
  .menu-text {
    display: none;
  }
  
  .sidebar-menu .el-menu-item {
    padding: 0 20px;
  }
  
  .menu-icon {
    margin-right: 0;
  }
}

/* 主题状态指示器 */
.theme-status-indicator {
  position: fixed;
  top: 70px;
  right: 20px;
  z-index: 9999;
}

.theme-indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--holiday-primary, #4A90E2) 0%, var(--holiday-accent, #FFB08A) 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-indicator-dot:hover {
  transform: scale(1.3);
}

.theme-status-indicator.solemn .theme-indicator-dot {
  background: linear-gradient(135deg, #6B6B6B 0%, #888888 100%);
}
</style>