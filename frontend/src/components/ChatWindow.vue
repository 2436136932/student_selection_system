<template>
  <div class="chat-window">
    <!-- 聊天窗口头部 -->
    <div class="chat-window-header">
      <div class="header-left">
        <el-avatar size="large">
          {{ getInitials(props.chatUser.realName || props.chatUser.username) }}
        </el-avatar>
        <div class="header-info">
          <h3 class="header-name">{{ props.chatUser.realName || props.chatUser.username }}</h3>
          <div class="header-details">
            <template v-if="props.chatUser.role === 'teacher'">
              <span class="header-department">{{ props.chatUser.department || '暂无部门信息' }}</span>
            </template>
            <template v-else-if="props.chatUser.role === 'student'">
              <span class="header-major-class">{{ props.chatUser.major || '' }} {{ props.chatUser.className || '' }}</span>
            </template>
          </div>
          <span class="header-status">在线</span>
        </div>
      </div>
      
      <div class="header-right">
        <el-button type="text" @click="handleCloseSession">
          <el-icon><Close /></el-icon>
          关闭会话
        </el-button>
      </div>
    </div>
    
    <!-- 消息显示区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="message in messages" 
        :key="message.id"
        class="message-item"
        :class="{ 'sent': message.senderId === currentUserId, 'received': message.senderId !== currentUserId }"
      >
        <div class="message-content">
          {{ message.content }}
        </div>
        <div class="message-time">
          {{ formatTime(message.createdAt) }}
          <el-icon v-if="message.senderId === currentUserId && message.readStatus === 'read'" class="read-icon">
            <CircleCheck />
          </el-icon>
        </div>
      </div>
      
      <div v-if="messages.length === 0" class="empty-messages">
        <el-empty description="暂无消息，开始聊天吧" />
      </div>
    </div>
    
    <!-- 消息输入区域 -->
    <div class="chat-input-area">
      <div class="input-hint">
        <small>按 Enter 发送消息，Shift + Enter 换行</small>
      </div>
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="3"
        placeholder="输入消息..."
        resize="none"
        @keyup.enter.exact="handleSendMessage"
        @keyup.enter.shift="handleEnterShift"
        style="margin-bottom: 12px;"
      >
        <template #append>
          <el-button type="primary" @click="handleSendMessage" :disabled="!inputMessage.trim()" size="large" style="height: 100%;">
            <el-icon style="margin-right: 4px;"><Right /></el-icon>
            发送
          </el-button>
        </template>
      </el-input>
      <!-- 额外的发送按钮 -->
      <div class="extra-send-button-container">
        <el-button 
          type="primary" 
          @click="handleSendMessage" 
          :disabled="!inputMessage.trim()" 
          size="large"
          class="extra-send-button"
        >
          <el-icon style="margin-right: 4px;"><Right /></el-icon>
          发送消息
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { Close, Right, CircleCheck } from '@element-plus/icons-vue'

// 接收父组件传递的属性
const props = defineProps({
  session: {
    type: Object,
    required: true
  },
  messages: {
    type: Array,
    default: () => []
  },
  chatUser: {
    type: Object,
    required: true
  }
})

// 定义事件
const emit = defineEmits(['send-message', 'close-session'])

// 获取当前用户ID
const currentUserId = computed(() => {
  return JSON.parse(localStorage.getItem('userInfo'))?.id || ''
})

// 获取用户角色
const currentUserRole = computed(() => {
  return JSON.parse(localStorage.getItem('userInfo'))?.role || ''
})

// 获取用户姓名首字母
const getInitials = (name) => {
  return name.charAt(0).toUpperCase()
}

// 输入框消息内容
const inputMessage = ref('')

// 消息容器引用
const messagesContainer = ref(null)

// 监听消息变化，自动滚动到底部
watch(
  () => props.messages,
  () => {
    scrollToBottom()
  },
  { deep: true }
)

// 自动滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  
  const date = new Date(timeStr)
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 处理发送消息
const handleSendMessage = () => {
  if (!inputMessage.value.trim()) {
    return
  }
  
  // 构建消息对象
  const message = {
    sessionId: props.session.id,
    senderId: currentUserId.value,
    receiverId: props.session.user1Id === currentUserId.value ? props.session.user2Id : props.session.user1Id,
    senderType: currentUserRole.value,
    content: inputMessage.value.trim(),
    readStatus: 'unread'
  }
  
  // 发送消息
  emit('send-message', message)
  
  // 清空输入框
  inputMessage.value = ''
  
  // 滚动到底部
  scrollToBottom()
}

// 处理Shift+Enter换行
const handleEnterShift = () => {
  // 插入换行符
  inputMessage.value += '\n'
}

// 处理关闭会话
const handleCloseSession = () => {
  emit('close-session', props.session.id)
  console.log('关闭会话:', props.session.id)
}
</script>

<style scoped>
.chat-window {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}

.chat-window-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #e8e8e8;
  background-color: #fafafa;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-info {
  margin-left: 12px;
}

.header-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.header-details {
  font-size: 12px;
  color: #909399;
  margin: 2px 0;
}

.header-department,
.header-major-class {
  font-size: 12px;
  color: #909399;
}

.header-status {
  font-size: 12px;
  color: #67c23a;
  display: flex;
  align-items: center;
}

.header-status::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #67c23a;
  margin-right: 4px;
}

.header-right {
  display: flex;
  align-items: center;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.message-item {
  display: flex;
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease;
}

.message-item.sent {
  justify-content: flex-end;
}

.message-item.received {
  justify-content: flex-start;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  word-wrap: break-word;
  line-height: 1.5;
}

.message-item.sent .message-content {
  background-color: #3498db;
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.received .message-content {
  background-color: white;
  color: #303133;
  border-bottom-left-radius: 4px;
  border: 1px solid #e8e8e8;
}

.message-time {
  font-size: 12px;
  color: #909399;
  margin: 0 8px;
  align-self: flex-end;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 已读图标样式 */
.read-icon {
  font-size: 12px;
  color: #409eff;
}

.empty-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.chat-input-area {
  padding: 16px 20px;
  border-top: 1px solid #e8e8e8;
  background-color: #ffffff;
}

.input-hint {
  margin-bottom: 8px;
  text-align: right;
  color: #909399;
  font-size: 12px;
}

.chat-input-area .el-input {
  border-radius: 8px;
}

/* 为追加的发送按钮添加样式 */
.chat-input-area .el-button {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

/* 额外的发送按钮容器 */
.extra-send-button-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}

/* 额外的发送按钮 */
.extra-send-button {
  min-width: 120px;
  border-radius: 8px;
  background-color: #67c23a;
  border-color: #67c23a;
}

/* 额外的发送按钮悬停效果 */
.extra-send-button:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

/* 额外的发送按钮禁用状态 */
.extra-send-button:disabled {
  background-color: #c0eb75;
  border-color: #c0eb75;
  color: #ffffff;
}

/* 为追加的发送按钮添加悬停效果 */
.chat-input-area .el-button:hover {
  background-color: #409eff;
  border-color: #409eff;
}

/* 为禁用状态的追加发送按钮添加样式 */
.chat-input-area .el-button:disabled {
  background-color: #a0cfff;
  border-color: #a0cfff;
  color: #ffffff;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>