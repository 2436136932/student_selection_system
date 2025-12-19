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
import { Setting, Brush, Monitor, User } from '@element-plus/icons-vue'
import { getUserInfo } from '../utils/role'

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
})
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