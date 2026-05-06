package gui.restaurant;

import dao.FoodOrderDAO;
import dao.CustomerDAO;
import model.FoodOrder;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantOrdersPanel extends JPanel {
    // restaurant can mark order status for customer and driver's view
    // flow: PENDING -> PREPARING -> READY
    // once driver assigned, restaurant cant modify order
    private int restaurantId;

    private JTable orderTable;
    private DefaultTableModel orderModel;

    // store order in same order as table rows
    private List<FoodOrder> orderList = new ArrayList<>();

    public RestaurantOrdersPanel(int restaurantId) {
        this.restaurantId = restaurantId;

        setLayout(new BorderLayout(10, 10));

        buildPanel();
        loadOrders();
    }

    // build main table and action buttons for managing order statuses
    private void buildPanel() {
        orderModel = new DefaultTableModel(
                new String[] {
                        "Order ID",
                        "Customer ID",
                        "Customer Name",
                        "Items",
                        "Total Price",
                        "Driver ID",
                        "Delivery Driver",
                        "Status"
                },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        orderTable.getColumnModel().getColumn(0).setPreferredWidth(60); // Order ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(90); // Customer ID
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Customer Name
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(210); // Items
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(90); // Total Price
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(70); // Driver ID
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(130); // Delivery Driver
        orderTable.getColumnModel().getColumn(7).setPreferredWidth(160); // Status

        // allow restaurant to set order status for customer and driver's view
        JButton cancelButton = new JButton("Cancel Order");
        JButton pendingButton = new JButton("Mark Pending");
        JButton preparingButton = new JButton("Mark Preparing");
        JButton readyButton = new JButton("Mark Ready for Pickup");
        JButton refreshButton = new JButton("Refresh Orders");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(cancelButton);
        buttonPanel.add(pendingButton);
        buttonPanel.add(preparingButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> confirmCancelOrder());
        preparingButton.addActionListener(e -> updateSelectedOrderStatus("PREPARING"));
        pendingButton.addActionListener(e -> updateSelectedOrderStatus("PENDING"));
        readyButton.addActionListener(e -> updateSelectedOrderStatus("READY"));
        refreshButton.addActionListener(e -> loadOrders());
    }

    // gets list of orders for this restaurant, populate table
    private void loadOrders() {
        try {
            orderList = new FoodOrderDAO().getByBusiness(restaurantId);
            orderModel.setRowCount(0);

            FoodOrderDAO orderDAO = new FoodOrderDAO();
            CustomerDAO customerDAO = new CustomerDAO();

            for (FoodOrder order : orderList) {
                String items = orderDAO.getOrderItemsText(order.getFoodOrderId());
                double total = orderDAO.getOrderTotal(order.getFoodOrderId());
                String driver = orderDAO.getDeliveryDriverName(order.getFoodOrderId());

                Customer customer = customerDAO.getById(order.getCustomerId());
                String customerName = customer != null ? customer.getFullName() : "Unknown";

                // put order, customer, driver infos on row
                orderModel.addRow(new Object[] {
                        order.getFoodOrderId(),
                        order.getCustomerId(),
                        customerName,
                        items,
                        String.format("$%.2f", total),
                        order.getDeliveryPersonnelId() == null ? "Not assigned" : order.getDeliveryPersonnelId(),
                        driver,
                        formatStatus(order.getOrderStatus())
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.");
        }
    }

    // confirm before cancelling selected order
    private void confirmCancelOrder() {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Select an order first.");
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

        updateSelectedOrderStatus("CANCELLED");
    }

    // format the status string displayed to user
    private String formatStatus(String status) {
        if (status == null)
            return "";
        // make it more readable
        String[] words = status.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            // capitalize first letter
            word = word.substring(0, 1).toUpperCase() + word.substring(1);

            result.append(word);

            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    // update selected order status
    private void updateSelectedOrderStatus(String status) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        FoodOrder selectedOrder = orderList.get(row);
        String currentStatus = selectedOrder.getOrderStatus();

        // cancellation flow:
        // PENDING -> CANCELLED

        if (status.equals("CANCELLED")
                && !currentStatus.equals("PENDING")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only pending orders can be cancelled.");

            return;
        }

        // cancelled orders cannot be modified

        if (currentStatus.equals("CANCELLED")) {

            JOptionPane.showMessageDialog(
                    this,
                    "This order has already been cancelled.");

            return;
        }
        // only allow changes if still in restaurant control
        // cant change after driver assigned
        if (!currentStatus.equals("PENDING")
                && !currentStatus.equals("PREPARING")
                && !currentStatus.equals("READY")) {
            JOptionPane.showMessageDialog(
                    this,
                    "This order has already been claimed by a driver.\nThe restaurant can no longer change its status.");
            return;
        }

        // enforce restaurant order flow, but can change for status mistake
        if (status.equals("PREPARING")
                && !currentStatus.equals("PENDING")
                && !currentStatus.equals("READY")) {
            JOptionPane.showMessageDialog(this, "Order must be pending or ready before it can be marked preparing.");
            return;
        }

        if (status.equals("READY") && !currentStatus.equals("PREPARING")) {
            JOptionPane.showMessageDialog(this, "Order must be preparing before it can be marked ready.");
            return;
        }

        if (status.equals("PENDING") && !currentStatus.equals("PREPARING")) {
            JOptionPane.showMessageDialog(this, "Only preparing orders can be moved back to pending.");
            return;
        }

        try {
            new FoodOrderDAO().updateStatus(selectedOrder.getFoodOrderId(), status);
            loadOrders();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating order status.");
        }
    }
}