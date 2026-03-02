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
            :online-users="onlineUsers"
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
// 在线用户列表
const onlineUsers = ref([])

// 导入WebSocket服务
import { initializeWebSocket as initWebSocket } from '../utils/websocketService'

// 初始化聊天功能
onMounted(() => {
  console.log('ChatView组件已挂载，开始初始化聊天功能...')
  fetchUsers()
  fetchChatSessions()
  fetchOnlineUsers()
  // 使用WebSocket服务初始化连接，传入消息处理函数
  initWebSocket(handleWebSocketMessage)
  // 每30秒获取一次在线用户列表
  setInterval(fetchOnlineUsers, 30000)
  console.log('聊天功能初始化完成')
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
      const userId = JSON.parse(localStorage.getItem('userInfo'))?.id
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
      window.dispatchEvent(new CustomEvent('updateUnreadCount'))
    }
  } catch (error) {
    console.error('解析WebSocket消息失败:', error)
  }
}

// 获取所有用户
const fetchUsers = async () => {
  try {
    console.log('开始获取用户列表...')
    console.log('当前token:', localStorage.getItem('token'))
    const response = await fetch('http://localhost:8080/api/chats/users', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
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

// 获取在线用户列表
const fetchOnlineUsers = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/chats/online-users', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
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
    await fetch(`http://localhost:8080/api/chats/sessions/${sessionId}/read-all`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
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
    // 如果是收到的消息，未读消息数量加1
    if (message.senderId !== JSON.parse(localStorage.getItem('userInfo'))?.id) {
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
  overflow: hidden;
  background-color: #ffffff;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-window-panel {
  flex: 1;
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