// 测试TeacherView.vue API接口
const axios = require('axios');

// 设置axios基础URL
const baseURL = 'http://localhost:8080';
const api = axios.create({ baseURL });

// 测试教师列表接口
async function testTeacherList() {
  console.log('=== 测试TeacherView.vue API接口 ===\n');
  
  try {
    console.log('1. 测试教师列表接口:');
    const response = await api.get('/api/teachers', {
      params: {
        page: 1,
        size: 10
      }
    });
    
    const data = response.data;
    console.log(`   - 总教师数: ${data.total}`);
    console.log(`   - 当前页教师数: ${data.records.length}`);
    
    if (data.records.length > 0) {
      console.log('\n   - 教师列表:');
      data.records.forEach((teacher, index) => {
        console.log(`     ${index + 1}. ${teacher.name} (${teacher.teacherNumber}) - ${teacher.department} - ${teacher.title}`);
      });
    }
    
    console.log('\n=== 教师列表API测试成功！===');
    return true;
  } catch (error) {
    console.error('\n=== 教师列表API测试失败！===');
    console.error('错误信息:', error.message);
    return false;
  }
}

// 运行测试
testTeacherList();