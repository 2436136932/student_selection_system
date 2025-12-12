// 测试数据库连接和查询
const mysql = require('mysql2');

// 创建数据库连接
const connection = mysql.createConnection({
  host: 'localhost',
  user: 'root',
  password: '123456',
  database: 'student_selection_system'
});

// 连接数据库
connection.connect((err) => {
  if (err) {
    console.error('数据库连接失败:', err);
    return;
  }
  console.log('数据库连接成功!');
  
  // 查询student_award_applications表的所有数据
  const sql = 'SELECT * FROM student_award_applications';
  connection.query(sql, (err, results) => {
    if (err) {
      console.error('查询失败:', err);
      connection.end();
      return;
    }
    
    console.log('student_award_applications表数据:');
    results.forEach(row => {
      console.log('ID:', row.application_id);
      console.log('Student ID:', row.student_id);
      console.log('Award ID:', row.award_id);
      console.log('Status:', row.status);
      console.log('Teacher Approval Status:', row.teacher_approval_status);
      console.log('Admin Approval Status:', row.admin_approval_status);
      console.log('------------------------');
    });
    
    // 测试统计查询
    console.log('\n测试统计查询:');
    const countSql = 'SELECT COUNT(*) AS count FROM student_award_applications WHERE award_id = 1 AND teacher_approval_status = 1';
    connection.query(countSql, (err, countResults) => {
      if (err) {
        console.error('统计查询失败:', err);
        connection.end();
        return;
      }
      console.log('教师审核通过数量:', countResults[0].count);
      connection.end();
    });
  });
});
