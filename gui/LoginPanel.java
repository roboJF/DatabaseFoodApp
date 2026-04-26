package gui;

import dao.*;
import model.*;
import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    public LoginPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(4,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel usernameLabel = new JLabel("username:");
        JTextField usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton createAccountButton = new JButton("create account");
        JButton loginButton = new JButton("login");

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel());
        formPanel.add(new JLabel());
        formPanel.add(createAccountButton);
        formPanel.add(loginButton);

        add(formPanel);

        createAccountButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            try{

                Customer c = new CustomerDAO().getByUsername(username);
                if (c != null && c.getPassword().equals(password)){
                    mainFrame.showCustomerPanel(c.getCustomerId());
                    return;
                }

                FoodBusiness b = new FoodBusinessDAO().getByUsername(username);
                if (b != null && b.getPassword().equals(password)){
                    mainFrame.showRestaurantPanel(b.getFoodBusinessId());
                    return;
                }

                DeliveryPersonnel d = new DeliveryPersonnelDAO().getByUsername(username);
                if (d != null && d.getPassword().equals(password)){
                    mainFrame.showDriverPanel(d.getDeliveryPersonnelId());
                    return;
                }

                Administrator a = new AdministratorDAO().getByUsername(username);
                if (a != null && a.getPassword().equals(password)){
                    mainFrame.showAdminPanel(a.getAdminId());
                    return;
                }

                JOptionPane.showMessageDialog(this, "invalid username or password.");

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "login error.");
            }
        });
    }
}