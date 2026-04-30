package dao;

import java.sql.*;
import javax.swing.*;

public class DBConnection {
    private static Connection conn = null;
    private static final String URL  = "jdbc:mysql://localhost:3306/food_delivery";
    private static final String USER = "root";

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, getPassword());
        }
        return conn;
    }

    private static String password = null;

    private static String getPassword() {
        if (password == null) {
            JPasswordField passField = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(
                null,
                passField,
                "Enter MySQL root password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (result == JOptionPane.OK_OPTION) {
                password = new String(passField.getPassword());
            } else {
                JOptionPane.showMessageDialog(null, "Password required to run application.");
                System.exit(0);
            }
        }
        return password;
    }
}