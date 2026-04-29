package gui.create;

import gui.MainFrame;
import dao.*;
import model.Administrator;

import javax.swing.*;
import java.awt.*;

public class CreateAdminPanel extends JPanel {

    public CreateAdminPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(4,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // input fields
        // username
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        // email
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(15);

        // password
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        // buttons
        JButton backButton = new JButton("Back");
        JButton createButton = new JButton("Create");

        // form layout
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(backButton);
        formPanel.add(createButton);

        // main layout
        add(formPanel);

        // navigation action
        backButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        // create account action
        createButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            // check required fields
            if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Fill Required Fields.");
                return;
            }

            // check username across every account type
            try{
                if(usernameExists(username)){
                    JOptionPane.showMessageDialog(this, "Username Already Exists. Please Choose Another Username.");
                    return;
                }
            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Checking Username.");
                return;
            }

            try{
                Administrator admin = new Administrator(
                    0,
                    username,
                    email,
                    password
                );

                // create new admin
                new AdministratorDAO().insert(admin);

                JOptionPane.showMessageDialog(this, "Admin Account Created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Creating Admin Account.");
            }
        });
    }

    // checks if username already belongs to any account type
    private boolean usernameExists(String username) throws Exception {
        return new CustomerDAO().getByUsername(username) != null
            || new FoodBusinessDAO().getByUsername(username) != null
            || new DeliveryPersonnelDAO().getByUsername(username) != null
            || new AdministratorDAO().getByUsername(username) != null;
    }

}