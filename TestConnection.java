import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        String url  = "jdbc:mysql://localhost:3306/food_delivery";
        String user = "root";
        String pass = "password"; //change to your password

        System.out.println("Attempting to connect to MySQL...");

        try {
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection successful!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
