package gui.admin;

import dao.AdminManagesDAO;
import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class AdminCustomerPanel extends JPanel {

    private int adminId;

    private CustomerDAO customerDAO = new CustomerDAO();
    private AdminManagesDAO managesDAO = new AdminManagesDAO();

    private JTable customerTable;
    private DefaultTableModel customerModel;

    public AdminCustomerPanel(int adminId) {
        this.adminId = adminId;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildPanel();
        loadCustomers();
    }

    // build panel
    private void buildPanel() {
        String[] cols = {"ID", "First Name", "Last Name", "Address", "Contact", "Username", "Email"};

        customerModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        customerTable = new JTable(customerModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton registerButton = new JButton("Register Customer");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(customerTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // button actions
        registerButton.addActionListener(e -> showRegisterCustomerDialog());
        editButton.addActionListener(e -> showEditCustomerDialog());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        refreshButton.addActionListener(e -> loadCustomers());
    }

    // load customers
    private void loadCustomers() {
        customerModel.setRowCount(0);

        try {
            for (Customer customer : customerDAO.getAll()) {
                customerModel.addRow(new Object[]{
                        customer.getCustomerId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getAddress(),
                        customer.getContactInfo(),
                        customer.getUsername(),
                        customer.getEmail()
                });
            }

        } catch (SQLException e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }

    // register customer
    private void showRegisterCustomerDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel form = buildForm(
                new String[]{"First Name", "Last Name", "Address", "Contact Info", "Username", "Email", "Password"},
                new JComponent[]{firstNameField, lastNameField, addressField, contactField, usernameField, emailField, passwordField}
        );

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Register New Customer",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();

                if (customerDAO.getByUsername(username) != null) {
                    showError("Username already taken.");
                    return;
                }

                customerDAO.insert(new Customer(
                        0,
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        addressField.getText().trim(),
                        contactField.getText().trim(),
                        username,
                        emailField.getText().trim(),
                        new String(passwordField.getPassword())
                ));

                int newId = customerDAO.getByUsername(username).getCustomerId();
                managesDAO.assignAdminToCustomer(adminId, newId);

                showSuccess("Customer registered successfully.");
                loadCustomers();

            } catch (SQLException e) {
                showError("Error registering customer: " + e.getMessage());
            }
        }
    }

    // edit customer
    private void showEditCustomerDialog() {
        int row = customerTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a customer to edit.");
            return;
        }

        int id = (int) customerModel.getValueAt(row, 0);

        try {
            Customer existing = customerDAO.getById(id);

            if (existing == null) {
                showError("Customer not found.");
                return;
            }

            JTextField firstNameField = new JTextField(existing.getFirstName());
            JTextField lastNameField = new JTextField(existing.getLastName());
            JTextField addressField = new JTextField(existing.getAddress());
            JTextField contactField = new JTextField(existing.getContactInfo());
            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passwordField = new JPasswordField(existing.getPassword());

            JPanel form = buildForm(
                    new String[]{"First Name", "Last Name", "Address", "Contact Info", "Username", "Email", "Password"},
                    new JComponent[]{firstNameField, lastNameField, addressField, contactField, usernameField, emailField, passwordField}
            );

            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Customer - " + existing.getFullName(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newUsername = usernameField.getText().trim();

                if (!newUsername.equals(existing.getUsername())) {
                    if (customerDAO.getByUsername(newUsername) != null) {
                        showError("Username already taken.");
                        return;
                    }
                }

                existing.setFirstName(firstNameField.getText().trim());
                existing.setLastName(lastNameField.getText().trim());
                existing.setAddress(addressField.getText().trim());
                existing.setContactInfo(contactField.getText().trim());
                existing.setUsername(newUsername);
                existing.setEmail(emailField.getText().trim());
                existing.setPassword(new String(passwordField.getPassword()));

                customerDAO.update(existing);

                showSuccess("Customer updated successfully.");
                loadCustomers();
            }

        } catch (SQLException e) {
            showError("Error editing customer: " + e.getMessage());
        }
    }

    // delete customer
    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();

        if (row == -1) {
            showError("Please select a customer to delete.");
            return;
        }

        int id = (int) customerModel.getValueAt(row, 0);
        String name = customerModel.getValueAt(row, 1) + " " + customerModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete customer: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                customerDAO.delete(id);
                showSuccess("Customer deleted.");
                loadCustomers();

            } catch (SQLException e) {
                showError("Error deleting customer: " + e.getMessage());
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
