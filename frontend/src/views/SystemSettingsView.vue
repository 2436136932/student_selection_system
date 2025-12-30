<template>
  <div class="settings-container">
    <el-card class="settings-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </div>
      </template>

      <div class="settings-content">
        <!-- 颜色主题设置 -->
        <el-card class="setting-item-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Brush /></el-icon>
              <span>颜色主题</span>
            </div>
          </template>

          <div class="color-theme-section">
            <div class="theme-description">
              <p>选择您喜欢的系统主题颜色，实时预览效果</p>
            </div>
            
            <div class="theme-preview">
              <div class="preview-box" :style="themePreviewStyle">
                <div class="preview-header"></div>
                <div class="preview-content">
                  <div class="preview-button"></div>
                </div>
              </div>
            </div>
            
            <div class="theme-options">
              <el-radio-group v-model="selectedTheme" @change="handleThemeChange">
                <el-radio-button v-for="theme in themeOptions" :key="theme.name" :label="theme.name">
                  <div class="theme-option-item">
                    <div class="theme-color" :style="{ backgroundColor: theme.color }"></div>
                    <span>{{ theme.label }}</span>
                  </div>
                </el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </el-card>

        <!-- 显示设置 -->
        <el-card class="setting-item-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>显示设置</span>
            </div>
          </template>

          <div class="display-settings-section">
            <el-form :model="displaySettings" label-position="top" class="display-form">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="紧凑模式">
                    <el-switch v-model="displaySettings.compactMode" />
                    <span class="setting-desc">减少组件间距，适合小屏设备</span>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="深色模式">
                    <el-switch v-model="displaySettings.darkMode" />
                    <span class="setting-desc">切换深色/浅色主题</span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-card>

        <!-- 电脑信息 -->
        <el-card class="setting-item-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>电脑信息</span>
            </div>
          </template>

          <div class="computer-info-section">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="操作系统">{{ computerInfo.os }}</el-descriptions-item>
              <el-descriptions-item label="浏览器">{{ computerInfo.browser }}</el-descriptions-item>
              <el-descriptions-item label="屏幕分辨率">{{ computerInfo.screen }}</el-descriptions-item>
              <el-descriptions-item label="语言">{{ computerInfo.language }}</el-descriptions-item>
              <el-descriptions-item label="网络状态">{{ computerInfo.online ? '在线' : '离线' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 菜单顺序管理 -->
        <el-card class="setting-item-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <el-icon><Menu /></el-icon>
              <span>菜单顺序管理</span>
            </div>
          </template>

          <div class="menu-order-section">
            <div class="menu-order-description">
              <p>拖拽调整左侧菜单栏的显示顺序</p>
            </div>
            
            <div class="menu-order-container" ref="menuOrderContainer">
              <div 
                v-for="menu in menuItems" 
                :key="menu.index" 
                class="menu-item-order"
                :data-index="menu.index"
              >
                <el-icon class="drag-icon"><Menu /></el-icon>
                <div class="menu-item-content">
                  <el-icon class="menu-item-icon"><component :is="menu.icon" /></el-icon>
                  <span class="menu-item-text">{{ menu.text }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 登录信息 -->
        <el-card class="setting-item-card" shadow="hover" style="margin-top: 20px;">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>登录信息</span>
            </div>
          </template>

          <div class="login-info-section">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
              <el-descriptions-item label="角色">{{ userInfo.role || '-' }}</el-descriptions-item>
              <el-descriptions-item label="登录时间">{{ loginInfo.loginTime }}</el-descriptions-item>
              <el-descriptions-item label="上次登录">{{ loginInfo.lastLoginTime }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <!-- 操作按钮 -->
        <div class="settings-actions">
          <el-button type="primary" @click="saveSettings">保存设置</el-button>
          <el-button @click="resetSettings">恢复默认</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Setting, Brush, Monitor, User, Menu, 
  House, User as UserIcon, Briefcase, School, DataLine, 
  DocumentChecked, Medal, DocumentCopy, Bell, UserFilled, 
  PictureRounded
} from '@element-plus/icons-vue'
import { getUserInfo } from '../utils/role'
import Sortable from 'sortablejs'

// 主题选项
const themeOptions = ref([
  { name: 'default', label: '默认蓝色', color: '#409eff' },
  { name: 'green', label: '绿色', color: '#67c23a' },
  { name: 'purple', label: '紫色', color: '#909399' },
  { name: 'red', label: '红色', color: '#f56c6c' },
  { name: 'orange', label: '橙色', color: '#e6a23c' },
  { name: 'teal', label: '青色', color: '#409eff' }
])

// 用户信息
const userInfo = ref({})

// 电脑信息
const computerInfo = ref({
  browser: navigator.userAgent,
  os: navigator.platform,
  screen: `${window.screen.width}x${window.screen.height}`,
  language: navigator.language,
  online: navigator.onLine
})

// 登录信息
const loginInfo = ref({
  loginTime: localStorage.getItem('loginTime') || '未知',
  lastLoginTime: localStorage.getItem('lastLoginTime') || '未知'
})

// 菜单顺序管理
// 菜单定义，与App.vue保持一致
const menuItems = ref([
  { index: '/home', icon: House, text: '首页', roles: ['admin', 'teacher', 'student'] },
  { index: '/students', icon: UserIcon, text: '学生管理', roles: ['admin', 'teacher'] },
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
      { index: '/student-award-records', icon: DocumentChecked, text: '获奖记录', roles: ['admin', 'teacher', 'student'] }
    ]
  },
  { index: '/statistics', icon: DataLine, text: '数据统计', roles: ['admin', 'teacher'] },
  { index: '/notices', icon: Bell, text: '通知管理', roles: ['admin'] },
  { index: '/users', icon: UserFilled, text: '用户管理', roles: ['admin'] },
  { index: '/carousel', icon: PictureRounded, text: '轮播图管理', roles: ['admin'] }
])

// 菜单顺序容器引用
const menuOrderContainer = ref(null)

// 初始化拖拽排序
const initSortable = () => {
  if (menuOrderContainer.value) {
    const sortable = Sortable.create(menuOrderContainer.value, {
      animation: 150,
      handle: '.drag-icon',
      ghostClass: 'ghost',
      onEnd: (evt) => {
        // 拖拽结束后更新菜单顺序
        const newOrder = Array.from(menuOrderContainer.value.children).map(item => item.dataset.index)
        // 更新menuItems顺序
        const reorderedItems = newOrder.map(index => 
          menuItems.value.find(item => item.index === index)
        ).filter(Boolean)
        menuItems.value = reorderedItems
      }
    })
  }
}

// 保存菜单顺序
const saveMenuOrder = () => {
  // 保存菜单顺序到localStorage
  const menuOrder = menuItems.value.map(item => item.index)
  localStorage.setItem('menuOrder', JSON.stringify(menuOrder))
  ElMessage.success('菜单顺序已保存')
  // 触发storage事件，让App.vue感知到变化
  window.dispatchEvent(new Event('storage'))
}

// 恢复默认菜单顺序
const resetMenuOrder = () => {
  // 根据用户角色过滤菜单
  filterMenusByRole()
  // 重新初始化拖拽
  initSortable()
  ElMessage.info('已恢复默认菜单顺序')
}

// 选中的主题
const selectedTheme = ref('default')

// 主题预览样式
const themePreviewStyle = computed(() => {
  const currentTheme = themeOptions.value.find(theme => theme.name === selectedTheme.value)
  return {
    '--theme-color': currentTheme?.color || '#409eff'
  }
})

// 显示设置
const displaySettings = reactive({
  compactMode: false,
  darkMode: false
})



// 处理主题变化
const handleThemeChange = () => {
  const currentTheme = themeOptions.value.find(theme => theme.name === selectedTheme.value)
  if (currentTheme) {
    // 动态修改CSS变量
    document.documentElement.style.setProperty('--el-color-primary', currentTheme.color)
    
    // 保存到localStorage
    localStorage.setItem('selectedTheme', selectedTheme.value)
    
    ElMessage.success('主题已切换')
  }
}

// 保存设置
const saveSettings = () => {
  // 保存主题设置
  localStorage.setItem('selectedTheme', selectedTheme.value)
  
  // 保存显示设置
  localStorage.setItem('displaySettings', JSON.stringify(displaySettings))
  
  // 保存菜单顺序
  saveMenuOrder()
  
  ElMessage.success('设置已保存')
}

// 恢复默认设置
const resetSettings = () => {
  // 恢复默认主题
  selectedTheme.value = 'default'
  handleThemeChange()
  
  // 恢复默认显示设置
  Object.assign(displaySettings, {
    compactMode: false,
    darkMode: false
  })
  
  // 恢复默认菜单顺序
  resetMenuOrder()
  
  ElMessage.info('已恢复默认设置')
}

// 初始化设置
const initSettings = () => {
  // 加载主题设置
  const savedTheme = localStorage.getItem('selectedTheme')
  if (savedTheme) {
    selectedTheme.value = savedTheme
    handleThemeChange()
  } else {
    // 默认主题
    handleThemeChange()
  }
  
  // 加载显示设置
  const savedDisplaySettings = localStorage.getItem('displaySettings')
  if (savedDisplaySettings) {
    Object.assign(displaySettings, JSON.parse(savedDisplaySettings))
  }
}

// 监听显示设置变化，实时保存到localStorage
watch(displaySettings, (newSettings) => {
  // 实时保存到localStorage
  localStorage.setItem('displaySettings', JSON.stringify(newSettings))
  
  // 触发显示效果变化（通过localStorage事件）
  window.dispatchEvent(new Event('storage'))
}, { deep: true })

// 组件挂载时初始化
onMounted(() => {
  initSettings()
  // 获取用户信息
  userInfo.value = getUserInfo()
  // 根据用户角色过滤菜单
  filterMenusByRole()
  // 初始化拖拽排序
  initSortable()
  // 从localStorage加载菜单顺序
  const savedMenuOrder = localStorage.getItem('menuOrder')
  if (savedMenuOrder) {
    try {
      const menuOrder = JSON.parse(savedMenuOrder)
      // 根据保存的顺序重新排列菜单
      const reorderedItems = menuOrder.map(index => 
        menuItems.value.find(item => item.index === index)
      ).filter(Boolean)
      // 如果有有效数据，使用保存的顺序
      if (reorderedItems.length > 0) {
        menuItems.value = reorderedItems
      }
    } catch (error) {
      console.error('解析菜单顺序失败:', error)
    }
  }
})

// 根据用户角色过滤菜单
const filterMenusByRole = () => {
  const currentRole = userInfo.value.role
  if (currentRole) {
    // 根据用户角色过滤菜单
    const defaultMenus = [
      { index: '/home', icon: House, text: '首页', roles: ['admin', 'teacher', 'student'] },
      { index: '/students', icon: UserIcon, text: '学生管理', roles: ['admin', 'teacher'] },
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
          { index: '/student-award-records', icon: DocumentChecked, text: '获奖记录', roles: ['admin', 'teacher', 'student'] }
        ]
      },
      { index: '/statistics', icon: DataLine, text: '数据统计', roles: ['admin', 'teacher'] },
      { index: '/notices', icon: Bell, text: '通知管理', roles: ['admin'] },
      { index: '/users', icon: UserFilled, text: '用户管理', roles: ['admin'] },
      { index: '/carousel', icon: PictureRounded, text: '轮播图管理', roles: ['admin'] }
    ]
    
    // 过滤出当前用户有权限的菜单
    menuItems.value = defaultMenus.filter(menu => menu.roles.includes(currentRole))
  }
}
</script>

