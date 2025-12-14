// 导入axios
const axios = require('axios');

// 配置axios基础URL
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.timeout = 10000;

// 测试HomeView.vue中调用的所有API接口
const testHomeAPI = async () => {
    console.log('=== 测试HomeView.vue API接口 ===');
    
    try {
        // 测试统计数据接口
        console.log('\n1. 测试统计数据接口:');
        
        // 并发请求所有统计接口
        const [studentsCount, awardsCount, applicationsCount] = await Promise.all([
            axios.get('/api/students/count'),
            axios.get('/api/awards/count'),
            axios.get('/api/student-award-applications/count')
        ]);
        
        console.log(`   - 学生总数: ${studentsCount.data}`);
        console.log(`   - 奖项总数: ${awardsCount.data}`);
        console.log(`   - 申请总数: ${applicationsCount.data}`);
        
        // 测试最近评选活动接口
        console.log('\n2. 测试最近评选活动接口:');
        const recentAwards = await axios.get('/api/awards/recent');
        console.log(`   - 最近评选活动数: ${recentAwards.data.length}`);
        if (recentAwards.data.length > 0) {
            console.log(`   - 第一个活动: ${recentAwards.data[0].awardName}`);
        }
        
        // 测试最近通知接口
        console.log('\n3. 测试最近通知接口:');
        const recentNotices = await axios.get('/api/notices/recent');
        console.log(`   - 最近通知数: ${recentNotices.data.length}`);
        if (recentNotices.data.length > 0) {
            console.log(`   - 第一个通知: ${recentNotices.data[0].title}`);
        }
        
        console.log('\n=== 所有API测试成功！===');
        
    } catch (error) {
        console.error('\n=== API测试失败！===');
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
testHomeAPI();