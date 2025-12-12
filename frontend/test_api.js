import axios from 'axios';

// 测试获取所有教师API
axios.get('http://localhost:8080/api/teachers')
  .then(response => {
    console.log('教师API返回结果结构:');
    console.log('是否包含records属性:', response.data.hasOwnProperty('records'));
    console.log('records类型:', typeof response.data.records);
    console.log('教师数量:', response.data.records ? response.data.records.length : 0);
    console.log('\n');
  })
  .catch(error => {
    console.error('获取教师列表失败:', error);
  });

// 测试获取所有学生API
axios.get('http://localhost:8080/api/students')
  .then(response => {
    console.log('学生API返回结果结构:');
    console.log('是否包含records属性:', response.data.hasOwnProperty('records'));
    console.log('records类型:', typeof response.data.records);
    console.log('学生数量:', response.data.records ? response.data.records.length : 0);
    console.log('\n');
  })
  .catch(error => {
    console.error('获取学生列表失败:', error);
  });

// 测试获取所有课程(包含教师信息)API
axios.get('http://localhost:8080/api/courses/with-teacher')
  .then(response => {
    console.log('课程(包含教师信息)API返回结果结构:');
    console.log('是否为数组:', Array.isArray(response.data));
    console.log('课程数量:', response.data ? response.data.length : 0);
    console.log('\n');
  })
  .catch(error => {
    console.error('获取课程列表失败:', error);
  });