<style scoped>
.settings-container {
  padding: 20px;
  min-height: 80vh;
}

.settings-card {
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

.settings-content {
  padding: 20px 0;
}

.setting-item-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 颜色主题设置 */
.color-theme-section {
  padding: 20px 0;
}

.theme-description {
  margin-bottom: 20px;
  color: #606266;
}

.theme-preview {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.preview-box {
  width: 300px;
  height: 200px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.preview-header {
  height: 50px;
  background-color: var(--theme-color);
}

.preview-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
}

.preview-button {
  width: 100px;
  height: 40px;
  background-color: var(--theme-color);
  border-radius: 4px;
}

.theme-options {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.theme-option-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
}

.theme-color {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-bottom: 5px;
  border: 2px solid transparent;
  transition: all 0.3s;
}

.el-radio-button.is-checked .theme-color {
  border-color: #fff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/* 设置项通用样式 */
.display-settings-section,
.computer-info-section,
.login-info-section {
  padding: 20px 0;
}

.display-form {
  padding: 10px 0;
}

/* 描述列表样式 */
.el-descriptions {
  margin-top: 10px;
}

.el-descriptions__label {
  font-weight: 500;
  background-color: #f5f7fa;
}

.el-descriptions__content {
  word-break: break-all;
}

.setting-desc {
  margin-left: 10px;
  color: #909399;
  font-size: 14px;
}

/* 操作按钮 */
.settings-actions {
  display: flex;
  justify-content: flex-start;
  margin-top: 30px;
}

/* 菜单顺序管理样式 */
.menu-order-section {
  padding: 20px 0;
}

.menu-order-description {
  margin-bottom: 20px;
  color: #606266;
}

.menu-order-container {
  border: 1px solid #e6a23c;
  border-radius: 8px;
  padding: 10px;
  background-color: #f5f7fa;
  min-height: 400px;
}

.menu-item-order {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 8px;
  background-color: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  cursor: move;
  transition: all 0.3s ease;
}

.menu-item-order:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.menu-item-order.ghost {
  opacity: 0.5;
  background-color: #c6e2ff;
}

.drag-icon {
  font-size: 18px;
  color: #909399;
  margin-right: 12px;
  cursor: move;
  width: 20px;
  text-align: center;
}

.menu-item-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.menu-item-icon {
  font-size: 18px;
  color: #409eff;
  margin-right: 10px;
}

.menu-item-text {
  font-size: 14px;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-content {
    padding: 10px;
  }
  
  .theme-options {
    flex-direction: column;
  }
  
  .settings-actions {
    justify-content: center;
  }
}
</style>