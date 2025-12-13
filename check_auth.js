// 导入axios
const axios = require('axios');

// 配置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.timeout = 10000;

// 配置axios
console.log('=== 配置axios ===');

// 先登录获取token
console.log('\n发送登录请求...');
axios.post('/api/auth/login', {
    username: 'admin',
    password: 'password'
})
.then(response => {
    console.log('登录成功:', response.data);
    
    // 设置token到axios默认headers
    const token = response.data.token;
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    console.log('已设置Authorization头:', axios.defaults.headers.common['Authorization']);
    
    // 检查axios配置
    console.log('\n=== 检查axios配置 ===');
    console.log('Axios存在');
    console.log('Axios基础URL:', axios.defaults.baseURL);
    console.log('超时时间:', axios.defaults.timeout);
    console.log('默认headers:', axios.defaults.headers.common);
    console.log('Axios拦截器:', axios.interceptors.request);
    
    // 测试请求 - 获取所有专业
    console.log('\n=== 测试请求 ===');
    console.log('发送测试请求...');
    return axios.get('/api/majors');
})
.then(response => {
    console.log('请求成功:', response.status);
    console.log('响应数据:', response.data);
})
.catch(error => {
    console.error('请求失败:', error);
    if (error.response) {
        console.error('响应状态:', error.response.status);
        console.error('响应数据:', error.response.data);
        console.error('响应头:', error.response.headers);
    } else if (error.request) {
        console.error('请求已发送但未收到响应:', error.request);
    } else {
        console.error('请求配置错误:', error.message);
    }
});