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
          <span class="header-status" :class="isUserOnline ? 'online' : 'offline'">
            {{ isUserOnline ? '在线' : '离线' }}
          </span>
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
        <div class="message-content" :class="{ 
          'file-message': message.messageType === 'file',
          'image-message': message.messageType === 'file' && isImageFile(message.fileName)
        }">
          <!-- 图片文件预览 -->
          <div v-if="message.messageType === 'file' && isImageFile(message.fileName)" class="image-container">
            <img :src="message.fileUrl" alt="图片预览" @click="showImagePreview(message.fileUrl)" />
            <div class="image-actions">
              <el-button 
                type="text" 
                size="small" 
                @click.stop="handleFileDownload(message.fileUrl, message.fileName)"
              >
                <el-icon><Download /></el-icon> 下载
              </el-button>
            </div>
          </div>
          
          <!-- 其他文件消息 -->
          <div v-else-if="message.messageType === 'file'" class="file-container" @click="handleFileDownload(message.fileUrl, message.fileName)">
            <div class="file-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="file-info">
              <div class="file-name">{{ message.fileName }}</div>
              <div class="file-size">{{ formatFileSize(message.fileSize) }}</div>
            </div>
            <div class="file-action">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
          
          <!-- 文本消息 -->
          <span v-else>{{ message.content }}</span>
        </div>
        <div class="message-time">
            {{ formatTime(message.createdAt) }}
            <span v-if="message.readStatus === 'read'" class="read-status">
              <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="8" cy="8" r="7" stroke="#409EFF" stroke-width="2" fill="transparent"/>
                <path d="M5 8L7 10L11 6" stroke="#409EFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </span>
          </div>
      </div>
      
      <div v-if="messages.length === 0" class="empty-messages">
        <el-empty description="暂无消息，开始聊天吧" />
      </div>
    </div>
    
    <!-- 消息输入区域 -->
      <div class="chat-input-area">
        <div class="input-toolbar">
          <el-button type="text" @click="triggerFileUpload" class="toolbar-btn">
            <el-icon><Document /></el-icon>
          </el-button>
        </div>
        <div class="input-hint">
          <small>按 Enter 发送消息，Shift + Enter 换行</small>
        </div>
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :rows="2"
            placeholder="输入消息..."
            resize="none"
            @keyup.enter.exact="handleSendMessage"
            @keyup.enter.shift="handleEnterShift"
            class="message-input"
          ></el-input>
          <el-button 
            type="primary" 
            @click="handleSendMessage" 
            :disabled="!inputMessage.trim()" 
            size="default"
            class="send-button"
          >
            <el-icon style="margin-right: 4px;"><Right /></el-icon>
            发送
          </el-button>
        </div>
        
        <!-- 文件上传 -->
        <input
          ref="fileInputRef"
          type="file"
          accept="*"
          @change="handleFileSelect"
          style="display: none"
        />
      </div>
      
      <!-- 图片预览对话框 -->
      <el-dialog
        v-model="previewVisible"
        title="图片预览"
        width="80%"
        :close-on-click-modal="true"
      >
        <div class="preview-container">
          <img :src="previewImageUrl" :alt="previewTitle" class="preview-image" />
          <div class="preview-footer">{{ previewTitle }}</div>
        </div>
      </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { Close, Right, CircleCheck, CirclePlus, Document, Download } from '@element-plus/icons-vue'
