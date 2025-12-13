import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckMajorsData {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/student_selection_system?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "123456";

        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(url, username, password);
            // 创建Statement对象
            Statement statement = connection.createStatement();
            // 执行查询语句
            ResultSet resultSet = statement.executeQuery("SELECT * FROM majors");

            // 输出查询结果
            System.out.println("Majors表数据:");
            System.out.println("ID\tName\tDepartment\tCreate Time");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String department = resultSet.getString("department");
                String createTime = resultSet.getString("create_time");
                System.out.println(id + "\t" + name + "\t" + department + "\t" + createTime);
            }

            // 关闭资源
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}