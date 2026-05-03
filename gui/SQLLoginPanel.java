package gui;

import dao.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SQLLoginPanel extends JPanel {
    // asks user for database login
    // default username: root
    // default password: password
    public SQLLoginPanel(MainFrame mainFrame) {
        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Database Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel usernameLabel = new JLabel("SQL Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("SQL Password:");
        JPasswordField passwordField = new JPasswordField();

        JButton connectButton = new JButton("Connect");

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel());
        formPanel.add(connectButton);

        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(formPanel, BorderLayout.CENTER);

        add(wrapper);

        connectButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                DBConnection.connect(username, password);
                mainFrame.showLoginPanel(); // correct transition
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to database.\n\n" + ex.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}