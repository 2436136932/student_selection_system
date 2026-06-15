import { defineStore } from 'pinia'
import axios from 'axios'
import { getCurrentTheme, applyThemeCSS, getThemeStatusText, isSolemnTheme, ThemeMode } from '../utils/holidayTheme'

export const useAppStore = defineStore('app', {
  state: () => ({
    isDarkMode: false,
    isCompactMode: false,
    isCollapse: false,
    onlineUserCount: 0,
    unreadMessageCount: 0,
    currentHolidayTheme: null,
    holidayThemeMode: ThemeMode.AUTO,
    manualHolidayTheme: null
  }),

  getters: {
    themeStatusText: (state) => getThemeStatusText(state.currentHolidayTheme),
    isSolemn: (state) => isSolemnTheme(state.currentHolidayTheme),
    sidebarWidth: (state) => state.isCollapse ? '64px' : '220px'
  },

  actions: {
    initDisplaySettings() {
      const saved = localStorage.getItem('displaySettings')
      if (saved) {
        try {
          const settings = JSON.parse(saved)
          this.isCompactMode = settings.compactMode || false
          this.isDarkMode = settings.darkMode || false
        } catch (e) {
          console.error('解析显示设置失败:', e)
        }
      }
    },

    saveDisplaySettings() {
      localStorage.setItem('displaySettings', JSON.stringify({
        compactMode: this.isCompactMode,
        darkMode: this.isDarkMode
      }))
    },

    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },

    async fetchOnlineUserCount() {
      try {
        const response = await axios.get('/api/chats/online-count')
        if (response.data && typeof response.data.onlineCount === 'number') {
          this.onlineUserCount = response.data.onlineCount
        } else {
          console.warn('fetchOnlineUserCount: unexpected response', response.data)
          this.onlineUserCount = 0
        }
      } catch (error) {
        console.error('获取在线人数失败:', error)
      }
    },

    async fetchUnreadMessageCount() {
      try {
        const response = await axios.get('/api/chats/unread-count')
        if (response.data) {
          this.unreadMessageCount = response.data.unreadCount || 0
        }
      } catch (error) {
        console.error('获取未读消息数量失败:', error)
      }
    },

    initHolidayTheme() {
      const saved = localStorage.getItem('holidayThemeSettings')
      if (saved) {
        try {
          const settings = JSON.parse(saved)
          this.holidayThemeMode = settings.mode || ThemeMode.AUTO
          this.manualHolidayTheme = settings.manualTheme || null
        } catch (e) {
          console.error('解析节日主题设置失败:', e)
        }
      }
      this.updateHolidayTheme()
    },

    updateHolidayTheme() {
      const theme = getCurrentTheme(this.manualHolidayTheme, this.holidayThemeMode)
      this.currentHolidayTheme = theme
      applyThemeCSS(theme)
    },

    setHolidayThemeMode(mode) {
      this.holidayThemeMode = mode
      if (mode === ThemeMode.AUTO) {
        this.manualHolidayTheme = null
      }
      this.saveHolidayThemeSettings()
      this.updateHolidayTheme()
    },

    setManualTheme(themeKey) {
      this.manualHolidayTheme = themeKey
      this.saveHolidayThemeSettings()
      this.updateHolidayTheme()
    },

    saveHolidayThemeSettings() {
      localStorage.setItem('holidayThemeSettings', JSON.stringify({
        mode: this.holidayThemeMode,
        manualTheme: this.manualHolidayTheme
      }))
    }
  }
})