import { ElDialog, ElMessage } from 'element-plus'

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
  },
  onlineUsers: {
    type: Array,
    default: () => []
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

// 判断当前聊天用户是否在线
const isUserOnline = computed(() => {
  if (!props.onlineUsers || !props.chatUser) return false
  return props.onlineUsers.includes(props.chatUser.id.toString())
})

// 获取用户姓名首字母
const getInitials = (name) => {
  return name.charAt(0).toUpperCase()
}

// 输入框消息内容
const inputMessage = ref('')

// 消息容器引用
const messagesContainer = ref(null)

// 文件上传输入框引用
const fileInputRef = ref(null)

// 图片预览相关
const previewVisible = ref(false)
const previewImageUrl = ref('')
const previewTitle = ref('')

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

// 触发文件上传
const triggerFileUpload = () => {
  fileInputRef.value?.click()
}

// 处理文件选择
const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const maxSize = 10 * 1024 * 1024 // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }
  
  try {
    // 上传文件到服务器
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await fetch('http://localhost:8080/api/chats/files/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    })
    
    if (response.ok) {
      const result = await response.json()
      
      const message = {
        sessionId: props.session.id,
        senderId: currentUserId.value,
        receiverId: props.session.user1Id === currentUserId.value ? props.session.user2Id : props.session.user1Id,
        senderType: currentUserRole.value,
        content: result.fileName, // 使用文件名作为显示内容
        messageType: 'file',
        fileName: result.fileName,
        fileUrl: result.fileUrl,
        fileSize: result.fileSize,
        readStatus: 'unread'
      }
      emit('send-message', message)
      inputMessage.value = ''
      scrollToBottom()
      ElMessage.success('文件发送成功')
    } else {
      throw new Error('文件上传失败')
    }
  } catch (error) {
    ElMessage.error('文件发送失败')
    console.error('文件发送失败:', error)
  }
  
  event.target.value = ''
}

// 处理文件下载
const handleFileDownload = async (fileUrl, fileName) => {
  try {
    const response = await fetch(`http://localhost:8080/api/chats/files/download?fileUrl=${encodeURIComponent(fileUrl)}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (response.ok) {
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName || 'download.file'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      window.URL.revokeObjectURL(url)
      ElMessage.success('文件下载成功')
    } else {
      throw new Error('文件下载失败')
    }
  } catch (error) {
    ElMessage.error('文件下载失败')
    console.error('文件下载失败:', error)
  }
}

// 处理关闭会话
const handleCloseSession = () => {
  emit('close-session', props.session.id)
  console.log('会话已关闭:', props.session.id)
}

// 检查是否为图片文件
const isImageFile = (fileName) => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.svg']
  const extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.'))
  return imageExtensions.includes(extension)
}

// 显示图片预览
const showImagePreview = async (imageUrl, title = '图片预览') => {
  try {
    // 使用下载接口获取图片内容
    const response = await fetch(`http://localhost:8080/api/chats/files/download?fileUrl=${encodeURIComponent(imageUrl)}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (response.ok) {
      const blob = await response.blob()
      // 创建临时URL用于预览
      const tempUrl = window.URL.createObjectURL(blob)
      
      previewImageUrl.value = tempUrl
      previewTitle.value = title
      previewVisible.value = true
    } else {
      throw new Error('获取图片失败')
    }
  } catch (error) {
    ElMessage.error('图片预览失败')
    console.error('图片预览失败:', error)
  }
}

// 关闭图片预览
const handleClosePreview = () => {
  // 释放临时URL
  if (previewImageUrl.value) {
    window.URL.revokeObjectURL(previewImageUrl.value)
    previewImageUrl.value = ''
  }
  previewVisible.value = false
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 标记消息为已读
const markMessagesAsRead = async () => {
  try {
    await fetch(`http://localhost:8080/api/chats/sessions/${props.session.id}/read-all`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'application/json'
      }
    })
    console.log('消息已标记为已读')
    // 更新App.vue中的未读消息数量
    window.dispatchEvent(new CustomEvent('updateUnreadCount'))
  } catch (error) {
    console.error('标记消息为已读失败:', error)
  }
}

// 组件挂载时标记消息为已读
onMounted(() => {
  markMessagesAsRead()
})

// 监听新消息，标记为已读
watch(
  () => props.messages,
  (newMessages, oldMessages) => {
    if (newMessages.length > oldMessages.length) {
      // 有新消息，检查是否是收到的消息
      const newMessage = newMessages[newMessages.length - 1]
      if (newMessage.senderId !== currentUserId.value) {
        markMessagesAsRead()
      }
    }
  },
  { deep: true }
)
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
  flex-shrink: 0;
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
  display: flex;
  align-items: center;
}

