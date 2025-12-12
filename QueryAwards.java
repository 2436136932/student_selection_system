import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryAwards {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/student_selection_system?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "password";

        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            // 创建Statement
            Statement stmt = conn.createStatement();
            // 执行查询
            String sql = "SELECT id, award_name, status, current_status, end_time FROM awards LIMIT 10";
            ResultSet rs = stmt.executeQuery(sql);
            // 处理结果
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Award Name: " + rs.getString("award_name"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Current Status: " + rs.getString("current_status"));
                System.out.println("End Time: " + rs.getTimestamp("end_time"));
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