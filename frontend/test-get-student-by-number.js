import axios from 'axios';

// 测试根据学号获取学生信息接口
const testGetStudentByNumber = async () => {
  const studentNumber = 'S001'; // 数据库中存在的学号
  
  try {
    console.log(`测试获取学号为 ${studentNumber} 的学生信息...`);
    const response = await axios.get(`http://localhost:5174/api/students/id/${studentNumber}`);
    
    console.log('响应状态:', response.status);
    console.log('学生信息:', response.data);
    console.log('学生姓名:', response.data.name);
    console.log('学生ID:', response.data.id);
    
    if (response.status === 200 && response.data && response.data.name) {
      console.log('测试成功！');
    } else {
      console.log('测试失败：未获取到有效学生信息');
    }
  } catch (error) {
    console.error('测试失败:', error.message);
    if (error.response) {
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
    }
  }
};

testGetStudentByNumber();
