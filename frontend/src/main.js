import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import './styles/global.css'

// 配置axios基础URL，指向后端服务
axios.defaults.baseURL = 'http://localhost:8080'

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
  
  const token = localStorage.getItem('token')
  console.log('Axios请求拦截器 - token:', token)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    console.log('Axios请求拦截器 - 添加Authorization头:', config.headers.Authorization)
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

app.use(ElementPlus)
app.use(router)
app.mount('#app')