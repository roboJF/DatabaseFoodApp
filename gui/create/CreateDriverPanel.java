package gui.create;
import gui.MainFrame;
import dao.DeliveryPersonnelDAO;
import model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class CreateDriverPanel extends JPanel {

    public CreateDriverPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(8,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel firstNameLabel = new JLabel("first name:");
        JTextField firstNameField = new JTextField(15);

        JLabel lastNameLabel = new JLabel("last name:");
        JTextField lastNameField = new JTextField(15);

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

        JLabel vehicleLabel = new JLabel("vehicle details:");
        JTextField vehicleField = new JTextField(15);

        JLabel usernameLabel = new JLabel("username:");
        JTextField usernameField = new JTextField(15);

        JLabel emailLabel = new JLabel("email:");
        JTextField emailField = new JTextField(15);

        JLabel passwordLabel = new JLabel("password:");
        JPasswordField passwordField = new JPasswordField(15);

        JButton backButton = new JButton("back");
        JButton createButton = new JButton("create");

        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(vehicleLabel);
        formPanel.add(vehicleField);
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

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().replaceAll("[^0-9]", "");
            String vehicle = vehicleField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if(firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "please fill required fields.");
                return;
            }

            try{
                DeliveryPersonnel driver = new DeliveryPersonnel(
                    0,
                    firstName,
                    lastName,
                    phone,
                    vehicle,
                    username,
                    email,
                    password
                );

                new DeliveryPersonnelDAO().insert(driver);

                JOptionPane.showMessageDialog(this, "driver account created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "error creating driver account.");
            }
        });
    }
}