import java.sql.*;

public class QueryStudents {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_selection_system?allowPublicKeyRetrieval=true", "root", "123456");
            Statement stmt = conn.createStatement();
            
            // 查询学生总数
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM students");
            if (rs.next()) {
                System.out.println("学生总数: " + rs.getInt("total"));
            }
            
            // 查询前5名学生数据
            rs = stmt.executeQuery("SELECT * FROM students LIMIT 5");
            System.out.println("前5名学生数据: ");
            while (rs.next()) {
                System.out.println("ID: " + rs.getLong("id") + " 学号: " + rs.getString("student_number") + " 姓名: " + rs.getString("name"));
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}