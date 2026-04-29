package gui.create;

import gui.MainFrame;
import dao.*;
import model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class CreateDriverPanel extends JPanel {

    public CreateDriverPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(8,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // input fields
        // first name
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(15);

        // last name
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(15);

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

        // vehicle
        JLabel vehicleLabel = new JLabel("Vehicle Details:");
        JTextField vehicleField = new JTextField(15);

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

        // main layout
        add(formPanel);

        // navigation action
        backButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        // create account action
        createButton.addActionListener(e -> {

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().replaceAll("[^0-9]", "");
            String vehicle = vehicleField.getText().trim();
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            // check required fields
            if(firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()){
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

                // create new driver
                new DeliveryPersonnelDAO().insert(driver);

                JOptionPane.showMessageDialog(this, "Driver Account Created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Creating Driver Account.");
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