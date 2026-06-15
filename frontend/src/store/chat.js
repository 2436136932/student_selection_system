import { defineStore } from 'pinia'
import { markRaw } from 'vue'
import { useUserStore } from './user'

export const useChatStore = defineStore('chat', {
  state: () => ({
    socket: null,
    isConnected: false,
    isInitialized: false,
    _messageHandler: null,
    _reconnectTimer: null
  }),

  actions: {
    initWebSocket(onMessage) {
      if (onMessage) {
        this._messageHandler = onMessage
      }

      if (this.socket?.readyState === WebSocket.OPEN) {
        this.isConnected = true
        return
      }

      const userStore = useUserStore()
      const userId = userStore.userId
      const token = userStore.token

      if (!userId || !token) {
        this.isConnected = false
        this.isInitialized = true
        return
      }

      try {
        const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
        const ws = new WebSocket(`${wsProtocol}//${window.location.host}/ws/chat?userId=${encodeURIComponent(userId)}&token=${encodeURIComponent(token)}`)

        ws.onopen = () => {
          this.isConnected = true
          this.isInitialized = true
          if (this._reconnectTimer) {
            clearTimeout(this._reconnectTimer)
            this._reconnectTimer = null
          }
        }

        ws.onmessage = (event) => {
          if (this._messageHandler) {
            try {
              this._messageHandler(event)
            } catch (error) {
              console.error('处理WebSocket消息失败:', error)
            }
          }
        }

        ws.onclose = () => {
          this.isConnected = false
          this.isInitialized = true
          this._scheduleReconnect()
        }

        ws.onerror = (error) => {
          console.error('WebSocket连接出错:', error)
        }

        this.socket = markRaw(ws)
      } catch (error) {
        console.error('创建WebSocket连接失败:', error)
        this.isConnected = false
        this.isInitialized = true
        this._scheduleReconnect()
      }
    },

    _scheduleReconnect() {
      if (this._reconnectTimer) return
      this._reconnectTimer = setTimeout(() => {
        this._reconnectTimer = null
        this.initWebSocket()
      }, 5000)
    },

    triggerConnection() {
      this.isInitialized = false
      this.initWebSocket()
    },

    closeConnection() {
      if (this.socket) {
        this.socket.close()
        this.socket = null
      }
      if (this._reconnectTimer) {
        clearTimeout(this._reconnectTimer)
        this._reconnectTimer = null
      }
      this.isConnected = false
      this.isInitialized = false
    },

    sendMessage(message) {
      if (this.socket?.readyState === WebSocket.OPEN) {
        this.socket.send(JSON.stringify(message))
      } else {
        console.error('WebSocket连接未建立，无法发送消息')
      }
    }
  }
})
