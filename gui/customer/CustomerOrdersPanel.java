package gui.customer;

import dao.FoodBusinessDAO;
import dao.FoodOrderDAO;
import dao.MenuItemDAO;
import dao.OrderItemDAO;

import model.FoodBusiness;
import model.FoodOrder;
import model.MenuItem;
import model.OrderItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerOrdersPanel extends JPanel {
    // customer can see placed orders
    private int customerId;

    private JTable ordersTable;
    private DefaultTableModel ordersModel;

    public CustomerOrdersPanel(int customerId) {
        this.customerId = customerId;

        setLayout(new BorderLayout(10, 10));

        // table model
        ordersModel = new DefaultTableModel(
                new String[] { "Order ID", "Restaurant", "Status", "Items", "Total" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // table
        ordersTable = new JTable(ordersModel);
        ordersTable.setRowHeight(24);

        // buttons
        JButton cancelButton = new JButton("Cancel Order");
        JButton refreshButton = new JButton("Refresh Orders");

        // button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);

        // top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("My Orders"), BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // layout
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        // button actions
        refreshButton.addActionListener(e -> loadOrders());
        cancelButton.addActionListener(e -> confirmCancelOrder());

        loadOrders();
    }

    // load orders and populate table
    public void loadOrders() {
        try {
            ordersModel.setRowCount(0);

            // get all orders for this customer
            List<FoodOrder> orders = new FoodOrderDAO().getByCustomer(customerId);

            for (FoodOrder order : orders) {
                FoodBusiness business = new FoodBusinessDAO().getById(order.getFoodBusinessId());
                List<OrderItem> orderItems = new OrderItemDAO().getByOrder(order.getFoodOrderId());

                StringBuilder itemsText = new StringBuilder();
                double total = 0;

                // build item list and calculate total
                for (OrderItem orderItem : orderItems) {
                    MenuItem menuItem = new MenuItemDAO().getById(orderItem.getMenuItemId());

                    if (menuItem != null) {
                        if (itemsText.length() > 0) {
                            itemsText.append(", ");
                        }

                        itemsText.append(menuItem.getName())
                                .append(" x")
                                .append(orderItem.getQuantity());

                        total += menuItem.getPrice() * orderItem.getQuantity();
                    }
                }

                // add row to table
                ordersModel.addRow(new Object[] {
                        order.getFoodOrderId(),
                        business != null ? business.getName() : "Unknown",
                        formatStatus(order.getOrderStatus()),
                        itemsText.toString(),
                        String.format("$%.2f", total)
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }

    // confirm before cancelling selected order
    private void confirmCancelOrder() {
        int row = ordersTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order first.");
            return;
        }

        String status = ordersModel.getValueAt(row, 2).toString();

        if (status.equals("Cancelled")) {
            JOptionPane.showMessageDialog(
                    this,
                    "This order has already been cancelled.");
            return;
        }

        if (!status.equals("Pending")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Only pending orders can be cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to cancel this order?",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int orderId = (int) ordersModel.getValueAt(row, 0);

            new FoodOrderDAO().updateStatus(orderId, "CANCELLED");

            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error cancelling order.");
        }
    }

    // format database status into readable text
    private String formatStatus(String status) {
        if (status == null) {
            return "";
        }

        String[] words = status.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (word.equals("for")) {
                result.append("for");
            } else {
                result.append(
                        word.substring(0, 1).toUpperCase()
                                + word.substring(1));
            }

            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }
}