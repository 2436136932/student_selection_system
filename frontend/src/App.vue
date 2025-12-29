<template>
  <!-- 根据用户是否登录显示不同界面 -->
  <div v-if="isLoggedIn" class="app-container" :class="{ 'dark-mode': isDarkMode }">
    <el-container>
      <!-- 侧边栏导航 -->
      <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
        <div class="logo-container">
          <div class="logo-icon">
            <svg width="32" height="32" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M50 10L90 90H10L50 10Z" fill="white"/>
              <path d="M50 30L70 70H30L50 30Z" fill="#3498db"/>
              <path d="M50 50L60 60H40L50 50Z" fill="#2c3e50"/>
            </svg>
          </div>
          <h3 class="logo-text" v-if="!isCollapse">学生评奖评优系统</h3>
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
                <span class="menu-text" v-if="!isCollapse">{{ menu.text }}</span>
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
              <span class="menu-text" v-if="!isCollapse">{{ menu.text }}</span>
            </el-menu-item>
          </template>
        </el-menu>
        <div class="sidebar-toggle-btn" @click="toggleCollapse">
          <el-icon v-if="isCollapse"><ArrowRight /></el-icon>
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
                <span class="online-count">系统当前在线人数：{{ onlineUserCount }}</span>
                <span class="role-tag" :class="getRoleClass()">
                  {{ getRoleName() }}
                </span>
                <el-dropdown trigger="click" @command="handleDropdownCommand">
                  <div class="user-info">
                    <el-avatar 
                      size="small" 
                      :src="getFullAvatarUrl(userInfo.avatar)"
                      @error="handleAvatarError"
                    >
                      <el-icon v-if="!userInfo.avatar"><UserFilled /></el-icon>
                    </el-avatar>
                    <span class="user-name">{{ userInfo.name || userInfo.username }}</span>
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
                        <el-badge v-if="unreadMessageCount > 0" :value="unreadMessageCount" class="unread-badge" />
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
        <el-main class="main-content" :class="{ 'compact-mode': isCompactMode }">
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
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
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
import axios from 'axios'

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

// 紧凑模式
const isCompactMode = ref(false)

// 深色模式
const isDarkMode = ref(false)

// 未读消息数量
const unreadMessageCount = ref(0)

// 在线人数
const onlineUserCount = ref(0)

// 菜单定义，包含每个菜单的权限信息
const menuItems = [
  { index: '/home', icon: House, text: '首页', roles: ['admin', 'teacher', 'student'] },
  { index: '/students', icon: User, text: '学生管理', roles: ['admin', 'teacher'] },
  { index: '/teachers', icon: Briefcase, text: '教师管理', roles: ['admin'] },
  { index: '/courses', icon: School, text: '课程管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/majors', icon: School, text: '专业管理', roles: ['admin', 'teacher', 'student'] },
  { index: '/scores', icon: DataLine, text: '成绩管理', roles: ['admin', 'teacher', 'student'] },
  { 
    index: '/award-management', 
    icon: DocumentChecked, 
    text: '奖项管理', 
    roles: ['admin', 'teacher', 'student'],
    children: [
      { index: '/awards', icon: Medal, text: '评奖评优', roles: ['admin', 'teacher', 'student'] },
      { index: '/student-award-applications', icon: DocumentCopy, text: '奖项申请', roles: ['admin', 'teacher', 'student'] },
      { index: '/student-award-records', icon: DocumentChecked, text: '获奖记录', roles: ['admin', 'teacher', 'student'] }
    ]
  },
  { index: '/statistics', icon: DataLine, text: '数据统计', roles: ['admin', 'teacher'] },
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
  '/majors': '专业管理',
  '/scores': '成绩管理',
  '/awards': '评奖评优',
  '/student-award-applications': '奖项申请',
  '/student-award-records': '获奖记录',
  '/statistics': '数据统计',
  '/notices': '通知管理',
  '/users': '用户管理',
  '/carousel': '轮播图管理',
  '/profile': '个人中心',
  '/system-settings': '系统设置'
}

// 检查用户登录状态
const checkLoginStatus = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    isLoggedIn.value = true
    userInfo.value = JSON.parse(userInfoStr)
    // 登录状态下获取未读消息数量
    getUnreadMessageCount()
  } else {
    isLoggedIn.value = false
    userInfo.value = {}
  }
}

