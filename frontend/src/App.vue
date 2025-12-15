<template>
  <!-- 根据用户是否登录显示不同界面 -->
  <div v-if="isLoggedIn" class="app-container">
    <el-container style="height: 100vh;">
      <!-- 侧边栏导航 -->
      <el-aside width="220px" class="sidebar">
        <div class="logo-container">
          <div class="logo-icon">
            <svg width="32" height="32" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M50 10L90 90H10L50 10Z" fill="white"/>
              <path d="M50 30L70 70H30L50 30Z" fill="#3498db"/>
              <path d="M50 50L60 60H40L50 50Z" fill="#2c3e50"/>
            </svg>
          </div>
          <h3 class="logo-text">学生评奖评优系统</h3>
        </div>
        <el-menu
          :default-active="activeIndex"
          class="sidebar-menu"
          @select="handleMenuSelect"
          :router="true"
        >
          <el-menu-item
            v-for="menu in getVisibleMenus"
            :key="menu.index"
            :index="menu.index"
            class="menu-item"
          >
            <el-icon class="menu-icon"><component :is="menu.icon" /></el-icon>
            <span class="menu-text">{{ menu.text }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主内容区域 -->
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="top-header">
          <div class="header-content">
            <div class="header-left">
              <el-icon class="toggle-btn" @click="toggleCollapse"><menu /></el-icon>
              <div class="breadcrumb">
                <el-breadcrumb separator="/" separator-class="el-icon-right">
                  <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
                  <el-breadcrumb-item>{{ getCurrentMenuName() }}</el-breadcrumb-item>
                </el-breadcrumb>
              </div>
            </div>
            <div class="header-right">
              <div class="header-info">
                <span class="role-tag" :class="getRoleClass()">
                  {{ getRoleName() }}
                </span>
                <el-dropdown trigger="click" @command="handleDropdownCommand">
                  <div class="user-info">
                    <el-avatar size="small" :icon="userInfo.avatar || 'el-icon-user'"></el-avatar>
                    <span class="user-name">{{ userInfo.name || userInfo.username }}</span>
                    <el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="profile">
                        <el-icon><user /></el-icon>
                        个人中心
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
        <el-main class="main-content">
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
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
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
  Setting,
  SwitchButton,
  Bell,
  PictureRounded
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 用户登录状态
const isLoggedIn = ref(false)

// 用户信息
const userInfo = ref({})

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单项
const activeIndex = computed(() => route.path)

// 菜单定义，包含每个菜单的权限信息
const menuItems = [
  { index: '/home', icon: House, text: '首页', roles: ['admin', 'teacher', 'student'] },
  { index: '/students', icon: User, text: '学生管理', roles: ['admin'] },
  { index: '/teachers', icon: Briefcase, text: '教师管理', roles: ['admin'] },
  { index: '/courses', icon: School, text: '课程管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/standards', icon: DocumentChecked, text: '评奖标准管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/scores', icon: DataLine, text: '成绩管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/awards', icon: Medal, text: '评奖评优', roles: ['admin', 'teacher', 'student'] },
  { index: '/student-award-applications', icon: DocumentCopy, text: '奖项申请', roles: ['admin', 'teacher', 'student'] },
  { index: '/notices', icon: Bell, text: '通知管理', roles: ['admin'] },
  { index: '/users', icon: UserFilled, text: '用户管理', roles: ['admin'] },
  { index: '/carousel', icon: PictureRounded, text: '轮播图管理', roles: ['admin'] }
]

// 菜单映射
const menuMap = {
  '/home': '首页',
  '/students': '学生管理',
  '/teachers': '教师管理',
  '/courses': '课程管理',
  '/standards': '评奖标准管理',
  '/scores': '成绩管理',
  '/awards': '评奖评优',
  '/student-award-applications': '奖项申请',
  '/notices': '通知管理',
  '/users': '用户管理',
  '/carousel': '轮播图管理'
}

// 检查用户登录状态
const checkLoginStatus = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    isLoggedIn.value = true
    userInfo.value = JSON.parse(userInfoStr)
  } else {
    isLoggedIn.value = false
    userInfo.value = {}
  }
}

// 组件挂载时检查登录状态
onMounted(() => {
  checkLoginStatus()
})

// 监听userInfo变化
watch(userInfo, (newVal) => {
  if (newVal && newVal.username) {
    isLoggedIn.value = true
  } else {
    isLoggedIn.value = false
  }
}, { deep: true })

// 监听localStorage变化
window.addEventListener('storage', (event) => {
  if (event.key === 'userInfo') {
    checkLoginStatus()
  }
})

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 菜单选择事件处理
const handleMenuSelect = (key, keyPath) => {
  router.push(key)
}

// 获取当前菜单名称
const getCurrentMenuName = () => {
  return menuMap[route.path] || ''
}

// 获取角色名称
const getRoleName = () => {
  const roleMap = {
    'admin': '管理员',
    'teacher': '教师',
    'student': '学生'
  }
  return roleMap[userInfo.value.role] || '用户'
}

// 获取可见菜单
const getVisibleMenus = computed(() => {
  const currentRole = userInfo.value.role
  return menuItems.filter(menu => menu.roles.includes(currentRole))
})

// 获取角色样式类
const getRoleClass = () => {
  const roleClassMap = {
    'admin': 'role-admin',
    'teacher': 'role-teacher',
    'student': 'role-student'
  }
  return roleClassMap[userInfo.value.role] || ''
}

// 下拉菜单命令处理
const handleDropdownCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能开发中')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('userInfo')
  isLoggedIn.value = false
  userInfo.value = {}
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
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  background-color: #f5f7fa;
  color: #333;
  font-size: 14px;
  line-height: 1.5;
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
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
}

/* 应用容器 */
.app-container {
  transition: all 0.3s ease;
}

/* 侧边栏样式 */
.sidebar {
  background-color: #2c3e50;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
}

.logo-container {
  display: flex;
  align-items: center;
  height: 65px;
  background-color: #3498db;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.logo-icon {
  margin-right: 12px;
}

.logo-text {
  font-size: 16px;
  font-weight: 700;
  color: white;
  letter-spacing: 0.5px;
}

/* 侧边栏菜单 */
.sidebar-menu {
  border-right: none;
  height: calc(100vh - 65px);
  background-color: transparent;
}

.sidebar-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.9);
  height: 50px;
  line-height: 50px;
  padding: 0 25px;
  margin: 0 10px;
  border-radius: 8px;
  margin-bottom: 5px;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(52, 152, 219, 0.3);
  color: white;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #3498db;
  color: white;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
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
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 25px;
  height: 65px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
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
  color: #606266;
  transition: color 0.3s ease;
}

.toggle-btn:hover {
  color: #3498db;
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

/* 角色标签 */
.role-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.role-admin {
  background-color: #e74c3c;
}

.role-teacher {
  background-color: #27ae60;
}

.role-student {
  background-color: #3498db;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

/* 主内容区域 */
.main-content {
  padding: 25px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 65px);
}

.content-wrapper {
  background-color: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
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
</style>