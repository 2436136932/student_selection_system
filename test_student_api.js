// 导入axios
const axios = require('axios');

// 配置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.timeout = 10000;

// 测试StudentView.vue中的学生列表接口
const testStudentAPI = async () => {
    console.log('=== 测试StudentView.vue API接口 ===');
    
    try {
        // 测试学生列表接口
        console.log('\n1. 测试学生列表接口:');
        const response = await axios.get('/api/students', {
            params: {
                page: 1,
                size: 10
            }
        });
        
        console.log(`   - 总学生数: ${response.data.total}`);
        console.log(`   - 当前页学生数: ${response.data.records.length}`);
        
        if (response.data.records.length > 0) {
            console.log('\n   - 学生列表:');
            response.data.records.forEach((student, index) => {
                console.log(`     ${index + 1}. ${student.name} (${student.studentNumber}) - ${student.className} - ${student.major}`);
            });
        }
        
        console.log('\n=== 学生列表API测试成功！===');
        
    } catch (error) {
        console.error('\n=== 学生列表API测试失败！===');
        if (error.response) {
            console.error('   - 响应状态:', error.response.status);
            console.error('   - 响应数据:', error.response.data);
            console.error('   - 请求URL:', error.config.url);
        } else if (error.request) {
            console.error('   - 请求已发送但未收到响应:', error.request);
        } else {
            console.error('   - 请求配置错误:', error.message);
        }
    }
};

// 执行测试
testStudentAPI();