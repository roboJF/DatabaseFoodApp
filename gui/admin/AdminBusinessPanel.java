package gui.admin;

import dao.AdminManagesDAO;
import dao.FoodBusinessDAO;
import model.FoodBusiness;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class AdminBusinessPanel extends JPanel {

    private int adminId;

    private FoodBusinessDAO businessDAO = new FoodBusinessDAO();
    private AdminManagesDAO managesDAO = new AdminManagesDAO();

    private JTable businessTable;
    private DefaultTableModel businessModel;

    public AdminBusinessPanel(int adminId) {
        this.adminId = adminId;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildPanel();
        loadBusinesses();
    }

    // build panel
    private void buildPanel() {
        String[] cols = {"ID", "Name", "Location", "Contact", "Username", "Email"};

        businessModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        businessTable = new JTable(businessModel);
        businessTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton registerButton = new JButton("Register Business");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(businessTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // button actions
        registerButton.addActionListener(e -> showRegisterBusinessDialog());
        editButton.addActionListener(e -> showEditBusinessDialog());
        deleteButton.addActionListener(e -> deleteSelectedBusiness());
        refreshButton.addActionListener(e -> loadBusinesses());
    }

    // load businesses
    private void loadBusinesses() {
        businessModel.setRowCount(0);

        try {
            for (FoodBusiness business : businessDAO.getAll()) {
                businessModel.addRow(new Object[]{
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

    // register business
    private void showRegisterBusinessDialog() {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel form = buildForm(
                new String[]{"Business Name", "Location", "Contact Info", "Username", "Email", "Password"},
                new JComponent[]{nameField, locationField, contactField, usernameField, emailField, passwordField}
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Register New Business",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();

                if (businessDAO.getByUsername(username) != null) {
                    showError("Username already taken.");
                    return;
                }

                businessDAO.insert(new FoodBusiness(
                        0,
                        nameField.getText().trim(),
                        locationField.getText().trim(),
                        contactField.getText().trim(),
                        username,
                        emailField.getText().trim(),
                        new String(passwordField.getPassword())
                ));

                int newId = businessDAO.getByUsername(username).getFoodBusinessId();
                managesDAO.assignAdminToBusiness(adminId, newId);

                showSuccess("Business registered successfully.");
                loadBusinesses();

            } catch (SQLException e) {
                showError("Error registering business: " + e.getMessage());
            }
        }
    }

    // edit business
    private void showEditBusinessDialog() {
        int row = businessTable.getSelectedRow();

        if (row == -1) {
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
            JTextField contactField = new JTextField(existing.getContactInfo());
            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passwordField = new JPasswordField(existing.getPassword());

            JPanel form = buildForm(
                    new String[]{"Business Name", "Location", "Contact Info", "Username", "Email", "Password"},
                    new JComponent[]{nameField, locationField, contactField, usernameField, emailField, passwordField}
            );

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Business - " + existing.getName(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newUsername = usernameField.getText().trim();

                if (!newUsername.equals(existing.getUsername())) {
                    if (businessDAO.getByUsername(newUsername) != null) {
                        showError("Username already taken.");
                        return;
                    }
                }

                existing.setName(nameField.getText().trim());
                existing.setLocation(locationField.getText().trim());
                existing.setContactInfo(contactField.getText().trim());
                existing.setUsername(newUsername);
                existing.setEmail(emailField.getText().trim());
                existing.setPassword(new String(passwordField.getPassword()));

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
                JOptionPane.WARNING_MESSAGE
        );

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

    // show error
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // show success
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
