const axios = require('axios');

async function testRecentAwardsAPI() {
    try {
        const response = await axios.get('http://localhost:8080/api/awards/recent');
        
        console.log('API返回的最近评选活动数据:');
        console.log('状态码:', response.status);
        console.log('数据:');
        
        response.data.forEach(award => {
            console.log('-------------------');
            console.log('奖项名称:', award.awardName);
            console.log('状态:', award.status);
            console.log('当前状态:', award.currentStatus);
            console.log('结束时间:', award.endTime);
        });
        
    } catch (error) {
        console.error('API调用失败:', error.message);
        if (error.response) {
            console.error('响应状态:', error.response.status);
            console.error('响应数据:', error.response.data);
        }
    }
}

testRecentAwardsAPI();