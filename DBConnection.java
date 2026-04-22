import java.sql.*;

public class DBConnection {
    private static Connection conn = null;
    private static final String URL  = "jdbc:mysql://localhost:3306/food_delivery";
    private static final String USER = "root";
    private static final String PASS = "password"; //change to your password

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASS);
        }
        return conn;
    }
}