.header-status.online {
  color: #67c23a;
}

.header-status.online::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #67c23a;
  margin-right: 4px;
}

.header-status.offline {
  color: #909399;
}

.header-status.offline::before {
  content: '';
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #909399;
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
  min-height: 0; /* 解决flex子元素溢出问题 */
}

.message-item {
  display: flex;
  margin-bottom: 16px;
  animation: messageSlideIn 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-item.sent {
  justify-content: flex-end;
  animation: messageSlideInRight 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-item.received {
  justify-content: flex-start;
  animation: messageSlideInLeft 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.message-content {
  max-width: 70%;
  padding: 14px 18px;
  border-radius: 20px;
  word-wrap: break-word;
  line-height: 1.6;
  font-size: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.message-item.sent .message-content {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  border-bottom-right-radius: 6px;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.message-item.sent .message-content:hover {
  box-shadow: 0 6px 16px rgba(52, 152, 219, 0.4);
  transform: translateY(-2px);
}

.message-item.received .message-content {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  color: #2c3e50;
  border-bottom-left-radius: 6px;
  border: 1px solid rgba(52, 152, 219, 0.15);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-item.received .message-content:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
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

/* 已读状态样式 */
.read-status {
  display: inline-flex;
  align-items: center;
  margin-left: 4px;
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
  flex-shrink: 0;
}

.input-hint {
  margin-bottom: 12px;
  text-align: right;
  color: #909399;
  font-size: 12px;
}

/* 输入框和按钮的包装容器 */
.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  width: 100%;
  margin-top: 8px;
}

/* 消息输入框样式 */
.message-input {
  flex: 1;
  border-radius: 8px;
  width: 100%;
  max-height: 150px;
  overflow-y: auto;
}

/* 发送按钮样式 */
.send-button {
  min-width: 80px;
  height: 40px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  border: none;
  color: #ffffff;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3);
  transition: all 0.3s ease;
}

.send-button:hover {
  background: linear-gradient(135deg, #2980b9 0%, #3498db 100%);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.4);
  transform: translateY(-2px);
}

.send-button:active {
  transform: translateY(0);
}

/* 工具栏样式 */
.input-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.toolbar-btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 16px;
  color: #606266;
  background: rgba(52, 152, 219, 0.1);
  transition: all 0.3s ease;
}

.toolbar-btn:hover {
  background: rgba(52, 152, 219, 0.2);
  color: #3498db;
  transform: scale(1.1);
}

/* 图片消息样式 */
.message-content.image-message {
  padding: 8px;
  background: transparent;
  box-shadow: none;
  border: none;
}

.message-content.image-message img {
  max-width: 280px;
  max-height: 280px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.message-content.image-message img:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.image-container {
  position: relative;
  display: inline-block;
  padding-bottom: 50px;
}

.image-actions {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  border-radius: 16px;
  padding: 8px 16px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-container:hover .image-actions {
  opacity: 1;
}

/* 文件消息样式 */
.message-content.file-message {
  padding: 12px;
  background: white;
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.message-content.file-message:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.message-item.sent .message-content.file-message {
  background: white;
  color: #2c3e50;
}

.file-container {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.file-icon {
  font-size: 24px;
  color: #3498db;
  flex-shrink: 0;
}

.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.file-action {
  color: #3498db;
  font-size: 16px;
  flex-shrink: 0;
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

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes messageSlideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes messageSlideInRight {
  from {
    opacity: 0;
    transform: translateX(30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes bounceIn {
  0% {
    opacity: 0;
    transform: scale(0.3);
  }
  50% {
    transform: scale(1.1);
  }
  70% {
    transform: scale(0.9);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

/* 图片预览样式 */
.preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.preview-footer {
  margin-top: 16px;
  font-size: 14px;
  color: #606266;
  text-align: center;
}
</style>