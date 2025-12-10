import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryUsers {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username = "system";
        String password = "123456";
        
        try {
            // 加载驱动
            Class.forName("oracle.jdbc.OracleDriver");
            
            // 建立连接
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
            
            // 查询用户表
            String sql = "SELECT id, username, password, role, status FROM users";
            ResultSet rs = stmt.executeQuery(sql);
            
            // 打印结果
            System.out.println("ID | Username | Password | Role | Status");
            System.out.println("------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("id");
                String user = rs.getString("username");
                String pass = rs.getString("password");
                String role = rs.getString("role");
                int status = rs.getInt("status");
                System.out.printf("%d | %s | %s | %s | %d\n", id, user, pass, role, status);
            }
            
            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}