import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryAwards {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/student_selection_system?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";

        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            // 创建Statement
            Statement stmt = conn.createStatement();
            // 执行查询
            String sql = "SELECT * FROM student_award_applications";
            ResultSet rs = stmt.executeQuery(sql);
            // 处理结果
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Student ID: " + rs.getLong("student_id"));
                System.out.println("Award ID: " + rs.getInt("award_id"));
                System.out.println("Status: " + rs.getInt("status"));
                System.out.println("Teacher Approval Status: " + rs.getInt("teacher_approval_status"));
                System.out.println("Admin Approval Status: " + rs.getInt("admin_approval_status"));
                System.out.println("------------------------");
            }
            // 关闭资源
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}