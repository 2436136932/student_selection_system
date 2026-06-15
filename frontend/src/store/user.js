import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: {},
    token: ''
  }),

  getters: {
    isLoggedIn: (state) => !!state.token && !!state.userInfo.username,
    role: (state) => state.userInfo.role || '',
    roleName: (state) => {
      const map = { admin: '管理员', teacher: '教师', student: '学生' }
      return map[state.userInfo.role] || '用户'
    },
    userId: (state) => state.userInfo.id,
    displayName: (state) => state.userInfo.name || state.userInfo.username || ''
  },

  actions: {
    initFromStorage() {
      const userInfoStr = localStorage.getItem('userInfo')
      const token = localStorage.getItem('token')
      if (userInfoStr) {
        try {
          this.userInfo = JSON.parse(userInfoStr)
        } catch (e) {
          this.userInfo = {}
        }
      }
      if (token) {
        this.token = token
      }
    },

    login(userData, token) {
      this.userInfo = userData
      this.token = token
      localStorage.setItem('userInfo', JSON.stringify(userData))
      localStorage.setItem('token', token)
    },

    logout() {
      this.userInfo = {}
      this.token = ''
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    },

    updateUserInfo(partial) {
      this.userInfo = { ...this.userInfo, ...partial }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },

    hasRole(role) {
      return this.role && this.role.toLowerCase() === role.toLowerCase()
    }
  }
})
