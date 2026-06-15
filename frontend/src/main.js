import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { useUserStore } from './store/user'
import './styles/global.css'
import './styles/holiday-themes.css'

// 配置axios基础URL，根据当前访问的hostname自动构建后端URL
const getBaseURL = () => {
  const hostname = window.location.hostname
  return `http://${hostname}:8080`
}
axios.defaults.baseURL = getBaseURL()

// 添加axios拦截器，自动为每个请求添加Authorization头，并过滤外部请求
axios.interceptors.request.use(config => {
  // 解析URL，检查路径部分是否以/api开头
  let urlPath = config.url
  
  // 如果是完整URL，提取路径部分
  if (urlPath.startsWith('http')) {
    const url = new URL(urlPath)
    urlPath = url.pathname
  }
  
  // 只处理我们自己的API请求，过滤掉所有外部请求
  if (!urlPath.startsWith('/api')) {
    console.warn('Axios请求拦截器 - 过滤外部请求:', config.url)
    return Promise.reject(new Error('外部请求被拦截'))
  }
  
  const userStore = useUserStore()
  const token = userStore.token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  // 忽略被拦截的外部请求错误
  if (error.message === '外部请求被拦截') {
    console.info('已拦截外部请求，忽略错误')
    return new Promise(() => {}) // 返回一个永远不会resolve的Promise，阻止请求继续
  }
  console.error('Axios请求拦截器错误:', error)
  return Promise.reject(error)
})

// 添加响应拦截器，处理响应错误
axios.interceptors.response.use(response => {
  return response
}, error => {
  console.error('Axios响应拦截器错误:', error)
  return Promise.reject(error)
})

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// 必须在 router 之前初始化 store，否则路由守卫读不到 token，刷新页面会被踢回登录页
const userStore = useUserStore()
userStore.initFromStorage()

app.use(ElementPlus)
app.use(router)
app.mount('#app')