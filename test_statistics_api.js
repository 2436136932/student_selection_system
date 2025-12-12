const axios = require('axios');

// 测试API端点
const awardId = 1; // 假设奖项ID为1

// 测试总申请数API
console.log('测试总申请数API...');
testApi(`/api/student-award-applications/award/${awardId}/count`);

// 测试最终获奖数API
console.log('\n测试最终获奖数API...');
testApi(`/api/student-award-applications/award/${awardId}/approved-count`);

// 测试教师审核通过数API
console.log('\n测试教师审核通过数API...');
testApi(`/api/student-award-applications/award/${awardId}/teacher-approved-count`);

// 测试管理员审核通过数API
console.log('\n测试管理员审核通过数API...');
testApi(`/api/student-award-applications/award/${awardId}/admin-approved-count`);

async function testApi(endpoint) {
  try {
    const response = await axios.get(`http://localhost:8080${endpoint}`);
    console.log(`成功: ${endpoint} 返回 ${response.data}`);
  } catch (error) {
    console.error(`失败: ${endpoint}`);
    if (error.response) {
      console.error(`状态码: ${error.response.status}`);
      console.error(`响应数据: ${JSON.stringify(error.response.data)}`);
    } else if (error.request) {
      console.error('没有收到响应:', error.request);
    } else {
      console.error('请求错误:', error.message);
    }
  }
}