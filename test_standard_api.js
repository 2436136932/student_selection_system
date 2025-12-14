const axios = require('axios');

// 测试/api/standards/page接口
const testStandardsPage = async () => {
  try {
    console.log('测试/api/standards/page接口...');
    const response = await axios.get('http://localhost:8080/api/standards/page', {
      params: {
        current: 1,
        size: 10
      }
    });
    console.log('✓ 接口调用成功');
    console.log('  状态码:', response.status);
    console.log('  返回数据:', JSON.stringify(response.data, null, 2));
    return true;
  } catch (error) {
    console.error('✗ 接口调用失败');
    console.error('  完整错误信息:', error);
    if (error.response) {
      console.error('  状态码:', error.response.status);
      console.error('  响应头:', error.response.headers);
      console.error('  错误信息:', JSON.stringify(error.response.data, null, 2));
    } else if (error.request) {
      console.error('  未收到响应:', error.request);
    } else {
      console.error('  请求配置错误:', error.message);
    }
    return false;
  }
};

// 执行测试
const runTests = async () => {
  console.log('开始测试接口...\n');
  
  const standardResult = await testStandardsPage();
  
  console.log('\n测试结果总结:');
  console.log('- /api/standards/page接口:', standardResult ? '成功' : '失败');
  
  if (standardResult) {
    console.log('\n所有接口测试通过！');
    return true;
  } else {
    console.log('\n部分或全部接口测试失败');
    return false;
  }
};

runTests();