// 导入WebSocket服务
import { initializeWebSocket as initWebSocket, triggerWebSocketConnection, closeWebSocket } from './utils/websocketService'

// 组件挂载时检查登录状态和读取设置
onMounted(() => {
  checkLoginStatus()
  
  // 读取显示设置
  const savedDisplaySettings = localStorage.getItem('displaySettings')
  if (savedDisplaySettings) {
    try {
      const displaySettings = JSON.parse(savedDisplaySettings)
      isCompactMode.value = displaySettings.compactMode || false
      isDarkMode.value = displaySettings.darkMode || false
    } catch (e) {
      console.error('解析显示设置失败:', e)
    }
  }
  
  // 监听未读消息数量更新事件
  window.addEventListener('updateUnreadCount', getUnreadMessageCount)
  
  // 初始化WebSocket连接，用户一登录就建立连接
  initWebSocket()
  
  // 获取在线人数
  getOnlineUserCount()
  // 每30秒更新一次在线人数
  setInterval(getOnlineUserCount, 30000)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('updateUnreadCount', getUnreadMessageCount)
})

// 监听userInfo变化
watch(userInfo, (newVal, oldVal) => {
  if (newVal && newVal.username) {
    isLoggedIn.value = true
    // 用户登录，触发WebSocket连接
    console.log('检测到用户登录，初始化WebSocket连接')
    triggerWebSocketConnection()
  } else {
    isLoggedIn.value = false
    // 用户注销，关闭WebSocket连接
    console.log('检测到用户注销，关闭WebSocket连接')
    closeWebSocket()
  }
}, { deep: true })

// 监听localStorage变化
window.addEventListener('storage', (event) => {
  if (event.key === 'userInfo') {
    // 检查登录状态
    checkLoginStatus()
    
    // 获取最新的用户信息
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      // 用户已登录，触发WebSocket连接
      console.log('检测到用户登录，触发WebSocket连接')
      triggerWebSocketConnection()
    } else {
      // 用户已注销，关闭WebSocket连接
      console.log('检测到用户注销，关闭WebSocket连接')
      closeWebSocket()
    }
  } else if (event.key === 'displaySettings') {
    // 重新读取显示设置
    const savedDisplaySettings = localStorage.getItem('displaySettings')
    if (savedDisplaySettings) {
      try {
        const displaySettings = JSON.parse(savedDisplaySettings)
        isCompactMode.value = displaySettings.compactMode || false
        isDarkMode.value = displaySettings.darkMode || false
      } catch (e) {
        console.error('解析显示设置失败:', e)
      }
    }
  } else if (event.key === 'menuOrder') {
    // 菜单顺序发生变化，Vue会自动重新计算getVisibleMenus，无需手动刷新
    console.log('菜单顺序已更新')
  }
})

