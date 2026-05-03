package gui.restaurant;

import dao.FoodBusinessDAO;
import model.FoodBusiness;

import javax.swing.*;
import java.awt.*;

public class RestaurantInfoPanel extends JPanel {
    // restaurant can view, edit their info
    private int restaurantId;

    private JTextField nameField;
    private JTextField locationField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;

    public RestaurantInfoPanel(int restaurantId) {
        this.restaurantId = restaurantId;

        setLayout(new GridBagLayout());

        // form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // input fields for restaurant profile data
        nameField = new JTextField(15);
        locationField = new JTextField(15);
        phoneField = new JTextField(15);
        usernameField = new JTextField(15);
        // username is fixed and cannot be edited after account creation
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

        // create form layout for editing restaurant information
        formPanel.add(new JLabel("Restaurant Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));

        // password field with toggle to show/hide characters
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
        refreshButton.addActionListener(e -> loadRestaurantInfo());
        saveButton.addActionListener(e -> saveRestaurantInfo());
        // reload data to show latest
        loadRestaurantInfo();
    }

    // get restaurant info from db, populate form
    private void loadRestaurantInfo() {
        try {
            FoodBusiness restaurant = new FoodBusinessDAO().getById(restaurantId);

            if (restaurant == null) {
                JOptionPane.showMessageDialog(this, "Restaurant not found.");
                return;
            }

            nameField.setText(restaurant.getName());
            locationField.setText(restaurant.getLocation());
            phoneField.setText(restaurant.getContactInfo());
            usernameField.setText(restaurant.getUsername());
            emailField.setText(restaurant.getEmail());
            passwordField.setText(restaurant.getPassword());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading restaurant info.");
        }
    }

    // validate input, update restaurant info in db
    private void saveRestaurantInfo() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // check required fields nonempty
        if (name.isEmpty() || location.isEmpty() || phone.isEmpty()
                || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields.");
            return;
        }

        try {
            FoodBusiness updatedRestaurant = new FoodBusiness(
                    restaurantId,
                    name,
                    location,
                    phone,
                    username,
                    email,
                    password);

            // update existing restaurant in database
            new FoodBusinessDAO().update(updatedRestaurant);

            JOptionPane.showMessageDialog(this, "Restaurant info updated.");
            // refresh to show latest data after save
            loadRestaurantInfo();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating restaurant info.");
        }
    }
}