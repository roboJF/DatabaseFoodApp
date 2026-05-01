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

    private int restaurantId;

    private JTable orderTable;
    private DefaultTableModel orderModel;

    private List<FoodOrder> orderList = new ArrayList<>();

    public RestaurantOrdersPanel(int restaurantId) {
        this.restaurantId = restaurantId;

        setLayout(new BorderLayout(10, 10));

        buildPanel();
        loadOrders();
    }

    // build panel
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
        JButton pendingButton = new JButton("Mark Pending");
        JButton readyButton = new JButton("Mark Ready for Pickup");
        JButton refreshButton = new JButton("Refresh Orders");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(pendingButton);
        buttonPanel.add(readyButton);
        buttonPanel.add(refreshButton);

        add(new JScrollPane(orderTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pendingButton.addActionListener(e -> updateSelectedOrderStatus("PENDING"));
        readyButton.addActionListener(e -> updateSelectedOrderStatus("READY"));
        refreshButton.addActionListener(e -> loadOrders());
    }

    // load orders
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

    // format the status string displayed to user
    private String formatStatus(String status) {
        if (status == null)
            return "";

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

    // update selected order status (RESTRICTED)
    private void updateSelectedOrderStatus(String status) {
        int row = orderTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order first.");
            return;
        }

        FoodOrder selectedOrder = orderList.get(row);
        String currentStatus = selectedOrder.getOrderStatus();

        // Only allow changes if still in restaurant control
        if (!currentStatus.equals("PENDING") && !currentStatus.equals("READY")) {
            JOptionPane.showMessageDialog(
                    this,
                    "This order has already been claimed by a driver.\nThe restaurant can no longer change its status.");
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