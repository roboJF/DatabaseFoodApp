package gui.customer;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerInfoPanel extends JPanel {

    private int customerId;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public CustomerInfoPanel(int customerId) {
        this.customerId = customerId;

        setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        addressField = new JTextField(15);
        phoneField = new JTextField(15);
        usernameField = new JTextField(15);
        emailField = new JTextField(15);

        passwordField = new JPasswordField(15);
        passwordField.setEchoChar('*');

        showPasswordCheckBox = new JCheckBox("Show");

        JButton saveButton = new JButton("Save Changes");
        JButton refreshButton = new JButton("Refresh");

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));

        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordCheckBox, BorderLayout.EAST);

        formPanel.add(passwordPanel);

        formPanel.add(refreshButton);
        formPanel.add(saveButton);

        add(formPanel);

        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });

        refreshButton.addActionListener(e -> loadCustomerInfo());
        saveButton.addActionListener(e -> saveCustomerInfo());

        loadCustomerInfo();
    }

    private void loadCustomerInfo() {
        try {
            Customer customer = new CustomerDAO().getById(customerId);

            if (customer == null) {
                JOptionPane.showMessageDialog(this, "Customer not found.");
                return;
            }

            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            addressField.setText(customer.getAddress());
            phoneField.setText(customer.getContactInfo());
            usernameField.setText(customer.getUsername());
            emailField.setText(customer.getEmail());
            passwordField.setText(customer.getPassword());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customer info.");
        }
    }

    private void saveCustomerInfo() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()
                || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields.");
            return;
        }

        try {
            Customer updatedCustomer = new Customer(
                    customerId,
                    firstName,
                    lastName,
                    address,
                    phone,
                    username,
                    email,
                    password
            );

            new CustomerDAO().update(updatedCustomer);

            JOptionPane.showMessageDialog(this, "Customer info updated.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer info.");
        }
    }
}