// 监听路由变化，当路由从登录页跳转到其他页面时，检查登录状态
watch(() => route.path, (newPath) => {
  checkLoginStatus()
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

// 获取完整的头像URL
const getFullAvatarUrl = (avatar) => {
  if (!avatar) return null
  // 如果是相对路径，添加后端域名和端口
  if (avatar.startsWith('/')) {
    return `http://localhost:8080${avatar}`
  }
  return avatar
}

// 处理头像加载错误
const handleAvatarError = (error) => {
  console.error('右上角头像加载失败：', error)
  // 头像加载失败时，不做特殊处理，让组件显示默认图标
}

// 获取未读消息数量
const getUnreadMessageCount = async () => {
  try {
    const response = await axios.get('/api/chats/unread-count', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    if (response.data) {
      unreadMessageCount.value = response.data.unreadCount || 0
    }
  } catch (error) {
    console.error('获取未读消息数量失败:', error)
    // 忽略错误，避免影响页面加载
  }
}

// 获取在线人数
const getOnlineUserCount = async () => {
  console.log('开始获取在线人数...')
  try {
    // 同时调用两个端点进行验证
    const [userIdsResponse, countResponse] = await Promise.all([
      axios.get('/api/chats/online-users'),
      axios.get('/api/chats/online-count')
    ]);
    
    console.log('获取在线用户ID成功，响应数据:', userIdsResponse.data);
    console.log('获取在线人数成功，响应数据:', countResponse.data);
    
    // 使用userIdsResponse.data.length获取在线人数
    onlineUserCount.value = userIdsResponse.data.length || 0;
    console.log('根据在线用户ID列表计算的在线人数:', onlineUserCount.value);
    console.log('直接获取的在线人数:', countResponse.data?.onlineCount);
    
    // 如果直接获取的在线人数不为空，使用它进行验证
    if (countResponse.data?.onlineCount !== undefined) {
      console.log('在线人数验证:', onlineUserCount.value === countResponse.data.onlineCount ? '一致' : '不一致');
    }
  } catch (error) {
    console.error('获取在线人数失败:', error);
    console.error('错误详情:', error.response?.status, error.response?.data);
    // 忽略错误，避免影响页面加载
  }
}

// 根据用户角色过滤可见菜单，并支持自定义顺序
const getVisibleMenus = computed(() => {
  const currentRole = userInfo.value.role
  // 先根据用户角色过滤菜单
  const filteredMenus = menuItems.filter(menu => menu.roles.includes(currentRole))
  
  // 读取localStorage中的菜单顺序
  const savedMenuOrder = localStorage.getItem('menuOrder')
  if (savedMenuOrder) {
    try {
      const menuOrder = JSON.parse(savedMenuOrder)
      // 创建菜单索引映射
      const menuMap = new Map()
      filteredMenus.forEach(menu => menuMap.set(menu.index, menu))
      
      // 按照保存的顺序排列菜单
      const orderedMenus = []
      menuOrder.forEach(index => {
        const menu = menuMap.get(index)
        if (menu) {
          orderedMenus.push(menu)
          menuMap.delete(index)
        }
      })
      
      // 添加剩余的菜单（未在配置中定义的菜单）
      menuMap.forEach(menu => orderedMenus.push(menu))
      
      return orderedMenus
    } catch (error) {
      console.error('解析菜单顺序失败:', error)
      return filteredMenus
    }
  }
  
  // 没有保存的顺序，返回默认顺序
  return filteredMenus
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
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')
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
  background-color: #2c3e50;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
  position: relative;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 65px;
  background-color: var(--el-color-primary);
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.sidebar-toggle-btn {
  cursor: pointer;
  color: white;
  font-size: 18px;
  padding: 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(255, 255, 255, 0.1);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebar-toggle-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
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
  min-height: calc(100vh - 65px);
  background-color: transparent;
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.9);
  --el-menu-active-color: white;
  --el-menu-hover-bg-color: rgba(52, 152, 219, 0.3);
}

/* 主菜单项样式 */
.sidebar-menu .el-menu-item,
.sidebar-menu .el-sub-menu__title {
  color: rgba(255, 255, 255, 0.9);
  height: 50px;
  line-height: 50px;
  padding: 0 25px;
  margin: 0 10px;
  border-radius: 8px;
  margin-bottom: 5px;
  transition: all 0.3s ease;
  background-color: transparent;
  border-bottom: none;
}

/* 主菜单项hover样式 */
.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-sub-menu__title:hover {
  background-color: rgba(52, 152, 219, 0.3);
  color: white;
}

/* 主菜单项激活样式 */
.sidebar-menu .el-menu-item.is-active {
  background-color: #3498db;
  color: white;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

/* 子菜单容器样式 */
.sidebar-menu .el-sub-menu {
  background-color: transparent;
}

/* 子菜单弹出样式 */
.sidebar-menu .el-sub-menu .el-menu {
  background-color: #2c3e50;
  border-radius: 8px;
  margin: 0 10px;
  padding: 5px 0;
}

/* 子菜单项样式 */
.sidebar-menu .el-sub-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.9);
  height: 45px;
  line-height: 45px;
  padding: 0 35px;
  margin: 0 5px;
  border-radius: 6px;
  margin-bottom: 2px;
  background-color: transparent;
}

/* 子菜单项hover样式 */
.sidebar-menu .el-sub-menu .el-menu-item:hover {
  background-color: rgba(52, 152, 219, 0.3);
  color: white;
}

/* 子菜单项激活样式 */
.sidebar-menu .el-sub-menu .el-menu-item.is-active {
  background-color: #3498db;
  color: white;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3);
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

/* 在线人数样式 */
.online-count {
  font-size: 14px;
  color: #606266;
  margin-right: 15px;
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
  min-height: 100%;
  height: 100%;
  position: relative;
  overflow: auto;
  margin: 0;
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
</style>