package gui.admin;

import dao.AdministratorDAO;
import model.Administrator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class AdminAdminPanel extends JPanel {
    // the admin will manage admin accounts through this panel
    private int loggedInAdminId; // admin's own id

    private AdministratorDAO adminDAO = new AdministratorDAO(); // admin data access object

    private JTable adminTable;
    private DefaultTableModel adminModel;

    public AdminAdminPanel(int loggedInAdminId) {
        this.loggedInAdminId = loggedInAdminId;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        buildPanel();
        loadAdmins();
    }

    // build panel
    private void buildPanel() {
        String[] cols = { "ID", "Username", "Email" }; // column headers

        adminModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        adminTable = new JTable(adminModel);
        adminTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // create buttons
        JButton registerButton = new JButton("Register Admin");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        // align buttons left
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        // scrollable list table of admins
        add(new JScrollPane(adminTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // button actions to trigger methods
        registerButton.addActionListener(e -> showRegisterAdminDialog());
        editButton.addActionListener(e -> showEditAdminDialog());
        deleteButton.addActionListener(e -> deleteSelectedAdmin());
        refreshButton.addActionListener(e -> loadAdmins());
    }

    // get the admin accounts
    private void loadAdmins() {
        adminModel.setRowCount(0);

        try {
            for (Administrator admin : adminDAO.getAll()) {
                adminModel.addRow(new Object[] {
                        admin.getAdminId(),
                        admin.getUsername(),
                        admin.getEmail()
                });
            }

        } catch (SQLException e) {
            showError("Error loading admins: " + e.getMessage());
        }
    }

    // register admin
    private void showRegisterAdminDialog() {
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
                new String[] { "Username", "Email", "Password", "" },
                new JComponent[] { usernameField, emailField, passwordField, showPasswordBox });

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Register New Admin",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // clean the inputs then validate
        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());

                // look for empties
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    showError("Please fill required fields.");
                    return;
                }

                // look for already exists
                if (adminDAO.getByUsername(username) != null) {
                    showError("Username already taken.");
                    return;
                }

                adminDAO.insert(new Administrator(0, username, email, password));

                showSuccess("Admin registered successfully.");
                loadAdmins();

            } catch (SQLException e) {
                showError("Error registering admin: " + e.getMessage());
            }
        }
    }

    // edit existing admin
    private void showEditAdminDialog() {
        int row = adminTable.getSelectedRow();

        if (row == -1) {
            showError("Please select an admin to edit.");
            return;
        }

        int id = (int) adminModel.getValueAt(row, 0);

        try {
            Administrator existing = adminDAO.getById(id);

            if (existing == null) {
                showError("Admin not found.");
                return;
            }
            // get their data
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
                    new String[] { "Username", "Email", "Password", "" },
                    new JComponent[] { usernameField, emailField, passwordField, showPasswordBox });

            // verify
            int result = JOptionPane.showConfirmDialog(
                    this,
                    form,
                    "Edit Admin",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newUsername = usernameField.getText().trim();
                // check for any empty fields
                if (newUsername.isEmpty()
                        || emailField.getText().trim().isEmpty()
                        || new String(passwordField.getPassword()).isEmpty()) {
                    showError("Please fill required fields.");
                    return;
                }
                // already exists
                if (!newUsername.equals(existing.getUsername())) {
                    if (adminDAO.getByUsername(newUsername) != null) {
                        showError("Username already taken.");
                        return;
                    }
                }

                // update the field with new data
                existing.setUsername(newUsername);
                existing.setEmail(emailField.getText().trim());
                existing.setPassword(new String(passwordField.getPassword()));

                adminDAO.update(existing);

                showSuccess("Admin updated successfully.");
                loadAdmins();
            }

        } catch (SQLException e) {
            showError("Error editing admin: " + e.getMessage());
        }
    }

    // delete admin
    private void deleteSelectedAdmin() {
        int row = adminTable.getSelectedRow();

        if (row == -1) {
            showError("Please select an admin to delete.");
            return;
        }

        int id = (int) adminModel.getValueAt(row, 0);
        String username = (String) adminModel.getValueAt(row, 1);

        // prevent issues with admin self deletion
        if (id == loggedInAdminId) {
            showError("You cannot delete yourself.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete admin: " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                adminDAO.delete(id);
                showSuccess("Admin deleted.");
                loadAdmins();

            } catch (SQLException e) {
                showError("Error deleting admin: " + e.getMessage());
            }
        }
    }

    // reusable helper form with username, email, password fields
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