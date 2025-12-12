import axios from 'axios';

// 测试学生API
async function testStudentAPI() {
  try {
    console.log('测试学生API...');
    const response = await axios.get('http://localhost:5174/api/students');
    
    console.log('响应状态:', response.status);
    console.log('响应数据结构:', JSON.stringify(response.data, null, 2));
    
    if (response.data) {
      console.log('学生总数:', response.data.total);
      console.log('学生记录数:', response.data.records.length);
    }
    
    return response.data;
  } catch (error) {
    console.error('API调用失败:', error.message);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误数据:', error.response.data);
    }
    throw error;
  }
}

// 执行测试
testStudentAPI();