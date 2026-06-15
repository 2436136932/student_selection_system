<template>
  <div class="chat-container">
    <el-card class="chat-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h2>聊天中心</h2>
          <!-- 小屏幕切换按钮 -->
          <div v-if="isSmallScreen" class="mobile-toggle">
            <el-button 
              :type="showUserList ? 'primary' : 'default'" 
              size="small"
              @click="toggleView('list')"
            >
              用户列表
            </el-button>
            <el-button 
              :type="!showUserList ? 'primary' : 'default'" 
              size="small"
              @click="toggleView('chat')"
              :disabled="!currentSession"
            >
              聊天窗口
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="chat-main">
        <!-- 左侧用户列表 -->
        <div class="chat-list-panel" :class="{ 'hidden-mobile': isSmallScreen && !showUserList }">
          <ChatList 
            :users="users" 
            :currentSession="currentSession"
            :chatSessions="chatSessions"
            :online-users="onlineUsers"
            @select-user="handleSelectUser"
          />
        </div>
        
        <!-- 右侧聊天窗口 -->
        <div class="chat-window-panel" :class="{ 'hidden-mobile': isSmallScreen && showUserList }">
          <ChatWindow 
            v-if="currentSession && currentChatUser" 
            :session="currentSession"
            :messages="currentMessages"
            :chat-user="currentChatUser"
            :online-users="onlineUsers"
            @send-message="handleSendMessage"
            @close-session="handleCloseSession"
          />
          <div v-else class="no-session">
            <el-empty description="请选择一个用户开始聊天" />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, onUnmounted, computed } from 'vue'
import ChatList from '../components/ChatList.vue'
import ChatWindow from '../components/ChatWindow.vue'
import { useUserStore } from '../store/user'
import { useChatStore } from '../store/chat'
import { useAppStore } from '../store/app'

const userStore = useUserStore()
const chatStore = useChatStore()
const appStore = useAppStore()

// 用户列表
const users = ref([])
// 聊天会话列表
const chatSessions = ref([])
// 当前选中的会话
const currentSession = ref(null)
// 当前聊天对象
const currentChatUser = ref(null)
// 当前会话的消息列表
const currentMessages = ref([])
// 在线用户列表
const onlineUsers = ref([])
const onlineUsersTimer = ref(null)

// 响应式布局相关
const windowWidth = ref(window.innerWidth)
const showUserList = ref(true)

// 判断是否为小屏幕
const isSmallScreen = computed(() => windowWidth.value <= 576)

// 监听窗口大小变化
const handleResize = () => {
  windowWidth.value = window.innerWidth
  // 如果从小屏幕切换到大屏幕，重置显示状态
  if (!isSmallScreen.value) {
    showUserList.value = true
  }
}

// 切换视图（仅在小屏幕下使用）
const toggleView = (view) => {
  showUserList.value = view === 'list'
}

// 处理选择用户（在小屏幕下自动切换到聊天窗口）
const handleSelectUser = (user) => {
  selectUser(user)
  // 如果是小屏幕，选择用户后自动切换到聊天窗口
  if (isSmallScreen.value) {
    showUserList.value = false
  }
}

// 导入WebSocket服务 - 已由 chatStore 管理

// 初始化聊天功能
onMounted(() => {
  fetchUsers()
  fetchChatSessions()
  fetchOnlineUsers()
  chatStore.initWebSocket(handleWebSocketMessage)
  onlineUsersTimer.value = setInterval(fetchOnlineUsers, 30000)

  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (onlineUsersTimer.value) {
    clearInterval(onlineUsersTimer.value)
    onlineUsersTimer.value = null
  }
})

// 消息处理函数
const handleWebSocketMessage = (event) => {
  try {
    const message = JSON.parse(event.data)
    console.log('接收到消息: ', message)
    
    // 如果消息属于当前会话，直接添加到消息列表
    if (currentSession.value && currentSession.value.id === message.sessionId) {
      currentMessages.value.push(message)
      // 如果是收到的消息，标记为已读
      const userId = userStore.userId
      if (message.senderId !== userId) {
        markMessagesAsRead(message.sessionId)
      }
    } else {
      // 否则更新会话列表中的最后一条消息
      updateSessionLastMessage(message.sessionId, message)
      // 如果会话不存在，添加到会话列表
      if (!chatSessions.value.some(session => session.id === message.sessionId)) {
        fetchChatSessions()
      }
      // 发送未读消息数量更新事件
      appStore.fetchUnreadMessageCount()
    }
  } catch (error) {
    console.error('解析WebSocket消息失败:', error)
  }
}

// 获取所有用户
const fetchUsers = async () => {
  try {
    console.log('开始获取用户列表...')
    console.log('当前token:', userStore.token)
    const response = await fetch('/api/chats/users', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    })
    
    console.log('获取用户列表响应状态:', response.status)
    if (response.ok) {
      const data = await response.json()
      console.log('获取用户列表成功:', data)
      // 打印每个学生用户的详细信息，检查是否包含学号
      data.forEach(user => {
        if (user.role === 'student') {
          console.log(`学生用户: ${user.realName}, 学号: ${user.studentNumber || '未找到'}`)
        }
      })
      users.value = data
    } else {
      console.error('获取用户列表失败:', response.statusText)
      // 获取错误详情
      try {
        const errorData = await response.json()
        console.error('获取用户列表失败详情:', errorData)
      } catch (e) {
        console.error('无法解析错误响应:', e)
      }
    }
  } catch (error) {
    console.error('获取用户列表异常:', error)
  }
}

