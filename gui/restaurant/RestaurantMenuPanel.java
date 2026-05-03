package gui.restaurant;

import dao.MenuItemDAO;
import model.MenuItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuPanel extends JPanel {
    // restaurant can view, edit menu items
    private int restaurantId;

    private JTable availableTable;
    private JTable unavailableTable;

    private DefaultTableModel availableModel;
    private DefaultTableModel unavailableModel;
    // store items separately so selection maps back to correct menuitem
    private List<MenuItem> availableItems = new ArrayList<>();
    private List<MenuItem> unavailableItems = new ArrayList<>();

    public RestaurantMenuPanel(int restaurantId) {
        this.restaurantId = restaurantId;

        setLayout(new BorderLayout(10, 10));

        buildPanel();
        loadMenuItems();
    }

    // builds panel layout with available, unavailable menu items
    private void buildPanel() {
        availableModel = buildModel();
        unavailableModel = buildModel();

        availableTable = new JTable(availableModel);
        unavailableTable = new JTable(unavailableModel);

        JPanel availablePanel = buildAvailablePanel();
        JPanel unavailablePanel = buildUnavailablePanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                availablePanel,
                unavailablePanel);

        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);
    }

    // create table model shared by both tables
    private DefaultTableModel buildModel() {
        return new DefaultTableModel(
                new String[] { "Item", "Description", "Price" },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // panel showing available items, customer can order these
    private JPanel buildAvailablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JLabel("Available Items"), BorderLayout.NORTH);
        panel.add(new JScrollPane(availableTable), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Item");
        JButton editButton = new JButton("Edit Selected");
        JButton markUnavailableButton = new JButton("Mark Unavailable");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(markUnavailableButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // button actions
        addButton.addActionListener(e -> addMenuItem());
        editButton.addActionListener(e -> editSelectedMenuItem(availableTable));
        markUnavailableButton.addActionListener(e -> updateAvailability(false, availableTable));
        refreshButton.addActionListener(e -> loadMenuItems());

        return panel;
    }

    // panel showing unavailable items, customer cant order these
    private JPanel buildUnavailablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.add(new JLabel("Unavailable Items"), BorderLayout.NORTH);
        panel.add(new JScrollPane(unavailableTable), BorderLayout.CENTER);

        JButton editButton = new JButton("Edit Selected");
        JButton markAvailableButton = new JButton("Mark Available");
        JButton refreshButton = new JButton("Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editButton);
        buttonPanel.add(markAvailableButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // button actions
        editButton.addActionListener(e -> editSelectedMenuItem(unavailableTable));
        markAvailableButton.addActionListener(e -> updateAvailability(true, unavailableTable));
        refreshButton.addActionListener(e -> loadMenuItems());

        return panel;
    }

    // fetch menu items from database and split into available/unavailable tables
    private void loadMenuItems() {
        try {
            List<MenuItem> allItems = new MenuItemDAO().getByBusiness(restaurantId);

            availableItems.clear();
            unavailableItems.clear();

            availableModel.setRowCount(0);
            unavailableModel.setRowCount(0);

            for (MenuItem item : allItems) {
                Object[] row = new Object[] {
                        item.getName(),
                        item.getDescription(),
                        String.format("$%.2f", item.getPrice())
                };
                // separate items based on availability flag
                if (item.isAvailable()) {
                    availableItems.add(item);
                    availableModel.addRow(row);
                } else {
                    unavailableItems.add(item);
                    unavailableModel.addRow(row);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items.");
        }
    }

    // return selected menu item based on which table was clicked
    private MenuItem getSelectedItem(JTable table) {
        int row = table.getSelectedRow();

        if (row == -1) {
            return null;
        }

        if (table == availableTable) {
            return availableItems.get(row);
        }

        return unavailableItems.get(row);
    }

    // prompt user for new item info, validate input, insert into database
    private void addMenuItem() {
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JTextField priceField = new JTextField();

        Object[] fields = {
                "Item Name:", nameField,
                "Description:", descriptionField,
                "Price:", priceField
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Add Menu Item",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        // validate fields
        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());

            if (name.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and description cannot be empty.");
                return;
            }

            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative.");
                return;
            }

            MenuItem item = new MenuItem(
                    0,
                    name,
                    description,
                    price,
                    true,
                    restaurantId);

            new MenuItemDAO().insert(item);
            loadMenuItems();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding menu item.");
        }
    }

    // edit existing menu items
    private void editSelectedMenuItem(JTable table) {
        MenuItem selectedItem = getSelectedItem(table);

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        JTextField nameField = new JTextField(selectedItem.getName());
        JTextField descriptionField = new JTextField(selectedItem.getDescription());
        JTextField priceField = new JTextField(String.valueOf(selectedItem.getPrice()));

        Object[] fields = {
                "Item Name:", nameField,
                "Description:", descriptionField,
                "Price:", priceField
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Edit Menu Item",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());

            if (name.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and description cannot be empty.");
                return;
            }

            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative.");
                return;
            }

            MenuItem updatedItem = new MenuItem(
                    selectedItem.getMenuItemId(),
                    name,
                    description,
                    price,
                    selectedItem.isAvailable(),
                    restaurantId);

            new MenuItemDAO().update(updatedItem);
            loadMenuItems();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing menu item.");
        }
    }

    // toggle availability of selected item and refresh tables so menu items can be
    // removed gracefully
    private void updateAvailability(boolean available, JTable table) {
        MenuItem selectedItem = getSelectedItem(table);

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        try {
            new MenuItemDAO().updateAvailability(selectedItem.getMenuItemId(), available);
            loadMenuItems();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating availability.");
        }
    }
}