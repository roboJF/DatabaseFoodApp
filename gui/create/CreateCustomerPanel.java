package gui.create;
import gui.MainFrame;
import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

public class CreateCustomerPanel extends JPanel {

    public CreateCustomerPanel(MainFrame mainFrame){

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(8,2,10,10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel firstNameLabel = new JLabel("first name:");
        JTextField firstNameField = new JTextField(15);

        JLabel lastNameLabel = new JLabel("last name:");
        JTextField lastNameField = new JTextField(15);

        JLabel addressLabel = new JLabel("address:");
        JTextField addressField = new JTextField(15);

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

        add(formPanel);

        backButton.addActionListener(e -> {
            mainFrame.showCreateAccountPanel();
        });

        createButton.addActionListener(e -> {

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().replaceAll("[^0-9]", "");
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if(firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "please fill required fields.");
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

                new CustomerDAO().insert(customer);

                JOptionPane.showMessageDialog(this, "customer account created.");
                mainFrame.showLoginPanel();

            } catch (Exception ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "error creating customer account.");
            }
        });
    }
}