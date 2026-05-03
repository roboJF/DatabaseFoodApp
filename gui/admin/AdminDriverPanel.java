package gui.admin;

import dao.AdminManagesDAO;
import dao.DeliveryPersonnelDAO;
import model.DeliveryPersonnel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class AdminDriverPanel extends JPanel {
    // the admin will manage drivers through this panel
    private int adminId;

    // data access objects
    private DeliveryPersonnelDAO deliveryDAO = new DeliveryPersonnelDAO();
    private AdminManagesDAO managesDAO = new AdminManagesDAO();

    private JTable deliveryTable;
    private DefaultTableModel deliveryModel;

    public AdminDriverPanel(int adminId) {
        this.adminId = adminId;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildPanel();
        loadDelivery();
    }

    // build panel, table, buttons
    private void buildPanel() {
        String[] cols = { "ID", "First Name", "Last Name", "Contact", "Vehicle", "Username", "Email" };

        deliveryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        // table
        deliveryTable = new JTable(deliveryModel);
        deliveryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // buttons
        JButton registerButton = new JButton("Register Personnel");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(deliveryTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // button actions
        registerButton.addActionListener(e -> showRegisterDeliveryDialog());
        editButton.addActionListener(e -> showEditDeliveryDialog());
        deleteButton.addActionListener(e -> deleteSelectedDelivery());
        refreshButton.addActionListener(e -> loadDelivery());
    }

    // load delivery personnel
    private void loadDelivery() {
        deliveryModel.setRowCount(0);

        try {
            for (DeliveryPersonnel driver : deliveryDAO.getAll()) {
                deliveryModel.addRow(new Object[] {
                        driver.getDeliveryPersonnelId(),
                        driver.getFirstName(),
                        driver.getLastName(),
                        driver.getContactInfo(),
                        driver.getVehicleDetails(),
                        driver.getUsername(),
                        driver.getEmail()
                });
            }

        } catch (SQLException e) {
            showError("Error loading delivery personnel: " + e.getMessage());
        }
    }

    // register delivery personnel
    private void showRegisterDeliveryDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField vehicleField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel form = buildForm(
                new String[] { "First Name", "Last Name", "Contact Info", "Vehicle Details", "Username", "Email",
                        "Password" },
                new JComponent[] { firstNameField, lastNameField, contactField, vehicleField, usernameField, emailField,
                        passwordField });

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Register New Delivery Personnel",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // clean and validate input fields
        if (result == JOptionPane.OK_OPTION) {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String contact = contactField.getText().trim();
                String vehicle = vehicleField.getText().trim();
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                // check required fields
                if (hasBlankFields(firstName, lastName, contact, vehicle, username, email, password)) {
                    showError("Please fill required fields.");
                    return;
                }

                // check username uniqueness
                if (deliveryDAO.getByUsername(username) != null) {
                    showError("Username already taken.");
                    return;
                }

                deliveryDAO.insert(new DeliveryPersonnel(
                        0,
                        firstName,
                        lastName,
                        contact,
                        vehicle,
                        username,
                        email,
                        password));

                // link admin to this new delivery personnel
                int newId = deliveryDAO.getByUsername(username).getDeliveryPersonnelId();
                managesDAO.assignAdminToDelivery(adminId, newId);

                showSuccess("Delivery personnel registered successfully.");
                loadDelivery();

            } catch (SQLException e) {
                showError("Error registering delivery personnel: " + e.getMessage());
            }
        }
    }

    // edit delivery personnel
    private void showEditDeliveryDialog() {
        int row = deliveryTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a delivery person to edit.");
            return;
        }

        int id = (int) deliveryModel.getValueAt(row, 0);

        try {
            DeliveryPersonnel existing = deliveryDAO.getById(id);

            if (existing == null) {
                showError("Delivery personnel not found.");
                return;
            }

            JTextField firstNameField = new JTextField(existing.getFirstName());
            JTextField lastNameField = new JTextField(existing.getLastName());
            JTextField contactField = new JTextField(existing.getContactInfo());
            JTextField vehicleField = new JTextField(existing.getVehicleDetails());
            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passwordField = new JPasswordField(existing.getPassword());

            JPanel form = buildForm(
                    new String[] { "First Name", "Last Name", "Contact Info", "Vehicle Details", "Username", "Email",
                            "Password" },
                    new JComponent[] { firstNameField, lastNameField, contactField, vehicleField, usernameField,
                            emailField, passwordField });

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Delivery Personnel - " + existing.getFullName(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            // clean and validate updated values
            if (result == JOptionPane.OK_OPTION) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String contact = contactField.getText().trim();
                String vehicle = vehicleField.getText().trim();
                String newUsername = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                // check required fields
                if (hasBlankFields(firstName, lastName, contact, vehicle, newUsername, email, password)) {
                    showError("Please fill required fields.");
                    return;
                }

                // check username if changed
                if (!newUsername.equals(existing.getUsername())) {
                    if (deliveryDAO.getByUsername(newUsername) != null) {
                        showError("Username already taken.");
                        return;
                    }
                }

                existing.setFirstName(firstName);
                existing.setLastName(lastName);
                existing.setContactInfo(contact);
                existing.setVehicleDetails(vehicle);
                existing.setUsername(newUsername);
                existing.setEmail(email);
                existing.setPassword(password);

                deliveryDAO.update(existing);

                showSuccess("Delivery personnel updated successfully.");
                loadDelivery();
            }

        } catch (SQLException e) {
            showError("Error editing delivery personnel: " + e.getMessage());
        }
    }

    // delete delivery personnel
    private void deleteSelectedDelivery() {
        int row = deliveryTable.getSelectedRow();

        if (row == -1) { // if user didnt click a cell
            showError("Please select a delivery person to delete.");
            return;
        }

        int id = (int) deliveryModel.getValueAt(row, 0);
        String name = deliveryModel.getValueAt(row, 1) + " " + deliveryModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                deliveryDAO.delete(id);
                showSuccess("Delivery personnel deleted.");
                loadDelivery();

            } catch (SQLException e) {
                showError("Error deleting delivery personnel: " + e.getMessage());
            }
        }
    }

    // build form
    private JPanel buildForm(String[] labels, JComponent[] fields) {
        JPanel form = new JPanel(new GridLayout(labels.length, 2, 10, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < labels.length; i++) {
            form.add(new JLabel(labels[i] + ":"));
            form.add(fields[i]);
        }

        return form;
    }

    // helper to check if any required field is empty
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