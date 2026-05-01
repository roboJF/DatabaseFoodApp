package gui.create;

import gui.MainFrame;
import dao.*;
import model.Customer;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class CreateCustomerPanel extends JPanel {

    public CreateCustomerPanel(MainFrame mainFrame){

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

        // address
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(15);

        // phone number
        JLabel phoneLabel = new JLabel("Phone number:");
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

        // name
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
        formPanel.add(addressLabel);
        formPanel.add(addressField);
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

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().replaceAll("[^0-9]", "");
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
                Customer customer = new Customer(
                    0,
                    firstName,
                    lastName,
                    address,
                    phone,
                    username,
                    email,
                    password
                );

                // create new customer
                new CustomerDAO().insert(customer);

                JOptionPane.showMessageDialog(this, "Customer Account Created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Creating Customer Account.");
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