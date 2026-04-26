package gui.create;

import gui.MainFrame;
import dao.FoodBusinessDAO;
import model.FoodBusiness;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class CreateRestaurantPanel extends JPanel {

    public CreateRestaurantPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(7,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // input fields
        // name
        JLabel nameLabel = new JLabel("Restaurant Name:");
        JTextField nameField = new JTextField(15);

        // location
        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField(15);

        // phone number
        JLabel phoneLabel = new JLabel("Phone Number:");
        JFormattedTextField phoneField = new JFormattedTextField();

        // phone format mask
        try{
            MaskFormatter phoneFormatter = new MaskFormatter("###-###-####");
            phoneFormatter.setPlaceholderCharacter('_');
            phoneFormatter.install(phoneField);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        // phone field
        phoneField.setColumns(10);
        phoneField.setPreferredSize(new Dimension(120, phoneField.getPreferredSize().height));
        phoneField.setHorizontalAlignment(JTextField.CENTER);

        // username
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        // email
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(15);

        // pasword
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        // buttons
        JButton backButton = new JButton("Back");
        JButton createButton = new JButton("Create");

        // form layout
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(locationLabel);
        formPanel.add(locationField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
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

            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String phone = phoneField.getText().replaceAll("[^0-9]", "");
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            // check required fields
            if(name.isEmpty() || location.isEmpty() || username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please Fill Required Fields.");
                return;
            }

            try{
                FoodBusiness restaurant = new FoodBusiness(
                    0,
                    name,
                    location,
                    phone,
                    username,
                    email,
                    password
                );

                // create new restaurant
                new FoodBusinessDAO().insert(restaurant);

                JOptionPane.showMessageDialog(this, "Restaurant Account Created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Creating Restaurant Account.");
            }
        });
    }
}