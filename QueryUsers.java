import java.sql.*;

public class QueryUsers {
    public static void main(String[] args) {
        try {
            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 连接数据库
            String url = "jdbc:mysql://localhost:3306/student_selection_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "123456";
            Connection conn = DriverManager.getConnection(url, username, password);
            
            // 创建查询语句
            String sql = "SELECT * FROM users WHERE real_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "张三");
            
            // 执行查询
            ResultSet rs = pstmt.executeQuery();
            
            // 输出结果
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("用户名: " + rs.getString("username"));
                System.out.println("真实姓名: " + rs.getString("real_name"));
                System.out.println("邮箱: " + rs.getString("email"));
                System.out.println("电话: " + rs.getString("phone"));
                System.out.println("角色: " + rs.getString("role"));
                System.out.println("=========================");
            }
            
            // 关闭连接
            rs.close();
            pstmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}