// 获取聊天会话列表
const fetchChatSessions = async () => {
  try {
    const response = await fetch('/api/chats/sessions', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      chatSessions.value = data
    } else {
      console.error('获取聊天会话失败:', response.statusText)
    }
  } catch (error) {
    console.error('获取聊天会话异常:', error)
  }
}

// 获取在线用户列表
const fetchOnlineUsers = async () => {
  try {
    const response = await fetch('/api/chats/online-users', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      onlineUsers.value = data
      console.log('获取在线用户列表成功:', onlineUsers.value)
    } else {
      console.error('获取在线用户列表失败:', response.statusText)
    }
  } catch (error) {
    console.error('获取在线用户列表异常:', error)
  }
}

// 选择用户，创建或打开会话
const selectUser = async (user) => {
  try {
    // 创建或获取会话
    const response = await fetch('/api/chats/sessions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ receiverId: user.id })
    })
    
    if (response.ok) {
      const session = await response.json()
      currentSession.value = session
      currentChatUser.value = user
      fetchMessagesBySessionId(session.id)
      // 标记该会话的消息为已读
      markMessagesAsRead(session.id)
      // 更新会话列表
      if (!chatSessions.value.some(s => s.id === session.id)) {
        chatSessions.value.push(session)
      }
    } else {
      console.error('创建会话失败:', response.statusText)
    }
  } catch (error) {
    console.error('创建会话异常:', error)
  }
}

// 标记消息为已读
const markMessagesAsRead = async (sessionId) => {
  try {
    await fetch(`/api/chats/sessions/${sessionId}/read-all`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    })
    console.log('消息已标记为已读')
    // 更新会话的未读消息数量为0
    const sessionIndex = chatSessions.value.findIndex(session => session.id === sessionId)
    if (sessionIndex !== -1) {
      chatSessions.value[sessionIndex].unreadCount = 0
    }
    // 更新App.vue中的未读消息数量
    window.dispatchEvent(new CustomEvent('updateUnreadCount'))
  } catch (error) {
    console.error('标记消息为已读失败:', error)
  }
}

// 根据会话ID获取聊天消息
const fetchMessagesBySessionId = async (sessionId) => {
  try {
    const response = await fetch(`/api/chats/messages/session/${sessionId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      currentMessages.value = data
    } else {
      console.error('获取聊天消息失败:', response.statusText)
    }
  } catch (error) {
    console.error('获取聊天消息异常:', error)
  }
}

// 处理发送消息
const handleSendMessage = async (message) => {
  try {
    const response = await fetch('/api/chats/messages', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${userStore.token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(message)
    })
    
    if (response.ok) {
      const newMessage = await response.json()
      currentMessages.value.push(newMessage)
      // 更新会话列表中的最后一条消息
      updateSessionLastMessage(currentSession.value.id, newMessage)
    } else {
      console.error('发送消息失败:', response.statusText)
    }
  } catch (error) {
    console.error('发送消息异常:', error)
  }
}

// 更新会话列表中的最后一条消息
const updateSessionLastMessage = (sessionId, message) => {
  const sessionIndex = chatSessions.value.findIndex(session => session.id === sessionId)
  if (sessionIndex !== -1) {
    chatSessions.value[sessionIndex].lastMessage = message.content
    chatSessions.value[sessionIndex].lastMessageTime = message.createdAt
    // 如果是收到的消息，未读消息数量加1
    if (message.senderId !== userStore.userId) {
      chatSessions.value[sessionIndex].unreadCount = (chatSessions.value[sessionIndex].unreadCount || 0) + 1
    }
  }
}

// 处理关闭会话
const handleCloseSession = (sessionId) => {
  currentSession.value = null
  currentChatUser.value = null
  currentMessages.value = []
  console.log('会话已关闭:', sessionId)
}


</script>

<style scoped>
.chat-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  margin: 0;
  padding: 0;
  overflow: hidden;
}

.chat-card {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 0;
  overflow: hidden;
  box-shadow: none;
  margin: 0;
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #fafafa;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  flex-wrap: wrap;
  gap: 12px;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

/* 小屏幕切换按钮 */
.mobile-toggle {
  display: flex;
  gap: 8px;
}

/* 直接修改 Element Plus 卡片内容区域样式 */
.chat-card :deep(.el-card__body) {
  padding: 0;
  margin: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
}

.chat-main {
  display: flex;
  overflow: hidden;
  flex: 1;
  width: 100%;
  height: 0;
}

.chat-list-panel {
  width: 320px;
  border-right: 1px solid #e8e8e8;
  overflow: hidden;
  background-color: #ffffff;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
  transition: all 0.3s ease;
}

.chat-window-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  min-width: 0;
  height: 100%;
  transition: all 0.3s ease;
}

.no-session {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  height: 100%;
  width: 100%;
}

/* 小屏幕隐藏类 */
.hidden-mobile {
  display: none !important;
}

/* 中等屏幕响应式（576px < 宽度 <= 768px） */
@media (max-width: 768px) {
  .card-header {
    padding: 12px 16px;
  }
  
  .card-header h2 {
    font-size: 18px;
  }
  
  .chat-list-panel {
    width: 280px;
  }
}

/* 小屏幕响应式（宽度 <= 576px） */
@media (max-width: 576px) {
  .card-header {
    padding: 12px;
    flex-direction: column;
    align-items: stretch;
  }
  
  .card-header h2 {
    font-size: 16px;
    text-align: center;
  }
  
  .mobile-toggle {
    justify-content: center;
  }
  
  .chat-main {
    flex-direction: column;
  }
  
  .chat-list-panel {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #e8e8e8;
  }
  
  .chat-window-panel {
    width: 100%;
  }
}
</style>