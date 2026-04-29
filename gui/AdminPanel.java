package gui;

import dao.*;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

//note, i've never done anything with swing so if this sucks, sorry!
public class AdminPanel extends JPanel {

    private MainFrame mainFrame;
    private int adminId;

    private AdministratorDAO adminDAO = new AdministratorDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private FoodBusinessDAO businessDAO = new FoodBusinessDAO();
    private DeliveryPersonnelDAO deliveryDAO = new DeliveryPersonnelDAO();
    private AdminManagesDAO managesDAO = new AdminManagesDAO();

    private JTable customerTable, businessTable, deliveryTable;
    private DefaultTableModel customerModel, businessModel, deliveryModel;

    public AdminPanel(MainFrame mainFrame, int adminId) {
        this.mainFrame = mainFrame;
        this.adminId = adminId;

        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(), BorderLayout.CENTER);

        loadCustomers();
        loadBusinesses();
        loadDelivery();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel welcomeLabel = new JLabel("  Logged in as: " + getAdminUsername());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> mainFrame.showLoginPanel());

        header.add(title, BorderLayout.WEST);
        header.add(welcomeLabel, BorderLayout.CENTER);
        header.add(logoutBtn, BorderLayout.EAST);

        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Customers", buildCustomerTab());
        tabs.addTab("Food Businesses", buildBusinessTab());
        tabs.addTab("Delivery Personnel", buildDeliveryTab());

        return tabs;
    }

    //----------CUSTOMERS----------

    private JPanel buildCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "First Name", "Last Name", "Address", "Contact", "Username", "Email"};
        customerModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        customerTable = new JTable(customerModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(customerTable);

        JButton registerBtn = new JButton("Register Customer");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        registerBtn.addActionListener(e -> showRegisterCustomerDialog());
        editBtn.addActionListener(e -> showEditCustomerDialog());
        deleteBtn.addActionListener(e -> deleteSelectedCustomer());
        refreshBtn.addActionListener(e -> loadCustomers());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(registerBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    //----------BUSINESSES----------

    private JPanel buildBusinessTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Name", "Location", "Contact", "Username", "Email"};
        businessModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        businessTable = new JTable(businessModel);
        businessTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(businessTable);

        JButton registerBtn = new JButton("Register Business");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        registerBtn.addActionListener(e -> showRegisterBusinessDialog());
        editBtn.addActionListener(e -> showEditBusinessDialog());
        deleteBtn.addActionListener(e -> deleteSelectedBusiness());
        refreshBtn.addActionListener(e -> loadBusinesses());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(registerBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    //----------DELIVERY----------

    private JPanel buildDeliveryTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "First Name", "Last Name", "Contact", "Vehicle", "Username", "Email"};
        deliveryModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        deliveryTable = new JTable(deliveryModel);
        deliveryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(deliveryTable);

        JButton registerBtn = new JButton("Register Personnel");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton refreshBtn = new JButton("Refresh");

        registerBtn.addActionListener(e -> showRegisterDeliveryDialog());
        editBtn.addActionListener(e -> showEditDeliveryDialog());
        deleteBtn.addActionListener(e -> deleteSelectedDelivery());
        refreshBtn.addActionListener(e -> loadDelivery());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(registerBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadCustomers() {
        customerModel.setRowCount(0);
        try {
            for (Customer c : customerDAO.getAll()) {
                customerModel.addRow(new Object[]{
                    c.getCustomerId(), c.getFirstName(), c.getLastName(),
                    c.getAddress(), c.getContactInfo(), c.getUsername(), c.getEmail()
                });
            }
        } catch (SQLException e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }

    private void loadBusinesses() {
        businessModel.setRowCount(0);
        try {
            for (FoodBusiness b : businessDAO.getAll()) {
                businessModel.addRow(new Object[]{
                    b.getFoodBusinessId(), b.getName(), b.getLocation(),
                    b.getContactInfo(), b.getUsername(), b.getEmail()
                });
            }
        } catch (SQLException e) {
            showError("Error loading businesses: " + e.getMessage());
        }
    }

    private void loadDelivery() {
        deliveryModel.setRowCount(0);
        try {
            for (DeliveryPersonnel d : deliveryDAO.getAll()) {
                deliveryModel.addRow(new Object[]{
                    d.getDeliveryPersonnelId(), d.getFirstName(), d.getLastName(),
                    d.getContactInfo(), d.getVehicleDetails(), d.getUsername(), d.getEmail()
                });
            }
        } catch (SQLException e) {
            showError("Error loading delivery personnel: " + e.getMessage());
        }
    }

    private void showRegisterCustomerDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JPanel form = buildForm(
            new String[]{"First Name", "Last Name", "Address", "Contact Info", "Username", "Email", "Password"},
            new JComponent[]{firstNameField, lastNameField, addressField, contactField, usernameField, emailField, passField}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Register New Customer",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (customerDAO.getByUsername(usernameField.getText().trim()) != null) {
                    showError("Username already taken.");
                    return;
                }
                customerDAO.insert(new Customer(
                    0,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    new String(passField.getPassword())
                ));

                int newId = customerDAO.getByUsername(usernameField.getText().trim()).getCustomerId();
                managesDAO.assignAdminToCustomer(adminId, newId);

                showSuccess("Customer registered successfully.");
                loadCustomers();
            } catch (SQLException e) {
                showError("Error registering customer: " + e.getMessage());
            }
        }
    }

    private void showRegisterBusinessDialog() {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JPanel form = buildForm(
            new String[]{"Business Name", "Location", "Contact Info", "Username", "Email", "Password"},
            new JComponent[]{nameField, locationField, contactField, usernameField, emailField, passField}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Register New Business",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (businessDAO.getByUsername(usernameField.getText().trim()) != null) {
                    showError("Username already taken.");
                    return;
                }
                businessDAO.insert(new FoodBusiness(
                    0,
                    nameField.getText().trim(),
                    locationField.getText().trim(),
                    contactField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    new String(passField.getPassword())
                ));

                int newId = businessDAO.getByUsername(usernameField.getText().trim()).getFoodBusinessId();
                managesDAO.assignAdminToBusiness(adminId, newId);

                showSuccess("Business registered successfully.");
                loadBusinesses();
            } catch (SQLException e) {
                showError("Error registering business: " + e.getMessage());
            }
        }
    }

    private void showRegisterDeliveryDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField vehicleField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JPanel form = buildForm(
            new String[]{"First Name", "Last Name", "Contact Info", "Vehicle Details", "Username", "Email", "Password"},
            new JComponent[]{firstNameField, lastNameField, contactField, vehicleField, usernameField, emailField, passField}
        );

        int result = JOptionPane.showConfirmDialog(
            this, form, "Register New Delivery Personnel",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (deliveryDAO.getByUsername(usernameField.getText().trim()) != null) {
                    showError("Username already taken.");
                    return;
                }
                deliveryDAO.insert(new DeliveryPersonnel(
                    0,
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    contactField.getText().trim(),
                    vehicleField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    new String(passField.getPassword())
                ));

                int newId = deliveryDAO.getByUsername(usernameField.getText().trim()).getDeliveryPersonnelId();
                managesDAO.assignAdminToDelivery(adminId, newId);

                showSuccess("Delivery personnel registered successfully.");
                loadDelivery();
            } catch (SQLException e) {
                showError("Error registering delivery personnel: " + e.getMessage());
            }
        }
    }

    //----------EDITING STUFF (great, right?)----------

    private void showEditCustomerDialog() {
        int row = customerTable.getSelectedRow();
        if (row == -1) { showError("Please select a customer to edit."); return; }

        int id = (int) customerModel.getValueAt(row, 0);

        try {
            Customer existing = customerDAO.getById(id);
            if (existing == null) { showError("Customer not found."); return; }

            JTextField firstNameField = new JTextField(existing.getFirstName());
            JTextField lastNameField = new JTextField(existing.getLastName());
            JTextField addressField = new JTextField(existing.getAddress());
            JTextField contactField = new JTextField(existing.getContactInfo());
            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passField = new JPasswordField(existing.getPassword());

            JPanel form = buildForm(
                new String[]{"First Name", "Last Name", "Address", "Contact Info", "Username", "Email", "Password"},
                new JComponent[]{firstNameField, lastNameField, addressField, contactField, usernameField, emailField, passField}
            );

            int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Customer — " + existing.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
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
                existing.setPassword(new String(passField.getPassword()));

                customerDAO.update(existing);
                showSuccess("Customer updated successfully.");
                loadCustomers();
            }
        } catch (SQLException e) {
            showError("Error editing customer: " + e.getMessage());
        }
    }

    private void showEditBusinessDialog() {
        int row = businessTable.getSelectedRow();
        if (row == -1) { showError("Please select a business to edit."); return; }

        int id = (int) businessModel.getValueAt(row, 0);

        try {
            FoodBusiness existing = businessDAO.getById(id);
            if (existing == null) { showError("Business not found."); return; }

            JTextField nameField = new JTextField(existing.getName());
            JTextField locationField = new JTextField(existing.getLocation());
            JTextField contactField = new JTextField(existing.getContactInfo());
            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passField = new JPasswordField(existing.getPassword());

            JPanel form = buildForm(
                new String[]{"Business Name", "Location", "Contact Info", "Username", "Email", "Password"},
                new JComponent[]{nameField, locationField, contactField, usernameField, emailField, passField}
            );

            int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Business — " + existing.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
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
                existing.setPassword(new String(passField.getPassword()));

                businessDAO.update(existing);
                showSuccess("Business updated successfully.");
                loadBusinesses();
            }
        } catch (SQLException e) {
            showError("Error editing business: " + e.getMessage());
        }
    }

    private void showEditDeliveryDialog() {
        int row = deliveryTable.getSelectedRow();
        if (row == -1) { showError("Please select a delivery person to edit."); return; }

        int id = (int) deliveryModel.getValueAt(row, 0);

        try {
            DeliveryPersonnel existing = deliveryDAO.getById(id);
            if (existing == null) { showError("Delivery personnel not found."); return; }

            JTextField firstNameField = new JTextField(existing.getFirstName());
            JTextField lastNameField = new JTextField(existing.getLastName());
            JTextField contactField = new JTextField(existing.getContactInfo());
            JTextField vehicleField = new JTextField(existing.getVehicleDetails());
            JTextField usernameField = new JTextField(existing.getUsername());
            JTextField emailField = new JTextField(existing.getEmail());
            JPasswordField passField = new JPasswordField(existing.getPassword());

            JPanel form = buildForm(
                new String[]{"First Name", "Last Name", "Contact Info", "Vehicle Details", "Username", "Email", "Password"},
                new JComponent[]{firstNameField, lastNameField, contactField, vehicleField, usernameField, emailField, passField}
            );

            int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Delivery Personnel — " + existing.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String newUsername = usernameField.getText().trim();
                if (!newUsername.equals(existing.getUsername())) {
                    if (deliveryDAO.getByUsername(newUsername) != null) {
                        showError("Username already taken.");
                        return;
                    }
                }

                existing.setFirstName(firstNameField.getText().trim());
                existing.setLastName(lastNameField.getText().trim());
                existing.setContactInfo(contactField.getText().trim());
                existing.setVehicleDetails(vehicleField.getText().trim());
                existing.setUsername(newUsername);
                existing.setEmail(emailField.getText().trim());
                existing.setPassword(new String(passField.getPassword()));

                deliveryDAO.update(existing);
                showSuccess("Delivery personnel updated successfully.");
                loadDelivery();
            }
        } catch (SQLException e) {
            showError("Error editing delivery personnel: " + e.getMessage());
        }
    }

    //----------DELETION STUFF----------

    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row == -1) { showError("Please select a customer to delete."); return; }

        int id = (int) customerModel.getValueAt(row, 0);
        String name = customerModel.getValueAt(row, 1) + " " + customerModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to delete customer: " + name + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
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

    private void deleteSelectedBusiness() {
        int row = businessTable.getSelectedRow();
        if (row == -1) { showError("Please select a business to delete."); return; }

        int id = (int)    businessModel.getValueAt(row, 0);
        String name = (String) businessModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to delete business: " + name + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
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

    private void deleteSelectedDelivery() {
        int row = deliveryTable.getSelectedRow();
        if (row == -1) { showError("Please select a delivery person to delete."); return; }

        int id = (int) deliveryModel.getValueAt(row, 0);
        String name = deliveryModel.getValueAt(row, 1) + " " + deliveryModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to delete: " + name + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                deliveryDAO.delete(id);
                showSuccess("Delivery personnel deleted.");
                loadDelivery();
            } catch (SQLException e) {
                showError("Error deleting: " + e.getMessage());
            }
        }
    }

    //----------RANDOM HELPERS I NEEDED FOR WHATEVER REASON!----------

    private JPanel buildForm(String[] labels, JComponent[] fields) {
        JPanel form = new JPanel(new GridLayout(labels.length, 2, 10, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < labels.length; i++) {
            form.add(new JLabel(labels[i] + ":"));
            form.add(fields[i]);
        }
        return form;
    }

    private String getAdminUsername() {
        try {
            Administrator admin = adminDAO.getById(adminId);
            return admin != null ? admin.getUsername() : "Unknown";
        } catch (SQLException e) {
            return "Unknown";
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}