package gui.create;
import gui.MainFrame;
import dao.AdministratorDAO;
import model.Administrator;

import javax.swing.*;
import java.awt.*;

public class CreateAdminPanel extends JPanel {

    public CreateAdminPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(4,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel usernameLabel = new JLabel("username:");
        JTextField usernameField = new JTextField(15);

        JLabel emailLabel = new JLabel("email:");
        JTextField emailField = new JTextField(15);

        JLabel passwordLabel = new JLabel("password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton backButton = new JButton("back");
        JButton createButton = new JButton("create");

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(backButton);
        formPanel.add(createButton);

        add(formPanel);

        backButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        createButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "please fill required fields.");
                return;
            }

            try{
                Administrator admin = new Administrator(
                    0,
                    username,
                    email,
                    password
                );

                new AdministratorDAO().insert(admin);

                JOptionPane.showMessageDialog(this, "admin account created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "error creating admin account.");
            }
        });
    }
}