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

        JPanel formPanel = new JPanel(new GridLayout(7,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel nameLabel = new JLabel("restaurant name:");
        JTextField nameField = new JTextField(15);

        JLabel locationLabel = new JLabel("location:");
        JTextField locationField = new JTextField(15);

        JLabel phoneLabel = new JLabel("phone number:");
        JFormattedTextField phoneField = new JFormattedTextField();

        try{
            MaskFormatter phoneFormatter = new MaskFormatter("###-###-####");
            phoneFormatter.setPlaceholderCharacter('_');
            phoneFormatter.install(phoneField);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        phoneField.setColumns(10);
        phoneField.setPreferredSize(new Dimension(120, phoneField.getPreferredSize().height));
        phoneField.setHorizontalAlignment(JTextField.CENTER);

        JLabel usernameLabel = new JLabel("username:");
        JTextField usernameField = new JTextField(15);

        JLabel emailLabel = new JLabel("email:");
        JTextField emailField = new JTextField(15);

        JLabel passwordLabel = new JLabel("password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton backButton = new JButton("back");
        JButton createButton = new JButton("create");

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

        add(formPanel);

        backButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        createButton.addActionListener(e -> {

            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String phone = phoneField.getText().replaceAll("[^0-9]", "");
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if(name.isEmpty() || location.isEmpty() || username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "please fill required fields.");
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

                new FoodBusinessDAO().insert(restaurant);

                JOptionPane.showMessageDialog(this, "restaurant account created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "error creating restaurant account.");
            }
        });
    }
}