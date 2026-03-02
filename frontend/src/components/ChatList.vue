<template>
  <div class="chat-list">
    <!-- 会话列表头部 -->
    <div class="chat-list-header">
      <h3>用户列表</h3>
      <!-- 搜索框 -->
      <div class="search-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户..."
          clearable
          @input="handleSearch"
          size="small"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      <!-- 搜索结果数量 -->
      <div class="search-result-count">
        <small>共 {{ filteredUsers.length }} 条记录</small>
      </div>
    </div>
    
    <!-- 会话列表内容 -->
    <div class="chat-list-content" ref="chatListContent">
      <!-- 按角色分组的折叠列表 -->
      <el-collapse v-model="activeNames" accordion>
        <!-- 管理员组 -->
        <el-collapse-item 
          v-if="groupedUsers.admin.length > 0" 
          name="admin"
        >
          <template #title>
            <div class="collapse-title">
              管理员 ({{ groupedUsers.admin.length }})
              <el-badge 
                v-if="roleUnreadCounts.admin > 0" 
                :value="roleUnreadCounts.admin" 
                class="role-unread-badge"
                :max="99"
              />
            </div>
          </template>
          <div 
            v-for="user in groupedUsers.admin" 
            :key="user.id"
            class="chat-item"
            :class="{ 'active': isUserActive(user) }"
            @click="handleSelectUser(user)"
          >
            <div class="chat-item-avatar">
              <el-popover
                placement="right"
                trigger="hover"
                :width="280"
              >
                <template #reference>
                  <el-avatar size="large">
                    {{ getUserInitial(user) }}
                  </el-avatar>
                </template>
                <div class="user-info-popover">
                  <div class="user-info-item">
                    <span class="label">姓名：</span>
                    <span class="value">{{ user.realName || user.username }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">角色：</span>
                    <span class="value">{{ getRoleName(user.role) }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">状态：</span>
                    <span class="value status" :class="user.id === 1 ? 'online' : 'offline'">
                      {{ user.id === 1 ? '在线' : '离线' }}
                    </span>
                  </div>
                </div>
              </el-popover>
              <!-- 未读消息提示 -->
              <el-badge 
                v-if="getUnreadCount(user) > 0" 
                :value="getUnreadCount(user)" 
                class="unread-badge"
                :max="99"
              />
            </div>
            
            <div class="chat-item-info">
              <div class="chat-item-top">
                <span class="chat-item-name">
                  {{ user.realName || user.username }}
                </span>
                <span class="chat-item-role-tag" :class="getRoleClass(user.role)">
                  {{ getRoleName(user.role) }}
                </span>
              </div>
              
              <div class="chat-item-bottom">
                <span class="chat-item-message">
                  点击开始聊天
                </span>
              </div>
            </div>
          </div>
        </el-collapse-item>
        
        <!-- 教师组 -->
        <el-collapse-item 
          v-if="groupedUsers.teacher.length > 0" 
          name="teacher"
        >
          <template #title>
            <div class="collapse-title">
              教师 ({{ groupedUsers.teacher.length }})
              <el-badge 
                v-if="roleUnreadCounts.teacher > 0" 
                :value="roleUnreadCounts.teacher" 
                class="role-unread-badge"
                :max="99"
              />
            </div>
          </template>
          <div 
            v-for="user in groupedUsers.teacher" 
            :key="user.id"
            class="chat-item"
            :class="{ 'active': isUserActive(user) }"
            @click="handleSelectUser(user)"
          >
            <div class="chat-item-avatar">
              <el-popover
                placement="right"
                trigger="hover"
                :width="280"
              >
                <template #reference>
                  <el-avatar size="large">
                    {{ getUserInitial(user) }}
                  </el-avatar>
                </template>
                <div class="user-info-popover">
                  <div class="user-info-item">
                    <span class="label">姓名：</span>
                    <span class="value">{{ user.realName || user.username }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">角色：</span>
                    <span class="value">{{ getRoleName(user.role) }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">部门：</span>
                    <span class="value">{{ user.department || '暂无部门信息' }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">状态：</span>
                    <span class="value status" :class="onlineUsers.includes(user.id.toString()) ? 'online' : 'offline'">
                      {{ onlineUsers.includes(user.id.toString()) ? '在线' : '离线' }}
                    </span>
                  </div>
                </div>
              </el-popover>
              <!-- 未读消息提示 -->
              <el-badge 
                v-if="getUnreadCount(user) > 0" 
                :value="getUnreadCount(user)" 
                class="unread-badge"
                :max="99"
              />
            </div>
            
            <div class="chat-item-info">
              <div class="chat-item-top">
                <span class="chat-item-name">
                  {{ user.realName || user.username }}
                </span>
                <span class="chat-item-role-tag" :class="getRoleClass(user.role)">
                  {{ getRoleName(user.role) }}
                </span>
              </div>
              
              <div class="chat-item-bottom">
                <span class="chat-item-message">
                  {{ user.department || '暂无部门信息' }}
                </span>
              </div>
            </div>
          </div>
        </el-collapse-item>
        
        <!-- 学生组 -->
        <el-collapse-item 
          v-if="groupedUsers.student.length > 0" 
          name="student"
        >
          <template #title>
            <div class="collapse-title">
              学生 ({{ groupedUsers.student.length }})
              <el-badge 
                v-if="roleUnreadCounts.student > 0" 
                :value="roleUnreadCounts.student" 
                class="role-unread-badge"
                :max="99"
              />
            </div>
          </template>
          <div 
            v-for="user in groupedUsers.student" 
            :key="user.id"
            class="chat-item"
            :class="{ 'active': isUserActive(user) }"
            @click="handleSelectUser(user)"
          >
            <div class="chat-item-avatar">
              <el-popover
                placement="right"
                trigger="hover"
                :width="280"
              >
                <template #reference>
                  <el-avatar size="large">
                    {{ getUserInitial(user) }}
                  </el-avatar>
                </template>
                <div class="user-info-popover">
                  <div class="user-info-item">
                    <span class="label">姓名：</span>
                    <span class="value">{{ user.realName || user.username }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">角色：</span>
                    <span class="value">{{ getRoleName(user.role) }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">专业：</span>
                    <span class="value">{{ user.major || '暂无专业信息' }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">班级：</span>
                    <span class="value">{{ user.className || '暂无班级信息' }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">学号：</span>
                    <span class="value">{{ user.studentNumber || '暂无学号信息' }}</span>
                  </div>
                  <div class="user-info-item">
                    <span class="label">状态：</span>
                    <span class="value status" :class="onlineUsers.includes(user.id.toString()) ? 'online' : 'offline'">
                      {{ onlineUsers.includes(user.id.toString()) ? '在线' : '离线' }}
                    </span>
                  </div>
                </div>
              </el-popover>
              <!-- 未读消息提示 -->
              <el-badge 
                v-if="getUnreadCount(user) > 0" 
                :value="getUnreadCount(user)" 
                class="unread-badge"
                :max="99"
              />
            </div>
            
            <div class="chat-item-info">
              <div class="chat-item-top">
                <span class="chat-item-name">
                  {{ user.realName || user.username }}
                </span>
                <span class="chat-item-role-tag" :class="getRoleClass(user.role)">
                  {{ getRoleName(user.role) }}
                </span>
              </div>
              
              <div class="chat-item-bottom">
                <span class="chat-item-message">
                  {{ user.major || '' }} {{ user.className || '' }}
                </span>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
      
      <!-- 无匹配用户时显示 -->
      <div v-if="filteredUsers.length === 0" class="empty-sessions">
        <el-empty description="暂无匹配用户" />
      </div>
      
      <!-- 添加一些占位内容，确保内容超出容器高度 -->
      <div v-if="filteredUsers.length > 0 && filteredUsers.length < 5" class="scroll-placeholder">
        <div v-for="i in (5 - filteredUsers.length)" :key="i" class="placeholder-item"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'

// 接收父组件传递的属性
const props = defineProps({
  users: {
    type: Array,
    default: () => []
  },
  currentSession: {
    type: Object,
    default: null
  },
  chatSessions: {
    type: Array,
    default: () => []
  },
  onlineUsers: {
    type: Array,
    default: () => []
  }
})

// 定义事件
const emit = defineEmits(['select-user'])

// 搜索关键词
const searchKeyword = ref('')

// 折叠面板的活跃名称
const activeNames = ref([])

// 过滤后的用户列表
const filteredUsers = computed(() => {
  if (!searchKeyword.value.trim()) {
    return props.users
  }
  
  const keyword = searchKeyword.value.trim().toLowerCase()
  return props.users.filter(user => {
    const realName = user.realName ? user.realName.toLowerCase() : ''
    const username = user.username ? user.username.toLowerCase() : ''
    const department = user.department ? user.department.toLowerCase() : ''
    const major = user.major ? user.major.toLowerCase() : ''
    const className = user.className ? user.className.toLowerCase() : ''
    
    return realName.includes(keyword) || 
           username.includes(keyword) || 
           department.includes(keyword) || 
           major.includes(keyword) || 
           className.includes(keyword)
  })
})

// 按角色分组的用户列表
const groupedUsers = computed(() => {
  // 初始化分组对象
  const groups = {
    admin: [],
    teacher: [],
    student: []
  }
  
  // 将过滤后的用户分配到对应的分组
  filteredUsers.value.forEach(user => {
    if (user.role === 'admin') {
      groups.admin.push(user)
    } else if (user.role === 'teacher') {
      groups.teacher.push(user)
    } else if (user.role === 'student') {
      groups.student.push(user)
    }
  })
  
  return groups
})

// 计算每个角色的未读消息总数
const roleUnreadCounts = computed(() => {
  // 初始化各角色未读消息总数
  const unreadCounts = {
    admin: 0,
    teacher: 0,
    student: 0
  }
  
  // 遍历所有聊天会话，计算各角色的未读消息总数
  props.chatSessions.forEach(session => {
    // 获取会话的对方用户ID
    const currentUserId = JSON.parse(localStorage.getItem('userInfo'))?.id
    const otherUserId = session.user1Id === currentUserId ? session.user2Id : session.user1Id
    
    // 查找对方用户
    const otherUser = props.users.find(user => user.id === otherUserId)
    
    // 如果找到对方用户，将该会话的未读消息数量添加到对应角色的未读消息总数中
    if (otherUser) {
      unreadCounts[otherUser.role] += session.unreadCount || 0
    }
  })
  
  return unreadCounts
})

// 获取当前用户ID
const currentUserId = computed(() => {
  return JSON.parse(localStorage.getItem('userInfo'))?.id || ''
})

// 判断用户是否活跃（是否是当前聊天对象）
const isUserActive = (user) => {
  if (!props.currentSession) return false
  return props.currentSession.user1Id === user.id || props.currentSession.user2Id === user.id
}

// 获取用户角色名称
const getRoleName = (role) => {
  const roleMap = {
    'admin': '管理员',
    'teacher': '教师',
    'student': '学生'
  }
  return roleMap[role] || '用户'
}

// 获取用户角色样式类
const getRoleClass = (role) => {
  return `role-${role}`
}

// 获取用户姓名首字母
const getUserInitial = (user) => {
  const name = user.realName || user.username
  return name.charAt(0).toUpperCase()
}

// 获取用户的未读消息数量
const getUnreadCount = (user) => {
  // 从chatSessions中查找与当前用户相关的会话
  const session = props.chatSessions.find(s => 
    s.user1Id === user.id || s.user2Id === user.id
  )
  // 返回会话的未读消息数量，如果没有会话或没有未读消息，返回0
  return session?.unreadCount || 0
}

// 处理选择用户
const handleSelectUser = (user) => {
  emit('select-user', user)
}

// 处理搜索
const handleSearch = () => {
  // 搜索逻辑由computed属性自动处理
  console.log('搜索关键词:', searchKeyword.value)
}

// 引用DOM元素
const chatListContent = ref(null)

// 在组件挂载后强制设置滚动条
onMounted(() => {
  // 确保内容区域有足够的高度来显示滚动条
  setTimeout(() => {
    if (chatListContent.value) {
      // 获取容器高度
      const containerHeight = chatListContent.value.clientHeight
      // 获取内容高度
      const contentHeight = chatListContent.value.scrollHeight
      
      // 如果内容高度小于容器高度，增加最小高度以确保滚动条可见
      if (contentHeight <= containerHeight) {
        chatListContent.value.style.minHeight = `${containerHeight + 100}px`
      }
      
      // 强制显示滚动条
      chatListContent.value.style.overflowY = 'scroll'
      
      console.log('聊天列表容器高度:', containerHeight)
      console.log('聊天列表内容高度:', contentHeight)
    }
  }, 1000)
})
</script>

<style scoped>
.chat-list {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-list-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
  background-color: #fafafa;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-list-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

/* 搜索容器 */
.search-container {
  width: 100%;
}

/* 搜索结果数量 */
.search-result-count {
  text-align: right;
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}

/* 角色未读消息徽章样式 */
.collapse-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-unread-badge {
  transform: scale(0.8);
  transform-origin: top center;
}

.collapse-title :deep(.el-badge__content) {
  background-color: #f56c6c;
  color: white;
  font-size: 12px;
  font-weight: 600;
  min-width: 20px;
  height: 20px;
  line-height: 20px;
  padding: 0 6px;
  border-radius: 10px;
}

.chat-list-content {
  flex: 1;
  overflow-y: scroll !important;
  overflow-x: hidden !important;
  padding: 10px;
  padding-right: 12px;
  /* 确保内容区域有足够的高度来触发滚动 */
  min-height: 0;
  /* 强制显示滚动条 */
  -webkit-overflow-scrolling: touch;
  /* 设置固定高度，确保滚动条可见 */
  height: calc(100vh - 200px);
  max-height: calc(100vh - 200px);
  /* 确保内容超出时显示滚动条 */
  overflow-y: overlay;
}

/* WebKit浏览器滚动条样式 */
.chat-list-content::-webkit-scrollbar {
  width: 14px !important;
  height: 14px !important;
  background: #f1f1f1 !important;
  display: block !important;
  visibility: visible !important;
  appearance: none !important;
}

.chat-list-content::-webkit-scrollbar-track {
  background: #f1f1f1 !important;
  border-radius: 7px !important;
  box-shadow: inset 0 0 3px rgba(0, 0, 0, 0.1) !important;
  display: block !important;
  visibility: visible !important;
  border: 1px solid #e0e0e0 !important;
}

.chat-list-content::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #3498db, #2980b9) !important;
  border-radius: 7px !important;
  transition: all 0.2s ease !important;
  box-shadow: 0 2px 4px rgba(52, 152, 219, 0.3) !important;
  min-height: 40px !important;
  display: block !important;
  visibility: visible !important;
  border: 2px solid #f1f1f1 !important;
  appearance: none !important;
}

.chat-list-content::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #2980b9, #3498db);
  box-shadow: 0 4px 8px rgba(52, 152, 219, 0.4);
  transform: scaleY(1.1);
}

.chat-list-content::-webkit-scrollbar-thumb:active {
  background: linear-gradient(135deg, #1f618d, #2980b9);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

/* Firefox浏览器滚动条样式 */
.chat-list-content {
  scrollbar-width: thin;
  scrollbar-color: #3498db #f1f1f1;
}

/* 强制显示滚动条的额外样式 */
.chat-list-content {
  /* 确保内容区域有足够的高度来触发滚动 */
  overflow-y: scroll !important;
  overflow-x: hidden !important;
  /* 强制显示滚动条 */
  -webkit-overflow-scrolling: touch;
  /* 确保滚动条始终可见 */
  --scrollbar-width: 12px;
  --scrollbar-height: 12px;
}

/* 增加滚动条容器的可见性 */
.chat-list {
  border-right: 1px solid #e8e8e8;
  position: relative;
  height: 100%;
  overflow: hidden;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 10px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid rgba(52, 152, 219, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chat-item:hover {
  background: linear-gradient(135deg, #f0f7ff 0%, #e3f2fd 100%);
  border-color: rgba(52, 152, 219, 0.3);
  box-shadow: 0 4px 16px rgba(52, 152, 219, 0.15);
  transform: translateX(4px);
}

.chat-item.active {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border-color: #3498db;
  box-shadow: 0 4px 16px rgba(52, 152, 219, 0.25);
  transform: translateX(4px);
}

.chat-item-avatar {
  margin-right: 12px;
  position: relative;
}

/* 未读消息徽章样式 */
.unread-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  transform: scale(0.8);
  transform-origin: top right;
}

/* 确保未读消息徽章显示在头像上方 */
.chat-item-avatar :deep(.el-badge) {
  position: absolute;
  top: -5px;
  right: -5px;
  transform: scale(0.8);
  transform-origin: top right;
}

.chat-item-info {
  flex: 1;
  min-width: 0;
}

.chat-item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.chat-item-name {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-item-time {
  font-size: 12px;
  color: #909399;
}

.chat-item-bottom {
  display: flex;
  align-items: center;
}

.chat-item-message {
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-sessions {
  padding: 40px 20px;
}

/* 滚动占位符样式 */
.scroll-placeholder {
  pointer-events: none;
}

.placeholder-item {
  height: 80px;
  margin-bottom: 10px;
  opacity: 0;
}

/* 用户信息悬浮窗样式 */
.user-info-popover {
  padding: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(52, 152, 219, 0.1);
  transition: all 0.3s ease;
}

.user-info-popover:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.user-info-item {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.user-info-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.user-info-item .label {
  font-weight: 600;
  color: #4a5568;
  margin-right: 12px;
  width: 70px;
  text-align: right;
  font-size: 13px;
  opacity: 0.8;
}

.user-info-item .value {
  color: #1a202c;
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.5;
}

.user-info-item .status {
  display: inline-flex;
  align-items: center;
  font-weight: 600;
}

.user-info-item .status.online {
  color: #67c23a;
  animation: pulse 2s infinite;
}

.user-info-item .status.online::before {
  content: '';
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #67c23a;
  margin-right: 6px;
  box-shadow: 0 0 0 2px rgba(103, 194, 58, 0.2);
  animation: onlinePulse 2s infinite;
}

.user-info-item .status.offline {
  color: #909399;
}

.user-info-item .status.offline::before {
  content: '';
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #909399;
  margin-right: 6px;
}

/* 动画效果 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

@keyframes onlinePulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.7);
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 0 0 6px rgba(103, 194, 58, 0);
  }
}
</style>