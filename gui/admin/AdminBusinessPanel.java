package gui.admin;

import dao.AdminManagesDAO;
import dao.FoodBusinessDAO;
import model.FoodBusiness;
import javax.swing.text.MaskFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class AdminBusinessPanel extends JPanel {
    // the admin will manage restaurants through this panel
    private int adminId; // current admins id

    // data access objects
    private FoodBusinessDAO businessDAO = new FoodBusinessDAO();
    private AdminManagesDAO managesDAO = new AdminManagesDAO();

    private JTable businessTable; // visual table
    private DefaultTableModel businessModel; // handle data in table

    // panel constructor
    public AdminBusinessPanel(int adminId) {
        this.adminId = adminId;

        // spacing and padding
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildPanel();
        loadBusinesses();
    }

    // create table, buttons, actions
    private void buildPanel() {
        // table columns
        String[] cols = { "ID", "Name", "Location", "Contact", "Username", "Email" };

        businessModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        businessTable = new JTable(businessModel);
        businessTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // limit selection to single row

        JButton registerButton = new JButton("Register Business");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        // buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(businessTable), BorderLayout.CENTER); // scrollable list
        add(buttonPanel, BorderLayout.SOUTH);

        // button actions
        registerButton.addActionListener(e -> showRegisterBusinessDialog());
        editButton.addActionListener(e -> showEditBusinessDialog());
        deleteButton.addActionListener(e -> deleteSelectedBusiness());
        refreshButton.addActionListener(e -> loadBusinesses());
    }

    // get the business accounts
    private void loadBusinesses() {
        businessModel.setRowCount(0);
        // get the businesses to populate rows in the list
        try {
            for (FoodBusiness business : businessDAO.getAll()) {
                businessModel.addRow(new Object[] {
                        business.getFoodBusinessId(),
                        business.getName(),
                        business.getLocation(),
                        business.getContactInfo(),
                        business.getUsername(),
                        business.getEmail()
                });
            }

        } catch (SQLException e) {
            showError("Error loading businesses: " + e.getMessage());
        }
    }

    // create new business account
    private void showRegisterBusinessDialog() {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        // JTextField contactField = new JTextField();

        // phone number
        JFormattedTextField contactField = new JFormattedTextField();

        // phone format mask
        try {
            MaskFormatter phoneFormatter = new MaskFormatter("###-###-####");
            phoneFormatter.setPlaceholderCharacter('_');
            phoneFormatter.install(contactField);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // phone field
        contactField.setColumns(10);
        contactField.setPreferredSize(new Dimension(120, contactField.getPreferredSize().height));
        contactField.setHorizontalAlignment(JTextField.CENTER);

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        // show password
        JCheckBox showPasswordBox = new JCheckBox("Show Password");
        char defaultEchoChar = passwordField.getEchoChar();

        showPasswordBox.addActionListener(e -> {
            if (showPasswordBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultEchoChar);
            }
        });

        JPanel form = buildForm(
                new String[] { "Business Name", "Location", "Contact Info", "Username", "Email", "Password", "" },
                new JComponent[] { nameField, locationField, contactField, usernameField, emailField, passwordField,
                        showPasswordBox });

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Register New Business",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // clean and validate
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();
                String contact = contactField.getText().replaceAll("[^0-9]", "");
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                // check required fields
                if (hasBlankFields(name, location, contact, username, email, password)) {
                    showError("Please fill required fields.");
                    return;
                }

                // check username uniqueness
                if (businessDAO.getByUsername(username) != null) {
                    showError("Username already taken.");
                    return;
                }

                businessDAO.insert(new FoodBusiness(
                        0,
                        name,
                        location,
                        contact,
                        username,
                        email,
                        password));

                // link admin to this new business
                int newId = businessDAO.getByUsername(username).getFoodBusinessId();
                managesDAO.assignAdminToBusiness(adminId, newId);

                showSuccess("Business registered successfully.");
                loadBusinesses();

            } catch (SQLException e) {
                showError("Error registering business: " + e.getMessage());
            }
        }
    }

    // edit existing business
    private void showEditBusinessDialog() {
        int row = businessTable.getSelectedRow();

        if (row == -1) { // if user didnt select a row
            showError("Please select a business to edit.");
            return;
        }

        int id = (int) businessModel.getValueAt(row, 0);

        try {
            FoodBusiness existing = businessDAO.getById(id);

            if (existing == null) {
                showError("Business not found.");
                return;
            }

            JTextField nameField = new JTextField(existing.getName());
            JTextField locationField = new JTextField(existing.getLocation());
            // JTextField contactField = new JTextField(existing.getContactInfo());

            // phone number
            JFormattedTextField contactField = new JFormattedTextField();

            // phone format mask
            try {
                MaskFormatter phoneFormatter = new MaskFormatter("###-###-####");
                phoneFormatter.setPlaceholderCharacter('_');
                phoneFormatter.install(contactField);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // phone field
            contactField.setColumns(10);
            contactField.setPreferredSize(new Dimension(120, contactField.getPreferredSize().height));
            contactField.setHorizontalAlignment(JTextField.CENTER);

            contactField.setText(existing.getContactInfo());

            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passwordField = new JPasswordField(existing.getPassword());

            // show password
            JCheckBox showPasswordBox = new JCheckBox("Show Password");
            char defaultEchoChar = passwordField.getEchoChar();

            showPasswordBox.addActionListener(e -> {
                if (showPasswordBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar(defaultEchoChar);
                }
            });

            JPanel form = buildForm(
                    new String[] { "Business Name", "Location", "Contact Info", "Username", "Email", "Password", "" },
                    new JComponent[] { nameField, locationField, contactField, usernameField, emailField, passwordField,
                            showPasswordBox });
            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Business - " + existing.getName(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();
                String contact = contactField.getText().replaceAll("[^0-9]", "");
                String newUsername = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                // check required fields
                if (hasBlankFields(name, location, contact, newUsername, email, password)) {
                    showError("Please fill required fields.");
                    return;
                }

                // check username if changed
                if (!newUsername.equals(existing.getUsername())) {
                    if (businessDAO.getByUsername(newUsername) != null) {
                        showError("Username already taken.");
                        return;
                    }
                }

                existing.setName(name);
                existing.setLocation(location);
                existing.setContactInfo(contact);
                existing.setUsername(newUsername);
                existing.setEmail(email);
                existing.setPassword(password);

                businessDAO.update(existing);

                showSuccess("Business updated successfully.");
                loadBusinesses();
            }

        } catch (SQLException e) {
            showError("Error editing business: " + e.getMessage());
        }
    }

    // delete business
    private void deleteSelectedBusiness() {
        int row = businessTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a business to delete.");
            return;
        }

        int id = (int) businessModel.getValueAt(row, 0);
        String name = (String) businessModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete business: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                businessDAO.delete(id);
                showSuccess("Business deleted.");
                loadBusinesses();

            } catch (SQLException e) {
                showError("Error deleting business: " + e.getMessage());
            }
        }
    }

    // build reusable form with name, loc, contact, username, email, pw
    private JPanel buildForm(String[] labels, JComponent[] fields) {
        JPanel form = new JPanel(new GridLayout(labels.length, 2, 10, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < labels.length; i++) {
            form.add(new JLabel(labels[i] + ":"));
            form.add(fields[i]);
        }

        return form;
    }

    // checks if any required field is blank
    private boolean hasBlankFields(String... values) {
        for (String value : values) {
            if (value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // show error
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // show success
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}