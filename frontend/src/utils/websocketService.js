// src/utils/websocketService.js
import { ref } from 'vue'

const socket = ref(null)
const isConnected = ref(false)
const isInitialized = ref(false)
const messageHandler = ref(null)
let reconnectTimer = null

// 初始化WebSocket连接
export const initializeWebSocket = (onMessage) => {
  // 如果有新的消息处理函数，更新它
  if (onMessage) {
    messageHandler.value = onMessage
  }
  
  // 如果连接已存在且状态为OPEN，直接返回
  if (socket.value?.readyState === WebSocket.OPEN) {
    console.log('WebSocket连接已存在且正常')
    isConnected.value = true
    return
  }
  
  // 获取用户ID和token
  const userId = JSON.parse(localStorage.getItem('userInfo'))?.id
  const token = localStorage.getItem('token')
  
  // 检查用户是否已登录
  if (!userId || !token) {
    console.log('用户未登录或token无效，无法建立WebSocket连接')
    isConnected.value = false
    isInitialized.value = true
    return
  }
  
  console.log('尝试建立WebSocket连接...')
  
  try {
    // 根据当前协议决定WebSocket协议
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    // 创建WebSocket连接
    socket.value = new WebSocket(`${wsProtocol}//${window.location.host}/ws/chat?userId=${userId}&token=${token}`)
    
    socket.value.onopen = () => {
      console.log('WebSocket连接已建立')
      isConnected.value = true
      isInitialized.value = true
      // 清除重连定时器
      if (reconnectTimer) {
        clearTimeout(reconnectTimer)
        reconnectTimer = null
      }
    }
    
    socket.value.onmessage = (event) => {
      if (messageHandler.value) {
        try {
          messageHandler.value(event)
        } catch (error) {
          console.error('处理WebSocket消息失败:', error)
        }
      }
    }
    
    socket.value.onclose = (event) => {
      console.log(`WebSocket连接已关闭，代码: ${event.code}, 原因: ${event.reason}`)
      isConnected.value = false
      isInitialized.value = true
      
      // 尝试重新连接
      scheduleReconnect()
    }
    
    socket.value.onerror = (error) => {
      console.error('WebSocket连接出错:', error)
      // 发生错误时，连接可能会关闭，onclose事件会处理重连
    }
  } catch (error) {
    console.error('创建WebSocket连接失败:', error)
    isConnected.value = false
    isInitialized.value = true
    
    // 尝试重新连接
    scheduleReconnect()
  }
}

// 安排重新连接
const scheduleReconnect = () => {
  // 如果已经有重连定时器，直接返回
  if (reconnectTimer) {
    return
  }
  
  // 5秒后尝试重新连接
  reconnectTimer = setTimeout(() => {
    console.log('尝试重新建立WebSocket连接...')
    reconnectTimer = null
    initializeWebSocket()
  }, 5000)
}

// 手动触发WebSocket连接
export const triggerWebSocketConnection = () => {
  // 重置初始化状态，确保重新连接
  isInitialized.value = false
  initializeWebSocket()
}

// 关闭WebSocket连接
export const closeWebSocket = () => {
  if (socket.value) {
    console.log('主动关闭WebSocket连接')
    socket.value.close()
    socket.value = null
  }
  
  // 清除重连定时器
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  
  isConnected.value = false
  isInitialized.value = false
}

// 发送消息
export const sendMessage = (message) => {
  if (socket.value?.readyState === WebSocket.OPEN) {
    socket.value.send(JSON.stringify(message))
  } else {
    console.error('WebSocket连接未建立，无法发送消息')
  }
}

// 获取连接状态
export const getConnectionStatus = () => isConnected.value

// 获取初始化状态
export const isWebSocketInitialized = () => isInitialized.value