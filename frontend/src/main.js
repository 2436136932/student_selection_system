import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// 配置axios基础URL，指向后端服务
axios.defaults.baseURL = 'http://localhost:8080'

// 添加axios拦截器，自动为每个请求添加Authorization头
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  console.log('Axios请求拦截器 - token:', token)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    console.log('Axios请求拦截器 - 添加Authorization头:', config.headers.Authorization)
  }
  return config
}, error => {
  console.error('Axios请求拦截器错误:', error)
  return Promise.reject(error)
})

const app = createApp(App)

app.use(ElementPlus)
app.use(router)
app.mount('#app')