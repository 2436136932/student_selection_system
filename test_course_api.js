const axios = require('axios');

// 测试/api/courses/with-teacher接口
const testCoursesWithTeacher = async () => {
  console.log('=== 测试/api/courses/with-teacher接口 ===');
  try {
    const response = await axios.get('http://localhost:8080/api/courses/with-teacher');
    console.log('✓ 成功获取课程列表');
    console.log('数据条数:', response.data.length);
    return true;
  } catch (error) {
    console.error('✗ 接口调用失败:', error.response?.status || error.message);
    return false;
  }
};

// 测试/api/majors/search接口
const testMajorsSearch = async () => {
  console.log('\n=== 测试/api/majors/search接口 ===');
  try {
    const response = await axios.get('http://localhost:8080/api/majors/search', {
      params: { page: 1, size: 10 }
    });
    console.log('✓ 成功获取专业列表');
    console.log('数据条数:', response.data.records?.length || 0);
    return true;
  } catch (error) {
    console.error('✗ 接口调用失败:', error.response?.status || error.message);
    return false;
  }
};

// 测试/api/courses/page接口
const testCoursesPage = async () => {
  console.log('\n=== 测试/api/courses/page接口 ===');
  try {
    const response = await axios.get('http://localhost:8080/api/courses/page', {
      params: { current: 1, size: 10 }
    });
    console.log('✓ 成功获取课程分页数据');
    console.log('数据条数:', response.data.records?.length || 0);
    return true;
  } catch (error) {
    console.error('✗ 接口调用失败:', error.response?.status || error.message);
    return false;
  }
};

// 运行所有测试
const runTests = async () => {
  console.log('开始测试CourseView.vue相关API接口...');
  
  const result1 = await testCoursesWithTeacher();
  const result2 = await testMajorsSearch();
  const result3 = await testCoursesPage();
  
  console.log('\n' + '='.repeat(50));
  console.log('测试结果总结:');
  console.log('/api/courses/with-teacher:', result1 ? '成功' : '失败');
  console.log('/api/majors/search:', result2 ? '成功' : '失败');
  console.log('/api/courses/page:', result3 ? '成功' : '失败');
  
  if (result1 && result2 && result3) {
    console.log('✅ 所有接口测试通过！');
  } else {
    console.log('❌ 部分或全部接口测试失败！');
  }
};

runTests();