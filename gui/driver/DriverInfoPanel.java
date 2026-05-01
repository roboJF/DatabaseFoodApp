package gui.driver;

import dao.DeliveryPersonnelDAO;
import model.DeliveryPersonnel;

import javax.swing.*;
import java.awt.*;

public class DriverInfoPanel extends JPanel {

    private int driverId;

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;
    private JTextField vehicleField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public DriverInfoPanel(int driverId) {
        this.driverId = driverId;

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // input fields
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        phoneField = new JTextField(15);
        vehicleField = new JTextField(15);
        usernameField = new JTextField(15);
        usernameField.setEditable(false);
        usernameField.setFocusable(false);
        emailField = new JTextField(15);

        // password field
        passwordField = new JPasswordField(15);
        passwordField.setEchoChar('*');

        showPasswordCheckBox = new JCheckBox("Show");

        // buttons
        JButton saveButton = new JButton("Save Changes");
        JButton refreshButton = new JButton("Refresh");

        // form layout
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Vehicle Details:"));
        formPanel.add(vehicleField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));

        // password panel
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordCheckBox, BorderLayout.EAST);

        formPanel.add(passwordPanel);

        formPanel.add(refreshButton);
        formPanel.add(saveButton);

        // main layout
        add(formPanel);

        // show/hide password
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });

        // button actions
        refreshButton.addActionListener(e -> loadDriverInfo());
        saveButton.addActionListener(e -> saveDriverInfo());

        loadDriverInfo();
    }

    // load driver info
    private void loadDriverInfo() {
        try {
            DeliveryPersonnel driver = new DeliveryPersonnelDAO().getById(driverId);

            if (driver == null) {
                JOptionPane.showMessageDialog(this, "Driver not found.");
                return;
            }

            firstNameField.setText(driver.getFirstName());
            lastNameField.setText(driver.getLastName());
            phoneField.setText(driver.getContactInfo());
            vehicleField.setText(driver.getVehicleDetails());
            usernameField.setText(driver.getUsername());
            emailField.setText(driver.getEmail());
            passwordField.setText(driver.getPassword());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading driver info.");
        }
    }

    // save driver info
    private void saveDriverInfo() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String vehicle = vehicleField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // check required fields
        if (firstName.isEmpty() || lastName.isEmpty()
                || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields.");
            return;
        }

        try {
            DeliveryPersonnel updatedDriver = new DeliveryPersonnel(
                    driverId,
                    firstName,
                    lastName,
                    phone,
                    vehicle,
                    username,
                    email,
                    password
            );

            // update driver in database
            new DeliveryPersonnelDAO().update(updatedDriver);

            JOptionPane.showMessageDialog(this, "Driver info updated.");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating driver info.");
        }
    }
}