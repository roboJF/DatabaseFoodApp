package gui;

import dao.*;
import model.*;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(4,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // input fields
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        // buttons
        JButton createAccountButton = new JButton("Create Account");
        JButton loginButton = new JButton("Login");

        // form layout
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());
        formPanel.add(createAccountButton);
        formPanel.add(loginButton);

        // main layout
        add(formPanel);

        // navigation action
        createAccountButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        // login action
        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            try{

                // check customer
                Customer c = new CustomerDAO().getByUsername(username);
                if (c != null && c.getPassword().equals(password)){
                    mainFrame.showCustomerPanel(c.getCustomerId());
                    return;
                }

                // check restaurant
                FoodBusiness b = new FoodBusinessDAO().getByUsername(username);
                if (b != null && b.getPassword().equals(password)){
                    mainFrame.showRestaurantPanel(b.getFoodBusinessId());
                    return;
                }

                // check driver
                DeliveryPersonnel d = new DeliveryPersonnelDAO().getByUsername(username);
                if (d != null && d.getPassword().equals(password)){
                    mainFrame.showDriverPanel(d.getDeliveryPersonnelId());
                    return;
                }

                // check admin
                Administrator a = new AdministratorDAO().getByUsername(username);
                if (a != null && a.getPassword().equals(password)){
                    mainFrame.showAdminPanel(a.getAdminId());
                    return;
                }

                // invalid or not found
                JOptionPane.showMessageDialog(this, "Invalid Username Or Password.");

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Login Error.");
            }
        });
    }
}