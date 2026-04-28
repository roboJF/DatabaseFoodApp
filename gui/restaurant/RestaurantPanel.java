package gui.restaurant;

import dao.MenuItemDAO;
import dao.FoodOrderDAO;

import model.MenuItem;
import model.FoodOrder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class RestaurantPanel extends JPanel {

    private int restaurantId;

    private JTable menuTable;
    private JTable orderTable;

    private DefaultTableModel menuModel;
    private DefaultTableModel orderModel;

    private List<MenuItem> menuItemList = new ArrayList<>();
    private List<FoodOrder> orderList = new ArrayList<>();

    public RestaurantPanel(int restaurantId) {
        this.restaurantId = restaurantId;

        setLayout(new BorderLayout(10, 10));

        // menu table
        menuModel = new DefaultTableModel(new String[] { "Item", "Description", "Price", "Available" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        menuTable = new JTable(menuModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel menuPanel = new JPanel(new BorderLayout(5, 5));
        menuPanel.add(new JLabel("Menu Items"), BorderLayout.NORTH);
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        // menu buttons
        JPanel menuButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addMenuItemButton = new JButton("Add Item");
        JButton editMenuItemButton = new JButton("Edit Selected");
        JButton removeMenuItemButton = new JButton("Remove Selected");
        JButton availableButton = new JButton("Mark Available");
        JButton unavailableButton = new JButton("Mark Unavailable");
        JButton refreshMenuButton = new JButton("Refresh Menu");
        JButton editInfoButton = new JButton("Edit Restaurant Info");

        menuButtonPanel.add(addMenuItemButton);
        menuButtonPanel.add(editMenuItemButton);
        menuButtonPanel.add(removeMenuItemButton);
        menuButtonPanel.add(availableButton);
        menuButtonPanel.add(unavailableButton);
        menuButtonPanel.add(refreshMenuButton);
        menuButtonPanel.add(editInfoButton);

        menuPanel.add(menuButtonPanel, BorderLayout.SOUTH);

        // order table
        orderModel = new DefaultTableModel(new String[] { "Order ID", "Customer ID", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel orderPanel = new JPanel(new BorderLayout(5, 5));
        orderPanel.add(new JLabel("Incoming Orders"), BorderLayout.NORTH);
        orderPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // order buttons
        JPanel orderButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton pendingButton = new JButton("Mark Pending");
        JButton readyButton = new JButton("Mark Ready for Pickup");
        JButton outForDeliveryButton = new JButton("Mark Out for Delivery");
        JButton completeButton = new JButton("Mark Complete");
        JButton refreshOrdersButton = new JButton("Refresh Orders");

        orderButtonPanel.add(pendingButton);
        orderButtonPanel.add(readyButton);
        orderButtonPanel.add(outForDeliveryButton);
        orderButtonPanel.add(completeButton);
        orderButtonPanel.add(refreshOrdersButton);

        orderPanel.add(orderButtonPanel, BorderLayout.SOUTH);

        // main layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuPanel, orderPanel);
        splitPane.setResizeWeight(0.55);

        add(splitPane, BorderLayout.CENTER);

        // menu actions
        addMenuItemButton.addActionListener(e -> addMenuItem());
        editMenuItemButton.addActionListener(e -> editSelectedMenuItem());
        removeMenuItemButton.addActionListener(e -> removeSelectedMenuItem());
        availableButton.addActionListener(e -> updateSelectedMenuItemAvailability(true));
        unavailableButton.addActionListener(e -> updateSelectedMenuItemAvailability(false));
        refreshMenuButton.addActionListener(e -> loadMenuItems());

        // order actions
        pendingButton.addActionListener(e -> updateSelectedOrderStatus("PENDING"));
        readyButton.addActionListener(e -> updateSelectedOrderStatus("READY"));
        outForDeliveryButton.addActionListener(e -> updateSelectedOrderStatus("OUT_FOR_DELIVERY"));
        completeButton.addActionListener(e -> updateSelectedOrderStatus("COMPLETE"));
        refreshOrdersButton.addActionListener(e -> loadOrders());
        editInfoButton.addActionListener(e -> showRestaurantInfo());

        loadMenuItems();
        loadOrders();
    }

    // load menu items
    private void loadMenuItems() {
        try {
            menuItemList = new MenuItemDAO().getByBusiness(restaurantId);
            menuModel.setRowCount(0);

            for (MenuItem item : menuItemList) {
                menuModel.addRow(new Object[] {
                        item.getName(),
                        item.getDescription(),
                        String.format("$%.2f", item.getPrice()),
                        item.isAvailable()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items.");
        }
    }

    // load orders
    private void loadOrders() {
        try {
            orderList = new FoodOrderDAO().getByBusiness(restaurantId);
            orderModel.setRowCount(0);

            for (FoodOrder order : orderList) {
                orderModel.addRow(new Object[] {
                        order.getFoodOrderId(),
                        order.getCustomerId(),
                        order.getOrderStatus()
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }

    // add menu item
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
                JOptionPane.OK_CANCEL_OPTION
        );

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

            MenuItem item = new MenuItem(0, name, description, price, true, restaurantId);
            new MenuItemDAO().insert(item);

            loadMenuItems();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding menu item.");
        }
    }

    // edit selected menu item
    private void editSelectedMenuItem() {
        int row = menuTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        MenuItem selectedItem = menuItemList.get(row);

        JTextField nameField = new JTextField(selectedItem.getName());
        JTextField descriptionField = new JTextField(selectedItem.getDescription());
        JTextField priceField = new JTextField(String.valueOf(selectedItem.getPrice()));
        JCheckBox availableBox = new JCheckBox("Available", selectedItem.isAvailable());

        Object[] fields = {
                "Item Name:", nameField,
                "Description:", descriptionField,
                "Price:", priceField,
                availableBox
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Edit Menu Item",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            boolean available = availableBox.isSelected();

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
                    available,
                    restaurantId
            );

            new MenuItemDAO().update(updatedItem);

            loadMenuItems();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing menu item.");
        }
    }

    // remove selected menu item
    private void removeSelectedMenuItem() {
        int row = menuTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        MenuItem selectedItem = menuItemList.get(row);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this item?",
                "Remove Menu Item",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            new MenuItemDAO().delete(selectedItem.getMenuItemId());
            loadMenuItems();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing menu item.");
        }
    }

    // update selected menu item availability
    private void updateSelectedMenuItemAvailability(boolean available) {
        int row = menuTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        MenuItem selectedItem = menuItemList.get(row);

        try {
            new MenuItemDAO().updateAvailability(selectedItem.getMenuItemId(), available);
            loadMenuItems();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating item availability.");
        }
    }

    // update selected order status
    private void updateSelectedOrderStatus(String status) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        FoodOrder selectedOrder = orderList.get(row);

        try {
            new FoodOrderDAO().updateStatus(selectedOrder.getFoodOrderId(), status);
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating order status.");
        }
    }
    //show restaurant info
    private void showRestaurantInfo() {
        JDialog dialog = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Edit Restaurant Info",
                true
        );

        dialog.setContentPane(new RestaurantInfoPanel(restaurantId));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}