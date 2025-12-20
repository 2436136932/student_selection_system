<template>
  <div class="chat-container">
    <el-card class="chat-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h2>聊天中心</h2>
        </div>
      </template>
      
      <div class="chat-main">
        <!-- 左侧用户列表 -->
        <div class="chat-list-panel">
          <ChatList 
            :users="users" 
            :currentSession="currentSession"
            :chatSessions="chatSessions"
            @select-user="selectUser"
          />
        </div>
        
        <!-- 右侧聊天窗口 -->
        <div class="chat-window-panel">
          <ChatWindow 
            v-if="currentSession && currentChatUser" 
            :session="currentSession"
            :messages="currentMessages"
            :chat-user="currentChatUser"
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
import { ref, onMounted, watch } from 'vue'
import ChatList from '../components/ChatList.vue'
import ChatWindow from '../components/ChatWindow.vue'

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

// 初始化聊天功能
onMounted(() => {
  fetchUsers()
  fetchChatSessions()
  initializeWebSocket()
})

// 获取所有用户
const fetchUsers = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/chats/users', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      users.value = data
    } else {
      console.error('获取用户列表失败:', response.statusText)
    }
  } catch (error) {
    console.error('获取用户列表异常:', error)
  }
}

// 获取聊天会话列表
const fetchChatSessions = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/chats/sessions', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
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

// 选择用户，创建或打开会话
const selectUser = async (user) => {
  try {
    // 创建或获取会话
    const response = await fetch('http://localhost:8080/api/chats/sessions', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ receiverId: user.id })
    })
    
    if (response.ok) {
      const session = await response.json()
      currentSession.value = session
      currentChatUser.value = user
      fetchMessagesBySessionId(session.id)
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

// 根据会话ID获取聊天消息
const fetchMessagesBySessionId = async (sessionId) => {
  try {
    const response = await fetch(`http://localhost:8080/api/chats/messages/session/${sessionId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
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
    const response = await fetch('http://localhost:8080/api/chats/messages', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
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
  }
}

// 处理关闭会话
const handleCloseSession = (sessionId) => {
  currentSession.value = null
  currentChatUser.value = null
  currentMessages.value = []
  console.log('会话已关闭:', sessionId)
}

// 初始化WebSocket连接
const initializeWebSocket = () => {
  const userId = JSON.parse(localStorage.getItem('userInfo'))?.id
  if (!userId) return
  
  // 使用WebSocket创建连接，添加详细的错误处理
    console.log(`尝试建立WebSocket连接: ws://localhost:8080/ws/chat?userId=${userId}`)
    try {
      const socket = new WebSocket(`ws://localhost:8080/ws/chat?userId=${userId}`)
      
      socket.onopen = () => {
        console.log('WebSocket连接已建立')
      }
      
      socket.onmessage = (event) => {
        console.log('收到WebSocket消息:', event.data)
        try {
          const message = JSON.parse(event.data)
          // 如果消息属于当前会话，直接添加到消息列表
          if (currentSession.value && currentSession.value.id === message.sessionId) {
            currentMessages.value.push(message)
          } else {
            // 否则更新会话列表中的最后一条消息
            updateSessionLastMessage(message.sessionId, message)
            // 如果会话不存在，添加到会话列表
            if (!chatSessions.value.some(session => session.id === message.sessionId)) {
              fetchChatSessions()
            }
          }
        } catch (error) {
          console.error('解析WebSocket消息失败:', error)
        }
      }
      
      socket.onclose = (event) => {
        console.log(`WebSocket连接已关闭，代码: ${event.code}, 原因: ${event.reason}`)
        // 5秒后重新连接
        setTimeout(() => {
          initializeWebSocket()
        }, 5000)
      }
      
      socket.onerror = (error) => {
        console.error('WebSocket连接出错:', error)
        console.error('错误类型:', error.type)
        console.error('错误目标:', error.target)
      }
    } catch (error) {
      console.error('创建WebSocket连接失败:', error)
      // 5秒后重新连接
      setTimeout(() => {
        initializeWebSocket()
      }, 5000)
    }
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
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

/* 直接修改 Element Plus 卡片内容区域样式 */
.chat-card :deep(.el-card__body) {
  padding: 0;
  margin: 0;
  width: 100%;
  flex: 1;
  max-width: none;
  box-sizing: border-box;
  overflow: hidden;
}

.chat-main {
  display: flex;
  overflow: hidden;
  flex: 1;
  width: 100%;
}

.chat-list-panel {
  width: 320px;
  border-right: 1px solid #e8e8e8;
  overflow-y: auto;
  background-color: #ffffff;
  flex-shrink: 0;
}

.chat-window-panel {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  width: 0; /* 确保flex:1能正确计算 */
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
</style>